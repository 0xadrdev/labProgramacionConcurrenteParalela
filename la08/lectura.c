#include <stdio.h> // Definicion de rutinas para E/S
#include <mpi.h>   // Definicion de rutinas de MPI

// Programa principal
int main(int argc, char *argv[])
{
  // Declaracion de variables
  int miId, numProcs;
  MPI_Status s;

  // Inicializacion de MPI
  MPI_Init(&argc, &argv);

  // Obtiene el numero de procesos en ejecucion
  MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
  // Obtiene el identificador del proceso
  MPI_Comm_rank(MPI_COMM_WORLD, &miId); 

  // ------ PARTE CENTRAL DEL CODIGO (INICIO) ---------------------------------
  // Definicion e inicializacion de la variable n
  int n = ( miId + 1 ) * numProcs;
  if (miId == 0) {
    printf("Dame un numero --> \n");
    scanf("%d", &n);

    for (int i = 1; i < numProcs; i++) {
      MPI_Send(&n, 1, MPI_INT, i, 0, MPI_COMM_WORLD); 
    }
  } else {
    MPI_Recv(&n, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &s);
  }

  printf("Proceso <%d> con n = %d\n", miId, n);
  // ------ PARTE CENTRAL DEL CODIGO (FINAL) ----------------------------------
  
  // Finalizacion de MPI
  MPI_Finalize();

  return 0;
}
