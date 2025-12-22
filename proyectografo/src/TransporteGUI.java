package src;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import javax.swing.Timer;
import java.util.List;

/**
 * Interfaz Gráfica del sistema de transporte.
 * Visualiza el grafo, los buses y controla la simulación.
 * Ahora incluye cálculo de ruta más corta (Dijkstra).
 *
 * 1 tick = 10 minutos de recorrido.
 */
public class TransporteGUI extends JFrame {

    private Simulador simulador;
    private Grafo grafo;

    private GraphPanel panelGrafo;

    // Componentes GUI Simulación
    private JLabel lblTiempo;
    private JButton btnIniciar;
    private JButton btnPausar;
    private JButton btnPaso;

    // Componentes GUI Ruta (NUEVO)
    private JComboBox<String> cbOrigen;
    private JComboBox<String> cbDestino;
    private JLabel lblResultadoRuta;
    private List<Nodo> rutaResaltada; // Para guardar el camino a dibujar

    // Control animación
    private Timer timer;

    public TransporteGUI(Simulador simulador) {
        this.simulador = simulador;
        this.grafo = simulador.getGrafo();
        this.rutaResaltada = new ArrayList<>(); // Inicializar lista vacía

        setTitle("Sistema de Transporte - Visualizador y Rutas");
        setSize(1100, 750); // Un poco más ancho para los controles extra
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelGrafo = new GraphPanel();
        add(panelGrafo, BorderLayout.CENTER);
        add(crearPanelControl(), BorderLayout.EAST);

        // Timer: controla ticks automáticos
        timer = new Timer(600, e -> ejecutarTick());

        setVisible(true);
    }

    // ================= PANEL CONTROL =================

    private JPanel crearPanelControl() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, getHeight())); // Un poco más ancho
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- SUB-PANEL SIMULACIÓN ---
        JPanel pSim = new JPanel(new GridLayout(4, 1, 5, 5));
        pSim.setBorder(new TitledBorder("Control Simulación"));
        pSim.setMaximumSize(new Dimension(300, 150));

        lblTiempo = new JLabel("Tiempo: 0 ticks", SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 14));

        btnIniciar = new JButton("▶ Iniciar");
        btnPausar = new JButton("⏸ Pausar");
        btnPaso = new JButton("⏭ Avanzar 1 Tick");
        btnPausar.setEnabled(false);

        btnIniciar.addActionListener(e -> {
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnPaso.setEnabled(false);
            timer.start();
        });

        btnPausar.addActionListener(e -> {
            timer.stop();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            btnPaso.setEnabled(true);
        });

        btnPaso.addActionListener(e -> ejecutarTick());

        pSim.add(lblTiempo);
        pSim.add(btnIniciar);
        pSim.add(btnPausar);
        pSim.add(btnPaso);

        // --- SUB-PANEL RUTAS (NUEVO) ---
        JPanel pRuta = new JPanel(new GridLayout(6, 1, 5, 5));
        pRuta.setBorder(new TitledBorder("Buscador de Rutas"));
        pRuta.setMaximumSize(new Dimension(300, 200));

        // Llenar combos con nombres de nodos
        Vector<String> nombresNodos = new Vector<>();
        for(Nodo n : grafo.getParaderos()){
            nombresNodos.add(n.getNombre());
        }
        
        cbOrigen = new JComboBox<>(nombresNodos);
        cbDestino = new JComboBox<>(nombresNodos);
        
        JButton btnCalcular = new JButton("🔍 Calcular Ruta Corta");
        JButton btnLimpiar = new JButton("❌ Limpiar Ruta");
        lblResultadoRuta = new JLabel("Seleccione origen y destino", SwingConstants.CENTER);
        lblResultadoRuta.setFont(new Font("Arial", Font.PLAIN, 11));

        btnCalcular.addActionListener(e -> calcularDijkstra());
        
        btnLimpiar.addActionListener(e -> {
            rutaResaltada.clear();
            lblResultadoRuta.setText("");
            panelGrafo.repaint();
        });

        pRuta.add(new JLabel("Origen:"));
        pRuta.add(cbOrigen);
        pRuta.add(new JLabel("Destino:"));
        pRuta.add(cbDestino);
        pRuta.add(btnCalcular);
        pRuta.add(btnLimpiar);

        // Agregar al panel principal
        panel.add(pSim);
        panel.add(Box.createVerticalStrut(20)); // Espacio
        panel.add(pRuta);
        panel.add(lblResultadoRuta); // Resultado debajo
        
        return panel;
    }

    //LÓGICA DIJKSTRA 

    private void calcularDijkstra() {
        String nombreOrigen = (String) cbOrigen.getSelectedItem();
        String nombreDestino = (String) cbDestino.getSelectedItem();

        if (nombreOrigen == null || nombreDestino == null || nombreOrigen.equals(nombreDestino)) {
            lblResultadoRuta.setText("Seleccione nodos distintos.");
            return;
        }

        Nodo inicio = grafo.getParaderos().stream().filter(n -> n.getNombre().equals(nombreOrigen)).findFirst().orElse(null);
        Nodo fin = grafo.getParaderos().stream().filter(n -> n.getNombre().equals(nombreDestino)).findFirst().orElse(null);

        if (inicio == null || fin == null) return;

        // Estructuras para Dijkstra
        Map<Nodo, Integer> distancias = new HashMap<>();
        Map<Nodo, Nodo> previo = new HashMap<>();
        PriorityQueue<Nodo> cola = new PriorityQueue<>(Comparator.comparingInt(distancias::get));

        // Inicialización
        for (Nodo n : grafo.getParaderos()) {
            distancias.put(n, Integer.MAX_VALUE);
        }
        distancias.put(inicio, 0);
        cola.add(inicio);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            if (actual.equals(fin)) break; // Llegamos al destino

            if (distancias.get(actual) == Integer.MAX_VALUE) break; // No hay ruta

            for (Arco arco : grafo.getAdyacentes(actual)) {
                Nodo vecino = arco.getDestino();
                int nuevaDist = distancias.get(actual) + arco.getTiempo();

                if (nuevaDist < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDist);
                    previo.put(vecino, actual);
                    
                    // Actualizar cola (remover y agregar para reordenar)
                    cola.remove(vecino);
                    cola.add(vecino);
                }
            }
        }

        // Reconstruir camino
        rutaResaltada.clear();
        if (distancias.get(fin) == Integer.MAX_VALUE) {
            lblResultadoRuta.setText("No hay ruta posible.");
        } else {
            Nodo paso = fin;
            while (paso != null) {
                rutaResaltada.add(0, paso); // Insertar al inicio
                paso = previo.get(paso);
            }
            lblResultadoRuta.setText("Tiempo estimado: " + distancias.get(fin) + " min");
        }
        
        panelGrafo.repaint();
    }

    // SIMULACIÓN 

    private void ejecutarTick() {
        simulador.tick();
        lblTiempo.setText(
                "Tiempo: " + simulador.getTiempoActual() + " ticks (10 min c/u)"
        );
        panelGrafo.repaint();
    }

    //  PANEL DE DIBUJO 

    private class GraphPanel extends JPanel {

        public GraphPanel() {
            setBackground(new Color(245, 245, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Dibujar arcos (Grafo base)
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.LIGHT_GRAY);

            for (Nodo n : grafo.getParaderos()) {
                for (Arco a : grafo.getAdyacentes(n)) {
                    Nodo d = a.getDestino();
                    g2.draw(new Line2D.Double(
                            n.getPosicionX(), n.getPosicionY(),
                            d.getPosicionX(), d.getPosicionY()
                    ));
                    // Peso
                    int mx = (int) ((n.getPosicionX() + d.getPosicionX()) / 2);
                    int my = (int) ((n.getPosicionY() + d.getPosicionY()) / 2);
                    g2.drawString(a.getTiempo() + "m", mx, my);
                }
            }

            // Dibujar RUTA RESALTADA
            if (!rutaResaltada.isEmpty()) {
                g2.setStroke(new BasicStroke(4)); // Línea más gruesa
                g2.setColor(new Color(50, 205, 50, 180)); // Verde lima semitransparente

                for (int i = 0; i < rutaResaltada.size() - 1; i++) {
                    Nodo n1 = rutaResaltada.get(i);
                    Nodo n2 = rutaResaltada.get(i + 1);
                    g2.draw(new Line2D.Double(
                            n1.getPosicionX(), n1.getPosicionY(),
                            n2.getPosicionX(), n2.getPosicionY()
                    ));
                }
                // Volver a trazo normal
                g2.setStroke(new BasicStroke(1));
            }

            // 2. Dibujar nodos
            for (Nodo n : grafo.getParaderos()) {
                int x = (int) n.getPosicionX();
                int y = (int) n.getPosicionY();

                g2.setColor(new Color(100, 149, 237));
                g2.fillOval(x - 12, y - 12, 24, 24);
                
                // Si el nodo es parte de la ruta, bordearlo de verde
                if(rutaResaltada.contains(n)){
                    g2.setColor(new Color(0, 100, 0));
                    g2.setStroke(new BasicStroke(3));
                } else {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1));
                }
                
                g2.drawOval(x - 12, y - 12, 24, 24);
                
                // Reset color texto
                g2.setColor(Color.BLACK);
                g2.drawString(n.getNombre(), x - 15, y - 15);

                if (n.getEsperandoCuantos() > 0) {
                    g2.setColor(Color.RED);
                    g2.fillOval(x + 6, y - 14, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.drawString(
                            String.valueOf(n.getEsperandoCuantos()),
                            x + 10, y - 2
                    );
                }
            }

            // Dibujar buses
            for (Bus b : simulador.getBuses()) {
                int offset = (b.getId() - 1) * 14; 

                Nodo a = b.getNodoActual();
                Nodo d = b.getNodoSiguiente();
                
                // Protección contra nulos al inicio
                if (a == null || d == null) continue; 
                
                double t = b.getProgreso();

                double x = a.getPosicionX() + (d.getPosicionX() - a.getPosicionX()) * t;
                double y = a.getPosicionY() + (d.getPosicionY() - a.getPosicionY()) * t;

                x += offset;
                y += offset;

                g2.setColor(new Color(255, 69, 0));
                g2.fillRoundRect((int) x - 10, (int) y - 10, 20, 20, 6, 6);
                g2.setColor(Color.BLACK);
                g2.drawString("B" + b.getId(), (int) x - 8, (int) y + 4);
            }
        }
    }

    // MAIN 
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Grafo grafo = new Grafo();

            Nodo estacion = new Nodo(1, "Estacion", 80, 140);
            Nodo centro = new Nodo(2, "Centro", 250, 140);
            Nodo parque = new Nodo(3, "Parque", 400, 80);
            Nodo terminal = new Nodo(4, "Terminal", 550, 140);
            Nodo museo = new Nodo(5, "Museo", 700, 140);

            Nodo plaza = new Nodo(6, "Plaza", 400, 280);
            Nodo universidad = new Nodo(7, "Universidad", 250, 420);
            Nodo hospital = new Nodo(8, "Hospital", 550, 420);
            Nodo mercado = new Nodo(9, "Mercado", 700, 420);
            Nodo aeropuerto = new Nodo(10, "Aeropuerto", 400, 520);

            grafo.agregarParadero(estacion);
            grafo.agregarParadero(centro);
            grafo.agregarParadero(parque);
            grafo.agregarParadero(terminal);
            grafo.agregarParadero(museo);
            grafo.agregarParadero(plaza);
            grafo.agregarParadero(universidad);
            grafo.agregarParadero(hospital);
            grafo.agregarParadero(mercado);
            grafo.agregarParadero(aeropuerto);

            // Horizontales superiores
            grafo.agregarArco(estacion, centro, 5);
            grafo.agregarArco(centro, parque, 6);
            grafo.agregarArco(parque, terminal, 7);
            grafo.agregarArco(terminal, museo, 4);

            // Verticales
            grafo.agregarArco(parque, plaza, 8);
            grafo.agregarArco(terminal, hospital, 8);
            grafo.agregarArco(centro, universidad, 10);

            // Inferiores
            grafo.agregarArco(universidad, hospital, 12);
            grafo.agregarArco(hospital, mercado, 5);
            grafo.agregarArco(universidad, aeropuerto, 7);
            grafo.agregarArco(aeropuerto, hospital, 6);

            // Diagonales
            grafo.agregarArco(plaza, universidad, 6);
            grafo.agregarArco(plaza, hospital, 6);
            grafo.agregarArco(plaza, aeropuerto, 6);

            // Arcos inversos
            grafo.agregarArco(centro, estacion, 5);
            grafo.agregarArco(parque, centro, 6);
            grafo.agregarArco(terminal, parque, 7);
            grafo.agregarArco(museo, terminal, 4);
            grafo.agregarArco(plaza, parque, 8);
            grafo.agregarArco(hospital, terminal, 8);
            grafo.agregarArco(hospital, universidad, 12);
            grafo.agregarArco(mercado, hospital, 5);
            grafo.agregarArco(aeropuerto, universidad, 7);
            grafo.agregarArco(hospital, aeropuerto, 6);
            grafo.agregarArco(universidad, plaza, 6);
            grafo.agregarArco(hospital, plaza, 6);
            grafo.agregarArco(aeropuerto, plaza, 6);

            List<Nodo> ida = Arrays.asList(
                    estacion, centro, parque, terminal, museo,
                    terminal, parque, plaza, universidad, aeropuerto
            );

            List<Nodo> vuelta = Arrays.asList(
                    aeropuerto, universidad, plaza, parque,
                    centro, estacion
            );

            Simulador simulador = new Simulador(grafo);
            simulador.agregarBus(new Bus(1, 40, ida, vuelta));

            new TransporteGUI(simulador);
        });
    }

}
