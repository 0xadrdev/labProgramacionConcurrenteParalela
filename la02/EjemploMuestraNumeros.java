import java.net.SocketOption;

// ============================================================================
class EjemploMuestraNumeros {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  n, numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <n>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt(args[0]);
      n         = Integer.parseInt( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( n <= 0 ) ) {
        System.err.print( "Uso: [ java programa <numHebras> <n> ] " );
        System.err.println( "donde ( numHebras > 0 )  y ( n > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    //
    // Implementacion paralela con distribucion ciclica o por bloques.
    //
    // Crea un vector de hebras. Crea y arranca las hebras
    // (A) ...

      HebraDistBloques vectorHebras[] = new HebraDistBloques[numHebras];

      for( int i = 0; i < numHebras; i++ ) {
          vectorHebras[i] = new HebraDistBloques(i, numHebras, n);
          vectorHebras[i].start();
      }

      // Espera a que terminen todas las hebras.
      // (B) ...
      //
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorHebras[ i ].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }
  }
}

// Crea las clases adicionales que sean necesarias
// (C) ... 
//

class HebraDistCiclica extends Thread {
    int miId, numHebras, numero;

    public HebraDistCiclica(int miId, int numHebras, int numero) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numero = numero;
    }

    public void run() {
        int iniElem = miId;
        int finElem = numero;

        for (int i = iniElem; i < finElem; i += numHebras) {
            System.out.printf("Soy la hebra %d, imprimo %d%n", miId, i);
        }
    }
}

class HebraDistBloques extends Thread {
    int miId, numHebras, numero;

    public HebraDistBloques(int miId, int numHebras, int numero) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numero = numero;
    }

    public void run() {
        int tamBloque = numero / numHebras;
        int iniElem = miId * tamBloque;
        int finElem = (miId == numHebras - 1) ? numero : iniElem + tamBloque;

        for (int i = iniElem; i < finElem; i++) {
            System.out.printf("Soy la hebra %d, imprimo %d%n", miId, i);
        }
    }
}

