package bits.microtime;


/**
 * Concurrent data structure for tick() call dispatch.
 * Does not guarantee order of elements.
 *
 * @author Philip DeCamp
 */
public class TickCaster implements Ticker {

    public static Ticker add( Ticker a, Ticker b ) {
        if( a == null ) return b;
        if( b == null ) return a;

        if( a instanceof TickCaster ) {
            if( b instanceof TickCaster ) {
                // Reconstruct B to point at A.
                TickCaster head = (TickCaster)a;
                TickCaster tail = (TickCaster)b;

                do {
                    head = new TickCaster( head, tail.mListener );
                    tail = tail.mNext;
                } while( tail != null );

                return head;
            }

            return new TickCaster( (TickCaster)a, b );
        }

        if( b instanceof TickCaster ) {
            return new TickCaster( (TickCaster)b, a );
        }

        // Both need new GluiMulticaster to wrap them.
        return new TickCaster( new TickCaster( null, b ), a );

    }

    public static Ticker remove( Ticker caster, Ticker old ) {
        if( caster == null || old == null ) {
            return caster;
        }

        if( caster instanceof TickCaster ) {
            TickCaster head = (TickCaster)caster;
            TickCaster pos  = head;

            // Find link to remove.
            while( pos != null && pos.mListener != old ) {
                pos = pos.mNext;
            }

            if( pos == null ) {
                // Not found.
                return head;
            }

            if( pos == head ) {
                // Remove the head.
                head = head.mNext;

                // Check if remaining sequence contains single listener.
                if( head != null && head.mNext == null ) {
                    // Remaining sequence contains single listener,
                    // which may be returned directly.
                    return head.mListener;
                }

                return head;
            }

            // Reconstruct chain without pos link.
            TickCaster tail = pos.mNext;

            if( head.mNext == pos && tail == null ) {
                // head is the only link.
                return head.mListener;
            }

            // Reconstruct head--pos subsequence.
            do {
                tail = new TickCaster( tail, head.mListener );
                head = head.mNext;
            } while( head != pos );

            return tail;
        }

        if( caster == old ) {
            // Remove single listener.
            return null;
        }

        // Not found.
        return caster;
    }


    private final TickCaster mNext;
    private final Ticker     mListener;

    protected TickCaster( TickCaster next, Ticker listener ) {
        mNext = next;
        mListener = listener;
    }

    @Override
    public void tick() {
        TickCaster c = this;
        do {
            c.mListener.tick();
            c = c.mNext;
        } while( c != null );
    }

}
