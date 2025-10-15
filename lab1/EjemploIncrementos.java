// ============================================================================
class CuentaIncrementos {
// ============================================================================
  long contador = 0;

  // --------------------------------------------------------------------------
  void incrementaContador() {
    contador++;
  }

  // --------------------------------------------------------------------------
  long dameContador() {
    return( contador );
  }
}


// ============================================================================
class MiHebraIncrementos extends Thread {
// ============================================================================
  // Declaracion de variables
  // ...
    int miId;
    int num1;
    CuentaIncrementos contador;


  // --------------------------------------------------------------------------
  // Definicion del constructor, si es necesario
  // ...
     public MiHebraIncrementos(int miId, int num1, CuentaIncrementos contador) {
         this.miId = miId;
         this.num1 = num1;
         this.contador = contador;
     }

  // --------------------------------------------------------------------------
  public void run() {
    System.out.println( "Hebra: " + miId + " Comenzando incrementos" );
    // Bucle de 1000000 incrementos del objeto compartido
    // ...
      for (int i = 1; i <= 1000000; i++) {
          num1++;
          contador.incrementaContador();
      }
    System.out.println( "Hebra: " + miId + " Terminando incrementos" );
  }
}

// ============================================================================
class EjemploIncrementos {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 1 ) {
      System.err.println( "Uso: java programa <numHebras>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      if( numHebras <= 0 ) {
        System.err.println( "Uso: [ java programa <numHebras> ] donde numHebras > 0" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    System.out.println( "numHebras: " + numHebras );

    // --------  INCLUIR NUEVO CODIGO A CONTINUACION --------------------------
    // ...

    CuentaIncrementos contador = new CuentaIncrementos();

    System.out.println( "Valor Inicial:" + contador.dameContador());

    MiHebraIncrementos vh[] = new MiHebraIncrementos[numHebras];

    for( int i = 0; i < numHebras; i++ ) {
        vh[i] = new MiHebraIncrementos( i, numHebras, contador);
        vh[i].start();
    }

    for ( int i = 0; i < numHebras ; i ++ ) {
        try {
            vh[ i ].join() ;
            } catch(InterruptedException ex ) {
            ex.printStackTrace() ;
        }
    }
    System.out.println( "Valor Final:" + contador.dameContador());
  }
}

