import javax.swing.plaf.TableHeaderUI;

class MiRun implements Runnable {
  int miId;
  public MiRun( int miId ) {
    this.miId = miId;
  }
  public void run() {
    for( int i = 0; i < 1000; i++ ) {
      System.out.println( "Hebra: " + miId );
    }
  }
}
class EjemploCreacionRunnable {
  public static void main( String args[] ) {
    new Thread( new MiRun( 0 ) ).run();
    new Thread( new MiRun( 1 ) ).run();
  }
}
