import javax.swing.*;
import java.awt.*;

class MiHebraCalculadoraUnDisparo extends Thread {
    Point objetivo;
    JTextField txfInformacion;
    NuevoDisparoUnaHebra nuevoDisparoUnaHebra;
    CanvasCampoTiroUnaHebra canvasCampoTiroUnaHebra;

    public MiHebraCalculadoraUnDisparo(CanvasCampoTiroUnaHebra canvasCampoTiroUnaHebra, JTextField txfInformacion, Point objetivo, NuevoDisparoUnaHebra nuevoDisparoUnaHebra) {
        this.objetivo = objetivo;
        this.txfInformacion = txfInformacion;
        this.nuevoDisparoUnaHebra = nuevoDisparoUnaHebra;
        this.canvasCampoTiroUnaHebra = canvasCampoTiroUnaHebra;
    }

    public void run() {
        ProyectilUnaHebra p;
        boolean      impactado;

        p = new ProyectilUnaHebra( nuevoDisparoUnaHebra.velocidadInicial, nuevoDisparoUnaHebra.anguloInicial, canvasCampoTiroUnaHebra);
        impactado = false;
        while( ! impactado ) {
          // Muestra en pantalla los datos del proyectil p.
          p.imprimeEstadoProyectilEnConsola();

          // Mueve un incremental de tiempo el proyectil p.
          p.mueveUnIncremental();

          // Actualiza en pantalla la posicion del proyectil p.
          p.actualizaDibujoDeProyectil();

          // Comprueba si el proyectil p ha impactado en el suelo.
          impactado = determinaEstadoProyectil( p );

          duermeUnPoco( 2L );
        }
    }

    boolean determinaEstadoProyectil( ProyectilUnaHebra p ) {
        // Devuelve cierto si el proyectil ha impactado contra el suelo o contra
        // el objetivo.
        boolean  impactado;
        String   mensaje;

        if ( ( p.intPosX == objetivo.x )&&( p.intPosY == objetivo.y ) ) {
            // El proyectil ha acertado el objetivo.
            impactado = true;
            mensaje = " Destruido!!!";
            muestraMensajeEnCampoInformacion( mensaje );

        } else if( ( p.intPosY <= 0 )&&( p.velY < 0.0 ) ) {
            // El proyectil ha impactado contra el suelo, pero no ha acertado.
            impactado = true;
            mensaje = "Has fallado. Esta en " + objetivo.x + ". " +
                    "Has disparado a " + p.intPosX + ".";
            muestraMensajeEnCampoInformacion( mensaje );
        } else {
            // El proyectil continua en vuelo.
            impactado = false;
        }
        return impactado;
    }

    void muestraMensajeEnCampoInformacion( String mensaje ) {
        // Muestra mensaje en el cuadro de texto de informacion.

        /* ========= INICIO CODIGO A ANALIZAR EN EJERCICIO 2.e) ========= */
        String miMensaje = mensaje;
        txfInformacion.setText( miMensaje );
        /* =========   FIN  CODIGO A ANALIZAR EN EJERCICIO 2.e) ========== */
    }

    void duermeUnPoco( long millis ) {
        try {
            Thread.sleep( millis );
        } catch( InterruptedException ex ) {
            ex.printStackTrace();
        }
    }
}