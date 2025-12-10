#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  // Inicializa MPI.
  MPI_Init( &argc, &argv );
  MPI_Comm_size( MPI_COMM_WORLD, &numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, &miId );
  
  // --------------------------------------------------------------------------
  int prc, suma, aux, dato;
  MPI_Status st ;
  // Cada proceso dispone de un dato cualquiera.
  dato = numProcs - miId + 1;
  printf( "Proc %d Dato vale: %d \n", miId, dato );
 
  // ... Incluir codigo asociado a los ejercicios 1 y 2
  
  // --------------------------------------------------------------------------

  if (miId % 2 == 0) { 
    if (miId == 0) {
      suma = dato;
      for (prc = 2; prc < numProcs; prc += 2) {
        MPI_Recv(&aux, 1, MPI_INT, prc, 88, MPI_COMM_WORLD, &st);
        suma += aux;
      }

      printf("Proc 0 informa: La suma de los pares es %d \n", suma);
    } else {
      MPI_Send(&dato, 1, MPI_INT, 0, 88, MPI_COMM_WORLD);
    }
  }

  // Finalizacion de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Final de programa (%d) \n", miId );
  return 0;
}

