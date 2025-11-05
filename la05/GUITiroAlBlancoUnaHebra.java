import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// ============================================================================
public class GUITiroAlBlancoUnaHebra {
  // ============================================================================
  
  // Declaracion de constantes (para tamanyos de ventana).
  static final int  maxVentanaX = 800 ;
  static final int  maxVentanaY = 600 ;
  
  // Declaracion de variables.
  JFrame             jframe;
  JPanel             jpanel;
  CanvasCampoTiroUnaHebra cnvCampoTiro;
  JTextField         txfInformacion;
  JTextField         txfVelocidadInicial;
  JTextField         txfAnguloInicial;
  JButton            btnDispara;
  Point              objetivo;
  static int         numIters;
//  MiHebraCalculadora                hebra ; // Ejercicio 4
//  LinkedBlockingQueue<NuevoDisparoUnaHebra> listaD; // Ejercicio 4

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
/*
    try {
      numIters = Integer.parseInt( args[ 0 ] );
      if( numIters <= 0 ) {
        System.err.print( "Uso: [ java programa <numIters> ] donde ( numIters > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numIters = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }
*/
    GUITiroAlBlancoUnaHebra gui = new GUITiroAlBlancoUnaHebra();
    gui.go();
  }
  
  // --------------------------------------------------------------------------
  public void go() {
    SwingUtilities.invokeLater( new Runnable() {
      public void run() {
        generaGUI();
      }
    } );
  }
  
  // --------------------------------------------------------------------------
  public void generaGUI() {
    
    // Declaracion de variables locales.
    JPanel  tablero, informacion, controles, incdec;
    JButton btnVelInc100, btnVelDec100, btnVelInc5, btnVelDec5;
    double  velIni, angIni;
    Font    miFuenteP, miFuenteM, miFuenteG;

    // Crea el JFrame principal.
    jframe = new JFrame( "GUI Tiro Al Blanco " );
    jpanel = ( JPanel ) jframe.getContentPane();
    jpanel.setPreferredSize( new Dimension( maxVentanaX, maxVentanaY ) );
    jpanel.setLayout( new BorderLayout() );
    
    //
    // Creacion del canvas para el campo de tiro.
    //
    cnvCampoTiro = new CanvasCampoTiroUnaHebra();
    
    //
    // Creacion del panel de informacion (aciertos, fallos, etc.).
    //
    informacion = new JPanel();
    informacion.setLayout( new FlowLayout() );
    
    // Crea y anyade el campo de mensajes.
    JLabel labInformacion = new JLabel( "Informacion:" );
    miFuenteM = labInformacion.getFont().deriveFont( Font.PLAIN, 15.0F );
    labInformacion.setFont( miFuenteM );
    informacion.add( labInformacion );
    
    txfInformacion = new JTextField( 45 );
    txfInformacion.setFont( miFuenteM );
    txfInformacion.setEditable( false );
    txfInformacion.setHorizontalAlignment( JTextField.CENTER );
    informacion.add( txfInformacion );
    
    //
    // Creacion del panel de controles de disparo.
    //
    controles = new JPanel();
    controles.setLayout( new FlowLayout() );
    
    // Crea y anyade el control de velocidad inicial.
    JLabel labVelocidadInicial = new JLabel( "Velocidad: " );
    miFuenteG = labVelocidadInicial.getFont().deriveFont( Font.PLAIN, 18.0F );
    labVelocidadInicial.setFont( miFuenteG );
    controles.add( labVelocidadInicial );
    
    velIni = 100.0 * Math.round( 50.0 + Math.random() * 10.0 );
    txfVelocidadInicial  = new JTextField( String.valueOf( velIni ), 7 );
    txfVelocidadInicial.setFont( miFuenteG );
    controles.add( txfVelocidadInicial );
    
    // Creacion del minipanel de incrementos/decrementos.
    incdec = new JPanel();
    incdec.setLayout( new GridLayout( 2, 2 ) );
    
    // Crea y anyade el boton para incrementar la velocidad en 100.
    btnVelInc100 = new JButton( "+100" );
    miFuenteP = btnVelInc100.getFont().deriveFont( Font.PLAIN, 10.0F );
    btnVelInc100.setFont( miFuenteP );
    incdec.add( btnVelInc100 );
    
    // Anyade el codigo para procesar la pulsacion del boton "btnVelInc100".
    btnVelInc100.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        // En las llamadas a getText/setText de objetos graficos aqui no hace
        // falta el invokeLater dado que este codigo lo ejecuta la
        // hebra event-dispatching.
        double vel;
        try {
          vel = Double.parseDouble( txfVelocidadInicial.getText().trim() );
          vel += 100.0;
          txfVelocidadInicial.setText( String.valueOf( vel ) );
        } catch( NumberFormatException ex ) {
          txfInformacion.setText( "ERROR: Numeros incorrectos." );
        }
      }
    } );
    
    // Crea y anyade el boton para incrementar la velocidad en 5.
    btnVelInc5 = new JButton( "+5" );
    btnVelInc5.setFont( miFuenteP );
    incdec.add( btnVelInc5 );
    
    // Anyade el codigo para procesar la pulsacion del boton "btnVelInc5".
    btnVelInc5.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        // En las llamadas a getText/setText de objetos graficos aqui no hace
        // falta el invokeLater dado que este codigo lo ejecuta la
        // hebra event-dispatching.
        double vel;
        try {
          vel = Double.parseDouble( txfVelocidadInicial.getText().trim() );
          vel += 5.0;
          txfVelocidadInicial.setText( String.valueOf( vel ) );
        } catch( NumberFormatException ex ) {
          txfInformacion.setText( "ERROR: Numeros incorrectos." );
        }
      }
    } );
    
    // Crea y anyade el boton para decrementar la velocidad en 100.
    btnVelDec100 = new JButton( "-100" );
    btnVelDec100.setFont( miFuenteP );
    incdec.add( btnVelDec100 );
    
    // Anyade el codigo para procesar la pulsacion del boton "btnVelDec100".
    btnVelDec100.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        // En las llamadas a getText/setText de objetos graficos aqui no hace
        // falta el invokeLater dado que este codigo lo ejecuta la
        // hebra event-dispatching.
        double vel;
        try {
          vel = Double.parseDouble( txfVelocidadInicial.getText().trim() );
          vel -= 100.0;
          txfVelocidadInicial.setText( String.valueOf( vel ) );
        } catch( NumberFormatException ex ) {
          txfInformacion.setText( "ERROR: Numeros incorrectos." );
        }
      }
    } );
    
    // Crea y anyade el boton para decrementar la velocidad en 5.
    btnVelDec5 = new JButton( "-5" );
    btnVelDec5.setFont( miFuenteP );
    incdec.add( btnVelDec5 );
    
    // Anyade el codigo para procesar la pulsacion del boton "btnVelDec5".
    btnVelDec5.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        // En las llamadas a getText/setText de objetos graficos aqui no hace
        // falta el invokeLater dado que este codigo lo ejecuta la
        // hebra event-dispatching.
        double vel;
        try {
          vel = Double.parseDouble( txfVelocidadInicial.getText().trim() );
          vel -= 5.0;
          txfVelocidadInicial.setText( String.valueOf( vel ) );
        } catch( NumberFormatException ex ) {
          txfInformacion.setText( "ERROR: Numeros incorrectos." );
        }
      }
    } );
    
    // Anyade el nuevo minipanel de incrementos/decrementos al panel de control.
    controles.add( incdec );
    
    // Crea y anyade un cierto espacio de separacion.
    JLabel labSeparacion1 = new JLabel( "    " );
    labSeparacion1.setFont( miFuenteG );
    controles.add( labSeparacion1 );
    
    // Crea y anyade el control del angulo inicial.
    JLabel labAnguloInicial = new JLabel( "angulo: " );
    labAnguloInicial.setFont( miFuenteG );
    controles.add( labAnguloInicial );
    
    angIni = Math.round( 45.0 + Math.random() * 15.0 );
    txfAnguloInicial = new JTextField( String.valueOf( angIni ), 5 );
    txfAnguloInicial.setFont( miFuenteG );
    controles.add( txfAnguloInicial );
    
    // Crea y anyade un cierto espacio de separacion.
    JLabel labSeparacion2 = new JLabel( "    " );
    labSeparacion2.setFont( miFuenteG );
    controles.add( labSeparacion2 );
    
    // Crea y anyade el boton de disparo.
    btnDispara = new JButton( "Dispara" );
    btnDispara.setFont( miFuenteG );
    controles.add( btnDispara );
    
    //
    // Creacion del panel de tablero que contiene los minipaneles de
    // informacion y controles.
    //
    tablero = new JPanel();
    tablero.setLayout( new BorderLayout() );
    tablero.add( "Center", informacion );
    tablero.add( "South", controles );
    
    //
    // Anyade el canvas y el tablero al panel principal.
    //
    jpanel.add( "Center", cnvCampoTiro );
    jpanel.add( "South", tablero );
    
    // Fija caracteristicas del frame.
    jframe.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    jframe.pack();
    jframe.setResizable( false );
    jframe.setVisible( true );
    
    // Inicializa la posicion del objetivo.
    this.objetivo = generaCoordenadasDeObjetivo();
    System.out.println( "generaGUI. Coordenadas del objetivo: " +
               this.objetivo.x + "," + this.objetivo.y);
    cnvCampoTiro.guardaCoordenadasObjetivo( this.objetivo );

/* ========= INICIO CODIGO A MODIFICAR EN EJERCICIO 2 ========== */
    // Anyade el codigo para procesar la pulsacion del boton "Dispara".
    btnDispara.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        // En las llamadas a getText/setText de objetos graficos aqui no hace
        // falta el invokeLater dado que este codigo lo ejecuta la
        // hebra event-dispatching.
        double vel, ang;
        try {
          vel = Double.parseDouble( txfVelocidadInicial.getText().trim() ) / 100.0;
          ang = Double.parseDouble( txfAnguloInicial.getText().trim() );
          if( ( 0.0 <= ang )&&( ang < 90 )&&( vel > 0 ) ) {
            txfInformacion.setText( "Calculando y dibujando trayectoria..." );
//            creaYMueveProyectil( new NuevoDisparoUnaHebra( vel, ang ) );
              NuevoDisparoUnaHebra nuevoDisparoUnaHebra = new NuevoDisparoUnaHebra(vel, ang);
              MiHebraCalculadoraUnDisparo t = new MiHebraCalculadoraUnDisparo(cnvCampoTiro, txfInformacion, objetivo, nuevoDisparoUnaHebra);

              t.start();
          } else {
            txfInformacion.setText( "ERROR: Datos incorrectos." );
          }
        } catch( NumberFormatException ex ) {
          txfInformacion.setText( "ERROR: Numeros incorrectos." );
        }
      }
    } );
/* =========   FIN  CODIGO A ANALIZAR EN EJERCICIO 2 ============= */
  }
  
  // --------------------------------------------------------------------------
  Point generaCoordenadasDeObjetivo() {
    int     maxDimX, maxDimY, distanciaAlBorde, objetivoX, objetivoY;
    double  mitadX, posicionX;
    
    // Obten las dimensiones del canvas.
    maxDimX = cnvCampoTiro.getWidth();
    maxDimY = cnvCampoTiro.getHeight();
    
    // Genera una posicion aleatoria en la segunda mitad.
    mitadX  = ( ( double ) ( maxDimX - 1 ) ) / 2.0 ;
    posicionX = Math.round( mitadX + Math.random() * mitadX );
    
    // Controla que el objetivo no esta muy cerca de los bordes.
    distanciaAlBorde = 50;
    objetivoX = Math.max( distanciaAlBorde,
                 Math.min( maxDimX - distanciaAlBorde, ( int ) posicionX ) );
    objetivoY = 0;
    
    return new Point( objetivoX, objetivoY );
  }
}

// ============================================================================
class CanvasCampoTiroUnaHebra extends Canvas {
  // ============================================================================
  
  // Declaracion de constantes.
  static final int  tamProyectil  = 5;
  static final int  tamObjetivoX  = 20;
  static final int  tamObjetivoY  = 30;
  static final int  tamCanyonX  = 40;
  static final int  tamCanyonY  = 40;
  
  // Declaracion de variables.
  int objetivoX, objetivoY;
  
  // --------------------------------------------------------------------------
  public void paint( Graphics g ) {
    
    // Fija el color de fondo.
    this.setBackground( Color.gray );
    
    // Dibuja el borde.
    g.setColor( Color.black );
    g.drawRect( 0, 0, this.getWidth() - 1, this.getHeight() - 1 );
    
    // Dibuja el canyon y el objetivo.
    dibujaCanyon( 0, 0 );
    dibujaObjetivo( objetivoX, objetivoY );
  }
  
  // --------------------------------------------------------------------------
  public void dibujaProyectil( int x, int y, int xOld, int yOld ) {
    Graphics g = this.getGraphics();
    // Borra posicion anterior.
    g.setColor( Color.white );
    g.fillOval( coorX( xOld ), coorY( yOld ), tamProyectil, tamProyectil );
    // Dibuja posicion nueva.
    g.setColor( Color.red );
    g.fillOval( coorX( x ), coorY( y ), tamProyectil, tamProyectil );
  }
  
  // --------------------------------------------------------------------------
  public void dibujaCanyon( int x, int y ) {
    Graphics g = this.getGraphics();
    g.setColor( Color.green );
    g.fillOval( coorX( x ) - tamCanyonX / 2, coorY( y ) - tamCanyonY / 2,
           tamCanyonX, tamCanyonY );
  }
  
  // --------------------------------------------------------------------------
  public void dibujaObjetivo( int x, int y ) {
    Graphics g = this.getGraphics();
    g.setColor( Color.yellow );
    g.fillRect( coorX( x ) - tamObjetivoX / 2, coorY( y ) - tamObjetivoY / 2,
           tamObjetivoX, tamObjetivoY );
  }
  
  // --------------------------------------------------------------------------
  int coorX( int x ) {
    return x;
  }
  
  // --------------------------------------------------------------------------
  int coorY( int y ) {
    return ( this.getHeight() - 1 - y );
  }
  
  // --------------------------------------------------------------------------
  void guardaCoordenadasObjetivo( Point objetivo ) {
    this.objetivoX = objetivo.x;
    this.objetivoY = objetivo.y;
  }
}

// ============================================================================
class NuevoDisparoUnaHebra {
  // ============================================================================
  
  final double velocidadInicial, anguloInicial;
  
  // --------------------------------------------------------------------------
  public NuevoDisparoUnaHebra(double velocidadInicial, double anguloInicial ) {
    this.velocidadInicial = velocidadInicial;
    this.anguloInicial  = anguloInicial;
  }
}

// ============================================================================
class ProyectilUnaHebra {
  // ============================================================================
  
  // Declaracion de constantes.
  static final double  GRAVITY = 9.8;
  static final double  TO_RAD  = ( 2.0 * Math.PI ) / 360.0;
  static final double  DELTA_T = 5.0E-3;
  
  // Declaracion de variables.
  CanvasCampoTiroUnaHebra cnvCampoTiro;
/* ========= INICIO CODIGO A ANALIZAR EN EJERCICIO 2.c) y 5.a) ========= */
  // Posiciones, angulo y velocidades con precision doble.
  double      posX, posY;
  double      anguloRad, velX, velY;
  // Posiciones exactas enteras.
  int         intPosX, intPosY, intPosXOld, intPosYOld;
/* =========   FIN  CODIGO A ANALIZAR EN EJERCICIO 2.c) y 5.a) ========== */
  
  // --------------------------------------------------------------------------
  ProyectilUnaHebra(double velocidadInicial, double anguloInicial,
                    CanvasCampoTiroUnaHebra cnvCampoTiro ) {
    this.posX     = 0.0;
    this.posY     = 0.0;
    this.anguloRad  = anguloInicial * TO_RAD;
    this.velX     = Math.cos( anguloRad ) * velocidadInicial;
    this.velY     = Math.sin( anguloRad ) * velocidadInicial;
    this.cnvCampoTiro = cnvCampoTiro;
  }
  
  // --------------------------------------------------------------------------
  void mueveUnIncremental() {
    // Actualiza la posicion y la velocidad.
    this.posX += this.velX * DELTA_T;
    this.posY += this.velY * DELTA_T;
    //// this.velX = this.velX;  Esta velocidad no cambia.
    this.velY -= GRAVITY * DELTA_T;
    
    // Guarda la anterior posicion entera.
    this.intPosXOld = intPosX;
    this.intPosYOld = intPosY;
    
    // Calcula la nueva posicion entera.
    this.intPosX = ( int ) posX;
    this.intPosY = ( int ) posY;
  }
  
  // --------------------------------------------------------------------------
  void imprimeEstadoProyectilEnConsola() {
    System.out.format( "  Pos:( %6.2f %6.2f )" +
              " Vel:( %6.2f %6.2f )" + " IntPos:( %4d %4d )%n",
              this.posX, this.posY,
              this.velX, this.velY, this.intPosX, this.intPosY );
  }
  
  // --------------------------------------------------------------------------
  public void actualizaDibujoDeProyectil() {
    // Dibuja la nueva posicion del proyectil solo si la nueva posicion es
    // distinta de la anterior.
    if( ( this.intPosX != this.intPosXOld )||
       ( this.intPosY != this.intPosYOld ) ) {
/* ========= INICIO CODIGO A ANALIZAR EN EJERCICIO 2.d) ========= */
      cnvCampoTiro.dibujaProyectil( intPosX, intPosY,
                       intPosXOld, intPosYOld );
/* =========   FIN  CODIGO A ANALIZAR EN EJERCICIO 2.d) ========== */
    }
  }
}


