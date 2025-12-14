package Transporte;

import java.util.*;

/**
 * Simulador de la red de transporte.
 * Orquesta el movimiento de buses, la generación de pasajeros y el tracking de tiempos de espera.
 */
public class Simulador {
    private Grafo grafo;
    private List<Bus> buses;
    private int tiempoActual;  // tiempo simulado en minutos
    private Map<Nodo, List<Long>> tiemposEspera;  // historial de tiempos de espera por parada
    private Random random;

    public Simulador(Grafo grafo) {
        this.grafo = grafo;
        this.buses = new ArrayList<>();
        this.tiempoActual = 0;
        this.tiemposEspera = new HashMap<>();
        this.random = new Random();

        // Inicializar tiempos de espera para cada parada
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
     * Ejecuta un tick de la simulación (representa un cierto período de tiempo).
     * En cada tick:
     * 1. Se generan nuevos pasajeros en paradas (aleatoriamente)
     * 2. Los buses bajan pasajeros
     * 3. Los buses suben pasajeros
     * 4. Los buses avanzan
     * 5. Se registran tiempos de espera
     */
    public void tick() {
        // 1. Generar pasajeros aleatoriamente
        generarPasajeros();

        // 2. Buses bajan y suben pasajeros
        for (Bus bus : buses) {
            bus.bajarPasajeros();
            bus.subirPasajeros();
        }

        // 3. Buses avanzan a la siguiente parada
        for (Bus bus : buses) {
            bus.avanzar();
        }

        // 4. Registrar tiempos de espera
        registrarTiemposEspera();

        // Incrementar tiempo
        tiempoActual++;
    }

    /**
     * Genera pasajeros aleatoriamente en paradas.
     */
    private void generarPasajeros() {
        for (Nodo parada : grafo.getParaderos()) {
            // 30% de probabilidad de que llegue un pasajero
            if (random.nextDouble() < 0.3) {
                int idPasajero = random.nextInt(10000);
                // Elegir un destino diferente al origen
                Nodo destino = parada;
                while (destino == parada) {
                    destino = grafo.getParaderos().get(random.nextInt(grafo.getParaderos().size()));
                }
                Pasajero p = new Pasajero(idPasajero, destino.getNombre(), System.currentTimeMillis());
                parada.agregarPasajero(p);
            }
        }
    }

    /**
     * Registra el tiempo de espera de los pasajeros en cada parada.
     */
    private void registrarTiemposEspera() {
        for (Nodo parada : grafo.getParaderos()) {
            long tiempoPromedio = 0;
            if (!parada.getCola().isEmpty()) {
                for (Pasajero p : parada.getCola()) {
                    tiempoPromedio += p.getTiempoEsperaMillis();
                }
                tiempoPromedio /= parada.getCola().size();
                tiemposEspera.get(parada).add(tiempoPromedio);
            }
        }
    }

    /**
     * Ejecuta la simulación por N ticks.
     */
    public void simular(int ticks) {
        for (int i = 0; i < ticks; i++) {
            tick();
        }
    }

    /**
     * Retorna estadísticas de la simulación.
     */
    public void imprimirEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS DE SIMULACIÓN ===");
        System.out.println("Tiempo simulado: " + tiempoActual + " ticks");
        System.out.println("\nTiempos promedio de espera por parada:");

        for (Nodo parada : grafo.getParaderos()) {
            List<Long> tiempos = tiemposEspera.get(parada);
            if (!tiempos.isEmpty()) {
                long promedio = tiempos.stream().mapToLong(Long::longValue).sum() / tiempos.size();
                System.out.println("  " + parada.getNombre() + ": " + promedio + " ms (muestras: " + tiempos.size() + ")");
            } else {
                System.out.println("  " + parada.getNombre() + ": sin datos");
            }
        }

        System.out.println("\nEstado actual de buses:");
        for (Bus bus : buses) {
            System.out.println("  Bus " + bus.getId() + 
                             ": " + bus.getCapacidadActual() + "/" + bus.getCapacidadMax() + 
                             " pasajeros, en parada " + bus.getNodoActual().getNombre());
        }

        System.out.println("\nPasajeros esperando por parada:");
        for (Nodo parada : grafo.getParaderos()) {
            System.out.println("  " + parada.getNombre() + ": " + parada.getEsperandoCuantos() + " pasajeros");
        }
    }

    public int getTiempoActual() {
        return tiempoActual;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public Grafo getGrafo() {
        return grafo;
    }
}
