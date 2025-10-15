// ============================================================================
class EjemploFuncionCostosa {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     n, numHebras;
    long    t1, t2;
    double  sumaX, sumaY, ts, tc, tb;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <tamanyo>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
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

    // Crea los vectores.
    double vectorX[] = new double[ n ];
    double vectorY[] = new double[ n ];

    //
    // Implementacion secuencial (sin temporizar).
    //
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );
    for( int i = 0; i < n; i++ ) {
      vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
    }

    //
    // Implementacion secuencial.
    //
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );
    t1 = System.nanoTime();
    for( int i = 0; i < n; i++ ) {
      vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
    }
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + ts );
    //// imprimeResultado( vectorX, vectorY );

    // Comprueba el resultado. 
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    //
    // Implementacion paralela ciclica.
    //

    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );

    t1 = System.nanoTime();

    // Gestion de hebras para la implementacion paralela ciclica
    // (A) ....
    HebraFuncionDistCiclica vectorHebrasDistCiclica[] = new HebraFuncionDistCiclica[numHebras];

    for( int i = 0; i < numHebras; i++ ) {
      vectorHebrasDistCiclica[i] = new HebraFuncionDistCiclica(i, numHebras, vectorX, vectorY);
      vectorHebrasDistCiclica[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorHebrasDistCiclica[ i ].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
    System.out.println( "Incremento paralela ciclica:                 " + ts / tc); // (B)


    //// imprimeResultado( vectorX, vectorY );
    // Comprueba el resultado.
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    //
    // Implementacion paralela por bloques.
    //
    // (C) ....
    //

    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );

    t1 = System.nanoTime();

    HebraFuncionDistBloques VectorHebrasDistBloques[] = new HebraFuncionDistBloques[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      VectorHebrasDistBloques[i] = new HebraFuncionDistBloques(i, numHebras, vectorX, vectorY);
      VectorHebrasDistBloques[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          VectorHebrasDistBloques[ i ].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    t2 = System.nanoTime();
    tb = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela bloques (seg.):              " + tb );
    System.out.println( "Incremento paralela bloques:                 " + ts / tb); // (B)

    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );

    System.out.println( "Fin del programa." );
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorX( double vectorX[] ) {
    if( vectorX.length == 1 ) {
      vectorX[ 0 ] = 0.0;
    } else {
      for( int i = 0; i < vectorX.length; i++ ) {
        vectorX[ i ] = 10.0 * ( double ) i / ( ( double ) vectorX.length - 1 );
      }
    }
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorY( double vectorY[] ) {
    for( int i = 0; i < vectorY.length; i++ ) {
      vectorY[ i ] = 0.0;
    }
  }

  // --------------------------------------------------------------------------
  static double sumaVector( double vector[] ) {
    double  suma = 0.0;
    for( int i = 0; i < vector.length; i++ ) {
      suma += vector[ i ];
    }
    return suma;
  }

  // --------------------------------------------------------------------------
  static double evaluaFuncion( double x ) {
    return -Math.cos( Math.exp( -x ) + Math.log1p( x ) );
  }

  // --------------------------------------------------------------------------
  static void imprimeVector( double vector[] ) {
    for( int i = 0; i < vector.length; i++ ) {
      System.out.println( " vector[ " + i + " ] = " + vector[ i ] );
    }
  }

  // --------------------------------------------------------------------------
  static void imprimeResultado( double vectorX[], double vectorY[] ) {
    for( int i = 0; i < Math.min( vectorX.length, vectorY.length ); i++ ) {
      System.out.println( "  i: " + i + 
                          "  x: " + vectorX[ i ] +
                          "  y: " + vectorY[ i ] );
    }
  }

}

// Crea las clases adicionales que sean necesarias
// (D) ... 
//

class HebraFuncionDistCiclica extends Thread {
    int miId, numHebras;
    double[] vectorX, vectorY;

    public HebraFuncionDistCiclica(int miId, int numHebras, double[] vectorX, double[] vectorY) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorX = vectorX;
        this.vectorY = vectorY;
    }

    public void run() {
        int iniElem = miId;
        int finElem = vectorX.length;

        for (int i = iniElem; i < finElem; i += numHebras) {
            vectorY[i] = EjemploFuncionCostosa.evaluaFuncion(vectorX[i]);
            System.out.printf("Soy la hebra %d, evaluando funcion en %d. %n", miId, i);
        }
    }
}

class HebraFuncionDistBloques extends Thread {
    int miId, numHebras;
    double[] vectorX, vectorY;

    public HebraFuncionDistBloques(int miId, int numHebras, double[] vectorX, double[] vectorY) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorX = vectorX;
        this.vectorY = vectorY;
    }

    public void run() {
        int tamBloque = vectorX.length / numHebras;
        int iniElem = miId * tamBloque;
        int finElem = (miId == numHebras - 1) ? vectorX.length : iniElem + tamBloque;

        for (int i = iniElem; i < finElem; i++) {
            vectorY[i] = EjemploFuncionCostosa.evaluaFuncion(vectorX[i]);
            System.out.printf("Soy la hebra %d, evaluando funcion en %d. %n", miId, i);
        }
    }
}


