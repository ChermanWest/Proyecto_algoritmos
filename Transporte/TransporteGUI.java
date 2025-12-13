package Transporte;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

/**
 * Interfaz Gráfica de Usuario (GUI) para el sistema de simulación de transporte.
 * <p>
 * Esta clase se encarga de visualizar el grafo, los buses y los pasajeros,
 * además de proporcionar controles para iniciar/pausar la simulación y
 * calcular rutas óptimas utilizando el algoritmo de Dijkstra.
 * </p>
 */
public class TransporteGUI extends JFrame {

    /** Referencia al simulador lógico. */
    private Simulador simulador;
    /** Referencia al grafo de la red. */
    private Grafo grafo;
    
    /** Panel personalizado donde se realiza el dibujo del mapa. */
    private GraphPanel panelGrafo;
    
    // --- Componentes de la Interfaz ---
    private JLabel lblTiempo;
    private JComboBox<String> comboOrigen;
    private JComboBox<String> comboDestino;
    private JTextArea txtRuta;
    private JTextArea txtInfo;
    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnPaso;

    // --- Variables de Control de Animación ---
    
    /** Timer para controlar el refresco de pantalla (FPS). */
    private Timer timerAnimacion;
    /** Indica si se está ejecutando una animación visual entre dos estados lógicos. */
    private boolean animando = false;
    /** Indica si la simulación corre en modo automático continuo. */
    private boolean modoAutomatico = false;
    /** Contador del frame actual dentro de la transición de un tick. */
    private int frameActual = 0;
    /** Cantidad de frames visuales por cada "tick" lógico (suavidad). */
    private final int FRAMES_POR_TICK = 30; 

    /** Mapa para almacenar la posición inicial de los buses antes de un tick. */
    private Map<Integer, Point> posInicioBus = new HashMap<>();
    /** Mapa para almacenar la posición final de los buses después de un tick. */
    private Map<Integer, Point> posFinBus = new HashMap<>();

    /**
     * Constructor principal de la GUI.
     * Configura la ventana, los paneles y el timer de animación.
     *
     * @param simulador Instancia del simulador que contiene la lógica del negocio.
     */
    public TransporteGUI(Simulador simulador) {
        this.simulador = simulador;
        this.grafo = simulador.getGrafo();

        setTitle("Sistema de Transporte - Visualizador");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel Central (Mapa)
        panelGrafo = new GraphPanel();
        add(panelGrafo, BorderLayout.CENTER);

        // 2. Panel Derecho (Controles e Info)
        add(crearPanelControles(), BorderLayout.EAST);

        // 3. Configurar Timer de Animación (aprox 50 FPS)
        timerAnimacion = new Timer(20, e -> bucleAnimacion());
        
        // Inicializar posiciones lógicas para evitar errores en el primer pintado
        actualizarPosicionesLogicas(); 
        
        setVisible(true);
    }

    /**
     * Crea y configura el panel lateral derecho con los controles de usuario.
     * Incluye botones de simulación, selectores de ruta y áreas de texto informativo.
     *
     * @return JPanel configurado con los controles.
     */
    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, getHeight()));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Sección Simulación ---
        JPanel pSim = new JPanel(new GridLayout(4, 1, 5, 5));
        pSim.setBorder(new TitledBorder("Control Simulación"));
        
        lblTiempo = new JLabel("Tiempo: 0 ticks");
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTiempo.setHorizontalAlignment(SwingConstants.CENTER);
        
        btnIniciar = new JButton("▶ Iniciar Automático");
        btnDetener = new JButton("⏸ Pausar");
        btnPaso = new JButton("⏯ Avanzar 1 Paso");
        
        btnDetener.setEnabled(false);

        pSim.add(lblTiempo);
        pSim.add(btnIniciar);
        pSim.add(btnDetener);
        pSim.add(btnPaso);

        // --- Sección Rutas (Dijkstra) ---
        JPanel pRuta = new JPanel(new GridLayout(4, 1, 5, 5));
        pRuta.setBorder(new TitledBorder("Consultar Ruta (Dijkstra)"));

        comboOrigen = new JComboBox<>();
        comboDestino = new JComboBox<>();
        
        // Llenar combos con nombres de nodos
        for(Nodo n : grafo.getParaderos()) {
            comboOrigen.addItem(n.getNombre());
            comboDestino.addItem(n.getNombre());
        }
        
        JButton btnCalcular = new JButton("Buscar Camino Más Corto");
        txtRuta = new JTextArea(3, 20);
        txtRuta.setEditable(false);
        txtRuta.setLineWrap(true);
        txtRuta.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        pRuta.add(new JLabel("Origen / Destino:"));
        JPanel combos = new JPanel(new GridLayout(1,2));
        combos.add(comboOrigen);
        combos.add(comboDestino);
        pRuta.add(combos);
        pRuta.add(btnCalcular);
        pRuta.add(new JScrollPane(txtRuta));

        // --- Sección Estadísticas ---
        txtInfo = new JTextArea(10, 20);
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBorder(new TitledBorder("Estado del Sistema"));

        // Agregar todo al panel lateral
        panel.add(pSim);
        panel.add(Box.createVerticalStrut(10));
        panel.add(pRuta);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollInfo);

        // --- Configuración de Eventos (Listeners) ---
        btnIniciar.addActionListener(e -> {
            modoAutomatico = true;
            btnIniciar.setEnabled(false);
            btnPaso.setEnabled(false);
            btnDetener.setEnabled(true);
            if (!animando) ejecutarTickLogico();
        });

        btnDetener.addActionListener(e -> {
            modoAutomatico = false;
            btnIniciar.setEnabled(true);
            btnPaso.setEnabled(true);
            btnDetener.setEnabled(false);
        });

        btnPaso.addActionListener(e -> {
            if (!animando) ejecutarTickLogico();
        });

        btnCalcular.addActionListener(e -> calcularRuta());

        return panel;
    }

    /**
     * Método invocado periódicamente por el Timer para actualizar la animación.
     * Calcula la interpolación visual entre paradas y repinta el panel.
     */
    private void bucleAnimacion() {
        if (animando) {
            frameActual++;
            if (frameActual >= FRAMES_POR_TICK) {
                // Terminó la animación del tick actual
                animando = false;
                timerAnimacion.stop();
                panelGrafo.repaint();
                
                // Si estamos en automático, lanzamos el siguiente tick inmediatamente
                if (modoAutomatico) {
                    ejecutarTickLogico();
                }
            } else {
                // Seguimos animando (interpolación)
                panelGrafo.repaint();
            }
        }
    }

    /**
     * Ejecuta un paso lógico (tick) en el simulador y prepara los datos
     * para la animación visual.
     */
    private void ejecutarTickLogico() {
        // 1. Guardar posición inicial (antes del movimiento)
        posInicioBus.clear();
        for (Bus b : simulador.getBuses()) {
            Nodo n = b.getNodoActual();
            posInicioBus.put(b.getId(), new Point((int)n.getPosicionX(), (int)n.getPosicionY()));
        }

        // 2. Avanzar la lógica del simulador
        simulador.tick();
        lblTiempo.setText("Tiempo: " + simulador.getTiempoActual() + " ticks");
        actualizarInfoPanel();

        // 3. Guardar posición final (después del movimiento)
        posFinBus.clear();
        for (Bus b : simulador.getBuses()) {
            Nodo n = b.getNodoActual();
            posFinBus.put(b.getId(), new Point((int)n.getPosicionX(), (int)n.getPosicionY()));
        }

        // 4. Iniciar la secuencia de animación
        frameActual = 0;
        animando = true;
        timerAnimacion.start();
    }
    
    /**
     * Actualiza los mapas de posición lógica sin realizar animación.
     * Útil para la inicialización.
     */
    private void actualizarPosicionesLogicas() {
        for (Bus b : simulador.getBuses()) {
            Nodo n = b.getNodoActual();
            Point p = new Point((int)n.getPosicionX(), (int)n.getPosicionY());
            posInicioBus.put(b.getId(), p);
            posFinBus.put(b.getId(), p);
        }
    }

    /**
     * Obtiene los paraderos seleccionados en los comboboxes y ejecuta Dijkstra.
     * Muestra el resultado en el área de texto.
     */
    private void calcularRuta() {
        String nOrigen = (String) comboOrigen.getSelectedItem();
        String nDestino = (String) comboDestino.getSelectedItem();
        
        Nodo nodoO = null, nodoD = null;
        
        // Buscar objetos Nodo por nombre
        for(Nodo n : grafo.getParaderos()) {
            if(n.getNombre().equals(nOrigen)) nodoO = n;
            if(n.getNombre().equals(nDestino)) nodoD = n;
        }

        if (nodoO != null && nodoD != null) {
            List<Nodo> camino = grafo.dijkstra(nodoO, nodoD);
            
            if (camino.isEmpty()) {
                txtRuta.setText("No hay ruta disponible o es el mismo nodo.");
            } else {
                StringBuilder sb = new StringBuilder();
                int costoTotal = 0;
                
                // Calcular costo total sumando los tiempos de los arcos
                for(int i = 0; i < camino.size() - 1; i++) {
                    for(Arco a : grafo.getAdyacentes(camino.get(i))) {
                        if(a.getDestino() == camino.get(i+1)) {
                            costoTotal += a.getTiempo();
                            break;
                        }
                    }
                }
                
                sb.append("Costo: ").append(costoTotal).append(" min\n");
                for (int i = 0; i < camino.size(); i++) {
                    sb.append(camino.get(i).getNombre());
                    if (i < camino.size() - 1) sb.append(" -> ");
                }
                txtRuta.setText(sb.toString());
            }
        }
    }

    /**
     * Actualiza el área de texto con información en tiempo real sobre
     * los buses y las colas de pasajeros en los paraderos.
     */
    private void actualizarInfoPanel() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ESTADO BUSES ---\n");
        for (Bus b : simulador.getBuses()) {
            sb.append("Bus ").append(b.getId())
            .append(" (").append(b.getCapacidadActual()).append("/").append(b.getCapacidadMax()).append(" pas)\n")
            .append("  -> ").append(b.getNodoActual().getNombre()).append("\n");
        }
        sb.append("\n--- PARADEROS (Colas) ---\n");
        for (Nodo n : grafo.getParaderos()) {
            sb.append(n.getNombre()).append(": ").append(n.getEsperandoCuantos()).append(" esperando.\n");
        }
        txtInfo.setText(sb.toString());
    }

    // ==========================================
    // CLASE INTERNA: PANEL DE DIBUJO
    // ==========================================
    
    /**
     * Panel personalizado para renderizar el grafo y los elementos móviles.
     */
    private class GraphPanel extends JPanel {
        
        public GraphPanel() {
            setBackground(new Color(245, 245, 250)); // Fondo claro
        }

        /**
         * Método principal de pintado. Se invoca automáticamente con repaint().
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            // Activar suavizado (Antialiasing) para mejores gráficos
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Dibujar Conexiones (Arcos)
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.LIGHT_GRAY);
            for (Nodo n : grafo.getParaderos()) {
                for (Arco a : grafo.getAdyacentes(n)) {
                    Nodo dest = a.getDestino();
                    g2.draw(new Line2D.Double(n.getPosicionX(), n.getPosicionY(), dest.getPosicionX(), dest.getPosicionY()));
                    
                    // Dibujar tiempo (peso) en la mitad de la arista
                    double mx = (n.getPosicionX() + dest.getPosicionX()) / 2;
                    double my = (n.getPosicionY() + dest.getPosicionY()) / 2;
                    g2.setColor(Color.GRAY);
                    g2.drawString(a.getTiempo() + "m", (int)mx, (int)my);
                    g2.setColor(Color.LIGHT_GRAY); // Restaurar color
                }
            }

            // 2. Dibujar Nodos (Paraderos)
            for (Nodo n : grafo.getParaderos()) {
                int x = (int) n.getPosicionX();
                int y = (int) n.getPosicionY();
                int radio = 24;

                g2.setColor(new Color(100, 149, 237)); // Azul Cornflower
                g2.fillOval(x - radio/2, y - radio/2, radio, radio);
                
                g2.setColor(Color.BLACK);
                g2.drawOval(x - radio/2, y - radio/2, radio, radio);
                g2.drawString(n.getNombre(), x - 10, y - 15);

                // Indicador de pasajeros esperando (círculo rojo pequeño)
                if (n.getEsperandoCuantos() > 0) {
                    g2.setColor(Color.RED);
                    g2.fillOval(x + 5, y - 12, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("" + n.getEsperandoCuantos(), x + 8, y);
                }
                g2.setColor(Color.BLACK); 
            }

            // 3. Dibujar Buses (con interpolación de movimiento)
            for (Bus b : simulador.getBuses()) {
                Point p1 = posInicioBus.get(b.getId());
                Point p2 = posFinBus.get(b.getId());

                double x, y;
                // Calcular posición intermedia si se está animando
                if (animando && p1 != null && p2 != null) {
                    double t = (double) frameActual / FRAMES_POR_TICK;
                    x = p1.x + (p2.x - p1.x) * t;
                    y = p1.y + (p2.y - p1.y) * t;
                } else {
                    Nodo actual = b.getNodoActual();
                    x = actual.getPosicionX();
                    y = actual.getPosicionY();
                }

                // Cuerpo del bus
                g2.setColor(new Color(255, 69, 0)); // Naranja Rojizo
                g2.fillRoundRect((int)x - 10, (int)y - 10, 20, 20, 5, 5);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect((int)x - 10, (int)y - 10, 20, 20, 5, 5);
                
                // Texto ID del Bus
                g2.setColor(Color.BLACK);
                g2.drawString("B" + b.getId(), (int)x - 8, (int)y + 4);
            }
        }
    }

    // ==========================================
    // MAIN DE PRUEBA
    // ==========================================
    
    /**
     * Método principal para ejecutar la GUI de forma independiente con datos de prueba.
     * Crea un grafo básico, buses y pasajeros iniciales.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Crear grafo
            Grafo grafo = new Grafo();

            // 2. Crear paradas (Coordenadas ajustadas para visualización)
            Nodo parada1 = new Nodo(1, "Centro", 200.0, 150.0);
            Nodo parada2 = new Nodo(2, "Terminal", 500.0, 150.0);
            Nodo parada3 = new Nodo(3, "Hospital", 500.0, 450.0);
            Nodo parada4 = new Nodo(4, "Universidad", 200.0, 450.0);

            grafo.agregarParadero(parada1);
            grafo.agregarParadero(parada2);
            grafo.agregarParadero(parada3);
            grafo.agregarParadero(parada4);

            // 3. Crear arcos (Ida y Vuelta)
            grafo.agregarArco(parada1, parada2, 10);
            grafo.agregarArco(parada2, parada3, 8);
            grafo.agregarArco(parada3, parada4, 12);
            grafo.agregarArco(parada4, parada1, 15);
            
            grafo.agregarArco(parada2, parada1, 10);
            grafo.agregarArco(parada3, parada2, 8);
            grafo.agregarArco(parada4, parada3, 12);
            grafo.agregarArco(parada1, parada4, 15);

            // 4. Definir Rutas
            List<Nodo> rutaIda = new ArrayList<>();
            rutaIda.add(parada1); rutaIda.add(parada2); rutaIda.add(parada3); rutaIda.add(parada4);

            List<Nodo> rutaVuelta = new ArrayList<>();
            rutaVuelta.add(parada4); rutaVuelta.add(parada3); rutaVuelta.add(parada2); rutaVuelta.add(parada1);

            // 5. Instanciar Simulador
            Simulador simulador = new Simulador(grafo);

            // 6. Configurar Buses
            Bus bus1 = new Bus(1, 40, rutaIda, rutaVuelta);
            Bus bus2 = new Bus(2, 40, rutaIda, rutaVuelta); 
            
            simulador.agregarBus(bus1);
            simulador.agregarBus(bus2);

            // 7. Agregar Pasajeros Iniciales
            parada1.agregarPasajero(new Pasajero(101, "Terminal", System.currentTimeMillis()));
            parada3.agregarPasajero(new Pasajero(102, "Centro", System.currentTimeMillis()));

            // 8. Iniciar Ventana
            new TransporteGUI(simulador);
        });
    }
}
