package src;

import java.util.*;

/**
 * Simulador de la red de transporte.
 * Controla el avance del tiempo (ticks), la generación de pasajeros
 * y coordina el movimiento de los buses.
 *
 * 1 tick = 10 minutos de tiempo simulado.
 */
public class Simulador {

    private Grafo grafo;
    private List<Bus> buses;
    private int tiempoActual; // tiempo en TICKS
    private Map<Nodo, List<Integer>> tiemposEspera;
    private Random random;

    public Simulador(Grafo grafo) {
        this.grafo = grafo;
        this.buses = new ArrayList<>();
        this.tiempoActual = 0; // comienza en 0 ticks
        this.tiemposEspera = new HashMap<>();
        this.random = new Random();

        // Inicializar registro de espera por parada
        for (Nodo n : grafo.getParaderos()) {
            tiemposEspera.put(n, new ArrayList<>());
        }
    }

    /**
     * Agrega un bus a la simulación.
     */
    public void agregarBus(Bus bus) {
        buses.add(bus);
    }

    /**
     * Ejecuta un tick de simulación.
     * Representa 10 minutos de tiempo real.
     */
    public void tick() {

        // 1. Avanzar tiempo (1 tick = 10 minutos)
        tiempoActual++;

        // 2. Generar pasajeros aleatoriamente
        generarPasajeros();

        // 3. Avanzar buses (cada bus maneja su propio tiempo)
        for (Bus bus : buses) {
            bus.avanzar(grafo);
        }

        // 4. Registrar tiempos de espera
        registrarTiemposEspera();
    }

    /**
     * Genera pasajeros aleatoriamente en las paradas.
     */
    private void generarPasajeros() {
        for (Nodo parada : grafo.getParaderos()) {

            // 30% de probabilidad por tick
            if (random.nextDouble() < 0.3) {

                int idPasajero = random.nextInt(10000);

                Nodo destino;
                do {
                    destino = grafo.getParaderos()
                            .get(random.nextInt(grafo.getParaderos().size()));
                } while (destino == parada);

                Pasajero p = new Pasajero(
                        idPasajero,
                        destino.getNombre(),
                        System.currentTimeMillis()
                );

                parada.agregarPasajero(p);
            }
        }
    }

    /**
     * Registra el tiempo de espera promedio por parada.
     */
    private void registrarTiemposEspera() {
        for (Nodo parada : grafo.getParaderos()) {

            if (!parada.getCola().isEmpty()) {
                int total = 0;

                for (Pasajero p : parada.getCola()) {
                    total += (System.currentTimeMillis() - p.getTiempoInicio());
                }

                int promedio = total / parada.getCola().size();
                tiemposEspera.get(parada).add(promedio);
            }
        }
    }

    // ================= GETTERS =================

    /**
     * Retorna el tiempo actual en ticks.
     */
    public int getTiempoActual() {
        return tiempoActual;
    }

    /**
     * Retorna los buses activos.
     */
    public List<Bus> getBuses() {
        return buses;
    }

    /**
     * Retorna el grafo de la simulación.
     */
    public Grafo getGrafo() {
        return grafo;
    }
}
