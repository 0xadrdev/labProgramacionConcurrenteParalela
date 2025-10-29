import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  Acumula() {
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
      suma += dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
      return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones extends Thread {
// ===========================================================================

    int miId, numHebras;
    long numRectangulos;
    Acumula acumulador;

    // -------------------------------------------------------------------------
    MiHebraMultAcumulaciones(int miId, int numHebras, long numRectangulos,
                             Acumula acumulador) {

        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.acumulador = acumulador;
    }

    // -------------------------------------------------------------------------

    public void run() {
        int iniElem = miId;
        long finElem = numRectangulos;
        double baseRectangulo = 1.0 / ((double) numRectangulos ), x;

        for (int i = iniElem; i < finElem; i += numHebras) {
            x = baseRectangulo * (((double)i) + 0.5 );
            acumulador.acumulaDato(EjemploNumeroPI.f(x));
        }
    }
}



// ===========================================================================
class MiHebraUnaAcumulacion extends Thread {
// ===========================================================================

    int miId, numHebras;
    long numRectangulos;
    Acumula acumulador;

    // -------------------------------------------------------------------------
    MiHebraUnaAcumulacion(int miId, int numHebras, long numRectangulos,
                             Acumula acumulador) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.acumulador = acumulador;
    }

    // -------------------------------------------------------------------------

    public void run() {
        int iniElem = miId;
        long finElem = numRectangulos;
        double baseRectangulo = 1.0 / ((double) numRectangulos ), x; // Tiene que ser double ???
        double sumaL = 0.0;

        for (int i = iniElem; i < finElem; i += numHebras) {
            x = baseRectangulo * (((double)i) + 0.5 );
            sumaL += EjemploNumeroPI.f(x);
        }
        acumulador.acumulaDato(sumaL);
    }
}



// ===========================================================================
class MiHebraMultAcumulacionAtomica extends Thread {
// ===========================================================================
    int miId, numHebras;
    long numRectangulos;
    DoubleAdder acumuladorAtomico;

    // -------------------------------------------------------------------------
    MiHebraMultAcumulacionAtomica(int miId, int numHebras, long numRectangulos, DoubleAdder acumuladorAtomico) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.acumuladorAtomico = acumuladorAtomico;
    }

    // -------------------------------------------------------------------------
    public void run() {
        int iniElem = miId;
        long finElem = numRectangulos;
        double baseRectangulo = 1.0 / ((double) numRectangulos ), x; // Tiene que ser double ???

        for (int i = iniElem; i < finElem; i += numHebras) {
            x = baseRectangulo * (((double)i) + 0.5 );
            acumuladorAtomico.add(EjemploNumeroPI.f(x));
        }
    }
}


// ===========================================================================
class MiHebraUnaAcumulacionAtomica extends Thread {
// ===========================================================================
    int miId, numHebras;
    long numRectangulos;
    DoubleAdder acumuladorAtomico;

    // -------------------------------------------------------------------------
    MiHebraUnaAcumulacionAtomica(int miId, int numHebras, long numRectangulos, DoubleAdder acumuladorAtomico) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.acumuladorAtomico = acumuladorAtomico;
    }

    // -------------------------------------------------------------------------

    public void run() {
        int iniElem = miId;
        long finElem = numRectangulos;
        double baseRectangulo = 1.0 / ((double) numRectangulos ), x; // Tiene que ser double ???
        double sumaL = 0.0;

        for (int i = iniElem; i < finElem; i += numHebras) {
            x = baseRectangulo * (((double) i) + 0.5);
            sumaL += EjemploNumeroPI.f(x);
        }

        acumuladorAtomico.add(sumaL);
    }
}


// ===========================================================================
class EjemploNumeroPI {
// ===========================================================================
  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    long                        t1, t2;
    double                      tSec, tPar;
    // Acumula                     a;
    // MiHebraMultAcumulaciones  vt[];

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( numRectangulos <= 0 ) ) {
        System.err.print( "Uso: [ java programa <numHebras> <n> ] " );
        System.err.println( "donde ( numHebras > 0 ) y ( numRectangulos > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Inicio del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ((double) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo secuencial (s.):        " + tSec );
    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra.
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra." );
    t1 = System.nanoTime();

    Acumula acumuladorMultAcumulaciones = new Acumula();
    MiHebraMultAcumulaciones vectorMiHebraMultAcumulaciones[] = new MiHebraMultAcumulaciones[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      vectorMiHebraMultAcumulaciones[i] = new MiHebraMultAcumulaciones(i, numHebras, numRectangulos, acumuladorMultAcumulaciones);
      vectorMiHebraMultAcumulaciones[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorMiHebraMultAcumulaciones[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    pi = acumuladorMultAcumulaciones.dameDato() * baseRectangulo;

    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec / tPar);

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra.
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra." );
    t1 = System.nanoTime();

    Acumula acumuladorUnaAcumulacion = new Acumula();
    MiHebraUnaAcumulacion vectorMiHebraUnaAcumulacion[] = new MiHebraUnaAcumulacion[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      vectorMiHebraUnaAcumulacion[i] = new MiHebraUnaAcumulacion(i, numHebras, numRectangulos, acumuladorUnaAcumulacion);
      vectorMiHebraUnaAcumulacion[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorMiHebraUnaAcumulacion[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    pi = acumuladorUnaAcumulacion.dameDato() * baseRectangulo;

    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec / tPar );


    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra (Atomica)
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra (At)." );
    t1 = System.nanoTime();

    DoubleAdder acumuladorMultAtomico = new DoubleAdder();
    MiHebraMultAcumulacionAtomica vectorMiHebraMultAcumulacionesAtomic[] = new MiHebraMultAcumulacionAtomica[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      vectorMiHebraMultAcumulacionesAtomic[i] = new MiHebraMultAcumulacionAtomica(i, numHebras, numRectangulos, acumuladorMultAtomico);
      vectorMiHebraMultAcumulacionesAtomic[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorMiHebraMultAcumulacionesAtomic[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    pi = acumuladorMultAtomico.sum() * baseRectangulo;

    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec / tPar);


    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra (Atomica).
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra (At)." );
    t1 = System.nanoTime();

    DoubleAdder acumuladorUnoAtomic = new DoubleAdder();
    MiHebraUnaAcumulacionAtomica vectorMiHebraUnaAcumulacionAtomic[] = new MiHebraUnaAcumulacionAtomica[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      vectorMiHebraUnaAcumulacionAtomic[i] = new MiHebraUnaAcumulacionAtomica(i, numHebras, numRectangulos, acumuladorUnoAtomic);
      vectorMiHebraUnaAcumulacionAtomic[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorMiHebraUnaAcumulacionAtomic[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    pi = acumuladorUnoAtomic.sum() * baseRectangulo;
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec / tPar);
    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

