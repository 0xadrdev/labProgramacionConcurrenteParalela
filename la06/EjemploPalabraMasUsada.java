import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// import java.util.concurrent.*;
// import java.util.concurrent.atomic.*;
// import java.util.Map;
// import java.util.stream.*;
// import java.util.function.*;
// import static java.util.stream.Collectors.*;
// import java.util.Comparator.*;

// ============================================================================
class EjemploPalabraMasUsada {
// ============================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                     t1, t2;
    double                   ts, tp;
    int                      numHebras;
    String                   nombreFichero, palabraActual;
    Vector<String>           vectorLineas;
    HashMap<String,Integer>  hmCuentaPalabras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <fichero>" );
      System.exit( -1 );
    }
    try {
      numHebras     = Integer.parseInt( args[ 0 ] );
      nombreFichero = args[ 1 ];
      if( numHebras <= 0 )  {
          System.err.print( "Uso: [ java programa <numHebras> <fichero> ] " );
          System.err.println( "donde ( numHebras > 0 )" );
          System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      nombreFichero = "";
      System.out.println( "ERROR: Argumento numerico incorrectos." );
      System.exit( -1 );
    }

    // Lectura y carga de lineas en "vectorLineas".
    vectorLineas = leeFichero( nombreFichero );
    System.out.println( "Numero de lineas leidas: " + vectorLineas.size() );
    System.out.println();

    //
    // Implementacion secuencial sin temporizar.
    //
    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < vectorLineas.size(); i++ ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }

    //
    // Implementacion secuencial.
    //
    t1 = System.nanoTime();
    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < vectorLineas.size(); i++ ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial: " );
    imprimePalabraMasUsadaYVeces( hmCuentaPalabras );
    System.out.println( " Tiempo(s): " + ts );
    System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 1: Uso de synchronizedMap y cerrojo
    //
    t1 = System.nanoTime();
    MiHebra_1 vectorMiHebra[] = new MiHebra_1[numHebras];
    HashMap<String, Integer> contadorPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < numHebras; i++ ) {
        vectorMiHebra[i] = new MiHebra_1(i, numHebras, vectorLineas, contadorPalabras);
        vectorMiHebra[i].start();
    }

    // Espera a que terminen todas las hebras.
    for (int i = 0; i < numHebras; i++) {
      try {
          vectorMiHebra[i].join() ;
      } catch(InterruptedException ex ) {
          ex.printStackTrace() ;
      }
    }
    t2 = System.nanoTime();
    tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 1: " );
    imprimePalabraMasUsadaYVeces(contadorPalabras);
    System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts / tp);
    System.out.println( "Num. elems. tabla hash: " + contadorPalabras);
    System.out.println();


    //
    // Implementacion paralela 2: Uso de Hashtable y cerrojo

      t1 = System.nanoTime();
      MiHebra_2 vectorMiHebraHashTable[] = new MiHebra_2[numHebras];
      Hashtable<String, Integer> contadorPalabrasHashTable = new Hashtable<String,Integer>( 1000, 0.75F );
      for( int i = 0; i < numHebras; i++ ) {
          vectorMiHebraHashTable[i] = new MiHebra_2(i, numHebras, vectorLineas, contadorPalabrasHashTable);
          vectorMiHebraHashTable[i].start();
      }

      // Espera a que terminen todas las hebras.
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorMiHebraHashTable[i].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela 2: " );
      imprimePalabraMasUsadaYVeces(contadorPalabrasHashTable);
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts / tp);
      System.out.println( "Num. elems. tabla hash: " + contadorPalabrasHashTable);
      System.out.println();


    //
    // Implementacion paralela 3: Uso de ConcurrentHashMap y cerrojo
    // ...
      t1 = System.nanoTime();
      MiHebra_2 vectorMiHebraConcurrentHashMap[] = new MiHebra_2[numHebras];
      ConcurrentHashMap<String, Integer> contadorPalabrasConcurrentHashMap = new ConcurrentHashMap<String, Integer>(1000, 0.75F);
      for( int i = 0; i < numHebras; i++ ) {
          vectorMiHebraConcurrentHashMap[i] = new MiHebra_2(i, numHebras, vectorLineas, contadorPalabrasConcurrentHashMap);
          vectorMiHebraConcurrentHashMap[i].start();
      }

      // Espera a que terminen todas las hebras.
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorMiHebraConcurrentHashMap[i].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela 3: " );
      imprimePalabraMasUsadaYVeces(contadorPalabrasConcurrentHashMap);
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts / tp);
      System.out.println( "Num. elems. tabla hash: " + contadorPalabrasConcurrentHashMap);
      System.out.println();



    //
    // Implementacion paralela 4: Uso de ConcurrentHashMap con merge
    // ...
      t1 = System.nanoTime();
      MiHebra_4 vectorMiHebraConcurrentMerge[] = new MiHebra_4[numHebras];
      ConcurrentHashMap<String, Integer> contadorPalabrasMerge = new ConcurrentHashMap<String,Integer>( 1000, 0.75F );
      for( int i = 0; i < numHebras; i++ ) {
          vectorMiHebraConcurrentMerge[i] = new MiHebra_4(i, numHebras, vectorLineas, contadorPalabrasMerge);
          vectorMiHebraConcurrentMerge[i].start();
      }

      // Espera a que terminen todas las hebras.
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorMiHebraConcurrentMerge[i].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela 4: " );
      imprimePalabraMasUsadaYVeces(contadorPalabrasMerge);
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts / tp);
      System.out.println( "Num. elems. tabla hash: " + contadorPalabrasMerge);
      System.out.println();

    //
    // Implementacion paralela 5: Uso de ConcurrentHashMap escalable
    // ...

      t1 = System.nanoTime();
      MiHebra_5 vectorMiHebraConcurrentAbsent[] = new MiHebra_5[numHebras];
      ConcurrentHashMap<String, Integer> contadorPalabrasAbsent = new ConcurrentHashMap<String,Integer>( 1000, 0.75F );
      for( int i = 0; i < numHebras; i++ ) {
          vectorMiHebraConcurrentAbsent[i] = new MiHebra_5(i, numHebras, vectorLineas, contadorPalabrasAbsent);
          vectorMiHebraConcurrentAbsent[i].start();
      }

      // Espera a que terminen todas las hebras.
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorMiHebraConcurrentAbsent[i].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela 5: " );
      imprimePalabraMasUsadaYVeces(contadorPalabrasAbsent);
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts / tp);
      System.out.println( "Num. elems. tabla hash: " + contadorPalabrasAbsent);
      System.out.println();



    //
    // Implementacion paralela 6: Uso de CHM escalable con AtomicInteger
    // ...

      t1 = System.nanoTime();
      MiHebra_6 vectorMiHebraConcurrentAtomic[] = new MiHebra_6[numHebras];
      ConcurrentHashMap<String, AtomicInteger> contadorPalabrasAtomic = new ConcurrentHashMap<String,AtomicInteger>( 1000, 0.75F );
      for( int i = 0; i < numHebras; i++ ) {
          vectorMiHebraConcurrentAtomic[i] = new MiHebra_6(i, numHebras, vectorLineas, contadorPalabrasAtomic);
          vectorMiHebraConcurrentAtomic[i].start();
      }

      // Espera a que terminen todas las hebras.
      for (int i = 0; i < numHebras; i++) {
          try {
              vectorMiHebraConcurrentAtomic[i].join() ;
          } catch(InterruptedException ex ) {
              ex.printStackTrace() ;
          }
      }
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela 6: " );
      // imprimePalabraMasUsadaYVeces(contadorPalabrasAtomic);
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts / tp);
      System.out.println( "Num. elems. tabla hash: " + contadorPalabrasAtomic);
      System.out.println();


/*

    //
    // Implementacion paralela 7: Uso de CHM escalable con AtomicInteger y 256 niv.
    // ...

    //
    // Implementacion paralela 8: Uso de Streams
    // t1 = System.nanoTime();
    // Map<String,Long> stCuentaPalabras = vectorLineas.parallelStream()
    //                                       .filter( s -> s != null )
    //                                       .map( s -> s.split( "\\W+" ) )
    //                                       .flatMap( Arrays::stream )
    //                                       .map( String::trim )
    //                                       .filter( s -> (s.length() > 0) )
    //                                       .collect( groupingBy (s -> s, counting()));
    // t2 = System.nanoTime();
    // ...
*/
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  public static Vector<String> leeFichero( String fileName ) {
    BufferedReader br; 
    String         linea;
    Vector<String> data = new Vector<String>();

    try {
      br = new BufferedReader( new FileReader( fileName ) );
      while( ( linea = br.readLine() ) != null ) {
        //// System.out.println( "Leida linea: " + linea );
        data.add( linea );
      }
      br.close(); 
    } catch( FileNotFoundException ex ) {
      ex.printStackTrace();
    } catch( IOException ex ) {
      ex.printStackTrace();
    }
    return data;
  }

  // -------------------------------------------------------------------------
  public static void contabilizaPalabra( // Se ha cambiado la firma del metodo de HashMap a Map.
                         Map<String,Integer> cuentaPalabras,
                         String palabra ) {
    Integer numVeces = cuentaPalabras.get( palabra );
    if( numVeces != null ) {
      cuentaPalabras.put( palabra, numVeces+1 );
    } else {
      cuentaPalabras.put( palabra, 1 );
    }
  }   

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVeces(
                  Map<String,Integer> cuentaPalabras ) {
    Vector<Map.Entry> lista = 
        new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    String palabraMasUsada = "";
    int    numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for( int i = 0; i < lista.size(); i++ ) {
      String palabra = ( String ) lista.get( i ).getKey();
      int numVeces = ( Integer ) lista.get( i ).getValue();
      if( i == 0 ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      } else if( numVecesPalabraMasUsada < numVeces ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print( "( Palabra: '" + palabraMasUsada + "' " + 
                         "veces: " + numVecesPalabraMasUsada + " )" );
  }

  // --------------------------------------------------------------------------
  static void printCuentaPalabrasOrdenadas(
                  HashMap<String,Integer> cuentaPalabras ) {
    int             i, numVeces;
    List<Map.Entry> list = new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    // Ordena por valor.
    Collections.sort( 
        list,
        new Comparator<Map.Entry>() {
            public int compare( Map.Entry e1, Map.Entry e2 ) {
              Integer i1 = ( Integer ) e1.getValue();
              Integer i2 = ( Integer ) e2.getValue();
              return i2.compareTo( i1 );
            }
        }
    );
    // Muestra contenido.
    i = 1;
    System.out.println( "Veces Palabra" );
    System.out.println( "-----------------" );
    for( Map.Entry e : list ) {
      numVeces = ( ( Integer ) e.getValue () ).intValue();
      System.out.println( i + " " + e.getKey() + " " + numVeces );
      i++;
    }
    System.out.println( "-----------------" );
  }
}

class MiHebra_1 extends Thread {
    int miId, numHebras;
    Vector<String> vectorLineas;
    final HashMap<String, Integer> contadorPalabras;

    public MiHebra_1(int miId, int numHebras, Vector<String> vectorLineas, HashMap<String, Integer> contadorPalabras) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorLineas = vectorLineas;
        this.contadorPalabras = contadorPalabras;
    }

    public void run() {
        int iniElem = miId;
        long finElem = vectorLineas.size();

        for (int i = iniElem; i < finElem; i += numHebras) {
            String[] palabras = vectorLineas.get(i).split( "\\W+" );
            for (String palabra : palabras) {
                palabra = palabra.trim();
                if (!palabra.isEmpty()) {
                    synchronized (this.contadorPalabras) {
                        EjemploPalabraMasUsada.contabilizaPalabra(contadorPalabras, palabra);
                    }
                }
            }
        }
    }

    public void cuentaPalabras(String palabra) { // Este metodo tiene que estar aquí ?
        Integer numVeces = contadorPalabras.get( palabra );
        if(numVeces != null) {
            contadorPalabras.put(palabra, numVeces+1);
        } else {
            contadorPalabras.put(palabra, 1);
        }
    }
}

class MiHebra_2 extends Thread {
    int miId, numHebras;
    Vector<String> vectorLineas;
    final Map<String, Integer> contadorPalabras;

    public MiHebra_2(int miId, int numHebras, Vector<String> vectorLineas, Map<String, Integer> contadorPalabras) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorLineas = vectorLineas;
        this.contadorPalabras = contadorPalabras;
    }

    public void run() {
        int iniElem = miId;
        long finElem = vectorLineas.size();

        for (int i = iniElem; i < finElem; i += numHebras) {
            String[] palabras = vectorLineas.get(i).split( "\\W+" );
            for (String palabra : palabras) {
                palabra = palabra.trim();
                if (!palabra.isEmpty()) {
                    synchronized (this.contadorPalabras) { // No entiendo muy bien porque tiene que estar sincronizado el acceso al metodo.
                        EjemploPalabraMasUsada.contabilizaPalabra(contadorPalabras, palabra);
                    }
                }
            }
        }
    }
}

class MiHebra_4 extends Thread {
    int miId, numHebras;
    Vector<String> vectorLineas;
    final Map<String, Integer> contadorPalabras;

    public MiHebra_4(int miId, int numHebras, Vector<String> vectorLineas, Map<String, Integer> contadorPalabras) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorLineas = vectorLineas;
        this.contadorPalabras = contadorPalabras;
    }

    public void run() {
        int iniElem = miId;
        long finElem = vectorLineas.size();

        for (int i = iniElem; i < finElem; i += numHebras) {
            String[] palabras = vectorLineas.get(i).split( "\\W+" );
            for (String palabra : palabras) {
                palabra = palabra.trim();
                if (!palabra.isEmpty()) {
                    contadorPalabras.merge(palabra, 1, Integer::sum);
                }
            }
        }
    }
}

class MiHebra_5 extends Thread {
    int miId, numHebras;
    Vector<String> vectorLineas;
    final Map<String, Integer> contadorPalabras;

    public MiHebra_5(int miId, int numHebras, Vector<String> vectorLineas, Map<String, Integer> contadorPalabras) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorLineas = vectorLineas;
        this.contadorPalabras = contadorPalabras;
    }

    public void run() {
        int iniElem = miId;
        long finElem = vectorLineas.size();

        for (int i = iniElem; i < finElem; i += numHebras) {
            String[] palabras = vectorLineas.get(i).split( "\\W+" );
            for (String palabra : palabras) {
                palabra = palabra.trim();

                if (!palabra.isEmpty()) {
                    Integer valorExistente = contadorPalabras.putIfAbsent(palabra, 1);
                    if (valorExistente != null) {
                        boolean actualizacionExitosa = false;
                        Integer valorAntiguo = valorExistente;

                        while (!actualizacionExitosa) {
                            Integer nuevoValor = valorAntiguo + 1;
                            actualizacionExitosa = contadorPalabras.replace(palabra, valorAntiguo, nuevoValor);

                            if (!actualizacionExitosa) {
                                valorAntiguo = contadorPalabras.get(palabra);
                            }
                        }
                    }
                }
            }
        }
    }
}

class MiHebra_6 extends Thread {
    int miId, numHebras;
    Vector<String> vectorLineas;
    final Map<String, AtomicInteger> contadorPalabras;

    public MiHebra_6(int miId, int numHebras, Vector<String> vectorLineas, Map<String, AtomicInteger> contadorPalabras) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vectorLineas = vectorLineas;
        this.contadorPalabras = contadorPalabras;
    }

    public void run() {
        int iniElem = miId;
        long finElem = vectorLineas.size();

        for (int i = iniElem; i < finElem; i += numHebras) {
            String[] palabras = vectorLineas.get(i).split( "\\W+" );
            for (String palabra : palabras) {
                palabra = palabra.trim();
                if (!palabra.isEmpty()) {
                    AtomicInteger contador = contadorPalabras.get(palabra);
                    if (contador == null) {
                        contador = contadorPalabras.putIfAbsent(palabra, new AtomicInteger(0));

                        if (contador == null) {
                            contador = contadorPalabras.get(palabra);
                        }
                    }
                    contador.incrementAndGet();
                }
            }
        }
    }
}









