import java.util.concurrent.atomic.AtomicInteger;

// ===========================================================================
public class EjemploMuestraPrimosEnVector {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     numHebras, vectOpt;
    boolean option = true;
    long    t1, t2;
    double  ts, tc, tb, td;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <vectOpt>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      vectOpt   = Integer.parseInt( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( ( vectOpt != 0 ) && ( vectOpt != 1 ) ) ){
          System.err.print( "Uso: [ java programa <numHebras> <vecOpt> ] " );
          System.err.println( "donde ( numHebras > 0 )  y ( vectOpt es 0 o 1 )" );
          System.exit( -1 );
      } else {
        option = (vectOpt == 0);
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    //
    // Eleccion del vector de trabajo
    //
    VectorNumeros vn = new VectorNumeros (option);
    long vectorTrabajo[] = vn.vector;

    //
    // Implementacion secuencial.
    //
    System.out.println( "" );
    System.out.println( "Implementacion secuencial." );
    t1 = System.nanoTime();
    for( int i = 0; i < vectorTrabajo.length; i++ ) {
      if( esPrimo( vectorTrabajo[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vectorTrabajo[ i ] );
      }
    }
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + ts );
    //
    // Implementacion paralela ciclica.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela ciclica." );
    t1 = System.nanoTime();
    // Gestion de hebras para la implementacion paralela ciclica
    // (A) ....

    MiHebraPrimoDistCiclica vectorHebrasDistCiclica[] = new MiHebraPrimoDistCiclica[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      vectorHebrasDistCiclica[i] = new MiHebraPrimoDistCiclica(i, numHebras, vn);
      vectorHebrasDistCiclica[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorHebrasDistCiclica[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
    System.out.println( "Incremento paralela ciclica:                 " + ts / tc ); // (B)

    //
    // Implementacion paralela por bloques.
    //
    // (C) ....

    MiHebraPrimoDistPorBloques vectorHebrasDistPorBloques[] = new MiHebraPrimoDistPorBloques[numHebras];
    for( int i = 0; i < numHebras; i++ ) {
      vectorHebrasDistPorBloques[i] = new MiHebraPrimoDistPorBloques(i, numHebras, vn.vector);
      vectorHebrasDistPorBloques[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorHebrasDistPorBloques[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }

    t2 = System.nanoTime();
    tb = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela bloques (seg.):              " + tb );
    System.out.println( "Incremento paralela bloques:                 " + ts / tb ); // (B)


    //
    // Implementacion paralela dinamica.
    //
    // (D) ....

      MiHebraPrimoDistDinamica vectorHebrasDistDinamica[] = new MiHebraPrimoDistDinamica[numHebras];
      AtomicInteger atomicIndex = new AtomicInteger();
      for( int i = 0; i < numHebras; i++ ) {
          vectorHebrasDistDinamica[i] = new MiHebraPrimoDistDinamica(atomicIndex, vn.vector);
          vectorHebrasDistDinamica[i].start();
      }

      // Espera a que terminen todas las hebras.
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorHebrasDistDinamica[i].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }

      t2 = System.nanoTime();
      td = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.println( "Tiempo paralela dinamica (seg.):              " + td );
      System.out.println( "Incremento paralela dinamica:                 " + ts / td ); // (B)

  }

  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean cond;
    if( num < 2 ) {
      cond = false;
    } else {
      cond = true;
      long i = 2;
      while( ( i < num )&&( cond ) ) { 
        cond = ( num % i != 0 );
        i++;
      }
    }
    return( cond );
  }
}

// Definicion de las Clases Hebras
//
// (E) ....

class MiHebraPrimoDistCiclica extends Thread {
    int miId, numHebras;
    VectorNumeros vector;

    public MiHebraPrimoDistCiclica(int miId, int numHebras, VectorNumeros vector) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vector = vector;
    }

    public void run() {
        int iniElem = miId;
        int finElem = vector.vector.length;

        for (int i = iniElem; i < finElem; i += numHebras) {
            if (EjemploMuestraPrimosEnVector.esPrimo(vector.vector[i])) {
                System.out.printf("Soy la hebra " + miId + " y he encontrado el primo " + vector.vector[i] + " \n");
            }
        }
    }
}

class MiHebraPrimoDistPorBloques extends Thread {
    int miId, numHebras;
    long[] vector;

    public MiHebraPrimoDistPorBloques(int miId, int numHebras, long[] vector) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vector = vector;
    }

    public void run() {
        int tamBloque = vector.length / numHebras;
        int iniElem = miId * tamBloque;
        int finElem = (miId == numHebras - 1) ? vector.length : iniElem + tamBloque;

        for (int i = iniElem; i < finElem; i++) {
            if (EjemploMuestraPrimosEnVector.esPrimo(vector[i])) {
                System.out.printf("Soy la hebra " + miId + " y he encontrado el primo " + vector[i] + " \n");
            }
        }
    }
}

class MiHebraPrimoDistDinamica extends Thread {
    AtomicInteger atomicIndex;
    long[] vector;

    public MiHebraPrimoDistDinamica(AtomicInteger atomicIndex, long[] vector) {
        this.atomicIndex = atomicIndex;
        this.vector = vector;
    }

    public void run() {
        int actualIndex = atomicIndex.getAndIncrement();

        while (actualIndex < vector.length) {
            if (EjemploMuestraPrimosEnVector.esPrimo(vector[actualIndex])) {
                System.out.printf("Es primo " + vector[actualIndex] + " \n");
            }
            atomicIndex.getAndIncrement();
        }
    }
}

// ===========================================================================
class VectorNumeros {
// ===========================================================================
  long    vector[];
  // -------------------------------------------------------------------------
  public VectorNumeros (boolean caso) {
    if (caso) {
      vector = new long [] {
      200000081L, 200000083L, 200000089L, 200000093L,
      200000107L, 200000117L, 200000123L, 200000131L,
      200000161L, 200000183L, 200000201L, 200000209L,
      200000221L, 200000237L, 200000239L, 200000243L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
      };
    } else {
      vector = new long [] {
      200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000107L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000117L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000123L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000131L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000161L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000183L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000201L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000209L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 
      200000221L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000237L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000239L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000243L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
      };
    }
  }
}

