class MiHebraVirtual extends Thread { // (A)
  // ... (B)
  int miId;
  int num1;
  int num2;
  public MiHebraVirtual( int miId, int num1, int num2 ) {
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

class EjemploVirtual {
  public static void main( String args[] ) {
    System.out.println( "Hebra Principal inicia" );
    // Crea y arranca hebra t0 sumando desde 1 hasta 1000000 
    // Crea y arranca hebra t1 sumando desde 1 hasta 1000000 
    // ... (D)

      Thread t0 = MiHebraVirtual.startVirtualThread(new MiHebraVirtual(0, 1, 1000000));
      Thread t1 = MiHebraVirtual.startVirtualThread (new MiHebraVirtual (1, 1, 1000000) ) ;


    // Espera la finalizacion de las hebras t0 y t1
    // ... (E)
      //try {
        //  t0.join ();
          //t1.join();
      //} catch ( InterruptedException ex ) {
        //  ex.printStackTrace () ;
      //}
    System.out.println( "Hebra Principal finaliza" );
  }
}

