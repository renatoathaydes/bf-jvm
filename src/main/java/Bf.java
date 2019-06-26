import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;


final class Bf {

    interface Op extends Consumer<Tape> {
    }

    static final class LoopOp implements Op {
        final Op[] loop;

        LoopOp( Op[] loop ) {
            this.loop = loop;
        }

        @Override
        public void accept( Tape tape ) {
            while ( tape.get() > 0 ) for ( Op op : loop ) {
                op.accept( tape );
            }
        }
    }

    static final class PrintOp implements Op {
        @Override
        public void accept( Tape tape ) {
            System.out.print( ( char ) tape.get() );
        }
    }

    static final class Tape {
        private int[] tape;
        private int pos;

        Tape() {
            tape = new int[ 1 ];
        }

        int get() {
            return tape[ pos ];
        }

        void inc( int x ) {
            tape[ pos ] += x;
        }

        void move( int x ) {
            pos += x;
            while ( pos >= tape.length ) {
                int[] tape = new int[ this.tape.length * 2 ];
                System.arraycopy( this.tape, 0, tape, 0, this.tape.length );
                this.tape = tape;
            }

        }
    }

    static final class Program {
        static final Op INCR = tape -> tape.inc( 1 );
        static final Op DECR = tape -> tape.inc( -1 );
        static final Op MV_UP = tape -> tape.move( 1 );
        static final Op MV_DOWN = tape -> tape.move( -1 );
        static final Op PRINT = new PrintOp();

        private final Op[] ops;

        Program( String code ) {
            ops = parse( code.chars().iterator() );
        }

        private Op[] parse( PrimitiveIterator.OfInt it ) {
            List<Op> res = new ArrayList<>();
            while ( it.hasNext() ) {
                switch ( it.nextInt() ) {
                    case '+':
                        res.add( INCR );
                        break;
                    case '-':
                        res.add( DECR );
                        break;
                    case '>':
                        res.add( MV_UP );
                        break;
                    case '<':
                        res.add( MV_DOWN );
                        break;
                    case '.':
                        res.add( PRINT );
                        break;
                    case '[':
                        res.add( new LoopOp( parse( it ) ) );
                        break;
                    case ']':
                        return res.toArray( new Op[ 0 ] );
                }
            }
            return res.toArray( new Op[ 0 ] );
        }

        void run() {
            Tape tape = new Tape();
            for ( Op op : ops ) op.accept( tape );
        }

    }

    public static void main( String[] args ) throws IOException {
        byte[] code = Files.readAllBytes( Paths.get( args[ 0 ] ) );

        long startTime = System.currentTimeMillis();
        Program program = new Program( new String( code, StandardCharsets.US_ASCII ) );
        program.run();
        System.err.printf( "time: %.3fs\n", ( System.currentTimeMillis() - startTime ) / 1e3 );
    }
}
