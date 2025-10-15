class MiHebraDaemon extends Thread { // (A)
  // ... (B)
  int miId;
  int num1;
  int num2;
  public MiHebraDaemon( int miId, int num1, int num2 ) {
    // ... (C)
    this.miId = miId;
    this.num1 = num1;
    this.num2 = num2;
  }

  public void run() {
    long suma = 0;

    System.out.println( "Hebra Auxiliar " + miId + " , inicia calculo" );
    for( int i = num1; i <= num2 ; i++ ) {
      suma += (long) i;
    }
    System.out.println( "Hebra Auxiliar " + miId + " , suma: " + suma);
  }

}

class EjemploDaemon {
  public static void main( String args[] ) {
    System.out.println( "Hebra Principal inicia" );
    // Crea y arranca hebra t0 sumando desde 1 hasta 1000000 
    // Crea y arranca hebra t1 sumando desde 1 hasta 1000000 
    // ... (D)
      //Sin Daemon
      //new MiHebraDaemon( 0, 1, 1000000 ).start();
      //new MiHebraDaemon( 1, 1, 1000000 ).start();

      //Con Daemon
      MiHebraDaemon t0 = new MiHebraDaemon (0, 1, 1000000) ;
      MiHebraDaemon t1 = new MiHebraDaemon (1, 1, 1000000) ;
      t0.setDaemon( true ) ;
      t0.start() ;
      t1.setDaemon( true ) ;
      t1.start() ;

    // Espera la finalizacion de las hebras t0 y t1
    // ... (E)
      try {
          t0.join ();
          t1.join();
      } catch ( InterruptedException ex ) {
          ex.printStackTrace () ;
      }
    System.out.println( "Hebra Principal finaliza" );
  }
}

