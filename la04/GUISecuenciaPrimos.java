import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*
// ===========================================================================
class ZonaIntercambio {
// ===========================================================================
  // ...

	public ZonaIntercambio ( ... ) {
    // ...
  }

  // -------------------------------------------------------------------------
  void setTiempo( ... ) {
    // ...
  }

  // -------------------------------------------------------------------------
  long getTiempo( ... ) {
    // ...
  }
}
*/

// ===========================================================================
public class GUISecuenciaPrimos {
// ===========================================================================
  JFrame      container;
  JPanel      jpanel;
  JTextField  txfMensajes;
  JButton     btnIniciaSecuencia, btnCancelaSecuencia;
  JSlider     sldEspera;
//  HebraTrabajadora  t; // Ejercicio 2.2
//  ZonaIntercambio   z; // Ejercicio 2.3
  
  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    GUISecuenciaPrimos gui = new GUISecuenciaPrimos();
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        gui.go();
      }
    });
  }

  // -------------------------------------------------------------------------
  public void go() {
    // Constantes.
    final int valorMaximo = 1000;
    final int valorMedio  = 500;

    // Variables.
    JPanel  tempPanel;

    // Crea el JFrame principal.
    container = new JFrame( "GUI Secuencia de Primos " );

    // Consigue el panel principal del Frame "container".
    jpanel = ( JPanel ) container.getContentPane();
    jpanel.setLayout( new GridLayout( 3, 1 ) );

    // Crea e inserta la etiqueta y el campo de texto para los mensajes.
    txfMensajes = new JTextField( 20 );
    txfMensajes.setEditable( false );
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( new JLabel( "Secuencia: " ) );
    tempPanel.add( txfMensajes );
    jpanel.add( tempPanel );

    // Crea e inserta los botones de Inicia secuencia y Cancela secuencia.
    btnIniciaSecuencia = new JButton( "Inicia secuencia" );
    btnCancelaSecuencia = new JButton( "Cancela secuencia" );
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( btnIniciaSecuencia );
    tempPanel.add( btnCancelaSecuencia );
    jpanel.add( tempPanel );

    // Crea e inserta el slider para controlar el tiempo de espera.
    sldEspera = new JSlider( JSlider.HORIZONTAL, 0, valorMaximo , valorMedio );
    tempPanel = new JPanel();
    tempPanel.setLayout( new BorderLayout() );
    tempPanel.add( new JLabel( "Tiempo de espera: " ) );
    tempPanel.add( sldEspera );
    jpanel.add( tempPanel );
    
    // Activa inicialmente los 2 botones.
    btnIniciaSecuencia.setEnabled( true );
    btnCancelaSecuencia.setEnabled( false );

    // Anyade codigo para procesar el evento del boton de Inicia secuencia.
    btnIniciaSecuencia.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            btnIniciaSecuencia.setEnabled( false );
            btnCancelaSecuencia.setEnabled( true );
        }
    } );

    // Anyade codigo para procesar el evento del boton de Cancela secuencia.
    btnCancelaSecuencia.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            btnIniciaSecuencia.setEnabled( true );
            btnCancelaSecuencia.setEnabled( false );
        }
    } );

    // Anyade codigo para procesar el evento del slider " Espera " .
    sldEspera.addChangeListener( new ChangeListener() {
      public void stateChanged( ChangeEvent e ) {
        JSlider sl = ( JSlider ) e.getSource();
        if ( ! sl.getValueIsAdjusting() ) {
          long tiempoEnMilisegundos = ( long ) sl.getValue();
          System.out.println( "JSlider value = " + tiempoEnMilisegundos );
          // ...
        }
      }
    } );

    // Fija caracteristicas del container.
    container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    container.pack();
    container.setResizable( false );
    container.setVisible( true );

    System.out.println( "% End of routine: go.\n" );
  }

  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean primo;
    if( num < 2 ) {
      primo = false;
    } else {
      primo = true;
      long i = 2;
      while( ( i < num )&&( primo ) ) {
        primo = ( num % i != 0 );
        i++;
      }
    }
    return( primo );
  }
}
