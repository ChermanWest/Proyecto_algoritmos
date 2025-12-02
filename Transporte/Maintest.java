package Transporte;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que demuestra el uso de la simulación de transporte.
 */
public class Maintest {
    public static void main(String[] args) {
        System.out.println("=== SIMULADOR DE RED DE TRANSPORTE ===\n");

        // 1. Crear el grafo de paradas
        Grafo grafo = new Grafo();

        // 2. Crear paradas (con id, nombre, y posiciones para visualización)
        Nodo parada1 = new Nodo(1, "Centro", 100.0, 100.0);
        Nodo parada2 = new Nodo(2, "Terminal", 200.0, 100.0);
        Nodo parada3 = new Nodo(3, "Hospital", 200.0, 200.0);
        Nodo parada4 = new Nodo(4, "Universidad", 100.0, 200.0);

        grafo.agregarParadero(parada1);
        grafo.agregarParadero(parada2);
        grafo.agregarParadero(parada3);
        grafo.agregarParadero(parada4);

        // 3. Crear arcos (conexiones entre paradas con tiempos)
        grafo.agregarArco(parada1, parada2, 10);  // Centro a Terminal (10 min)
        grafo.agregarArco(parada2, parada3, 8);   // Terminal a Hospital (8 min)
        grafo.agregarArco(parada3, parada4, 12);  // Hospital a Universidad (12 min)
        grafo.agregarArco(parada4, parada1, 15);  // Universidad a Centro (15 min)
        // Arcos de vuelta
        grafo.agregarArco(parada2, parada1, 10);
        grafo.agregarArco(parada3, parada2, 8);
        grafo.agregarArco(parada4, parada3, 12);
        grafo.agregarArco(parada1, parada4, 15);

        // 4. Crear rutas (ida y vuelta) para los buses
        List<Nodo> rutaIda = new ArrayList<>();
        rutaIda.add(parada1);
        rutaIda.add(parada2);
        rutaIda.add(parada3);
        rutaIda.add(parada4);

        List<Nodo> rutaVuelta = new ArrayList<>();
        rutaVuelta.add(parada4);
        rutaVuelta.add(parada3);
        rutaVuelta.add(parada2);
        rutaVuelta.add(parada1);

        // 5. Crear simulador
        Simulador simulador = new Simulador(grafo);

        // 6. Crear y agregar buses
        Bus bus1 = new Bus(1, 40, rutaIda, rutaVuelta);
        Bus bus2 = new Bus(2, 40, rutaIda, rutaVuelta);

        simulador.agregarBus(bus1);
        simulador.agregarBus(bus2);

        // 7. Agregar pasajeros iniciales en algunas paradas
        System.out.println("Agregando pasajeros iniciales...");
        parada1.agregarPasajero(new Pasajero(101, "Terminal", System.currentTimeMillis()));
        parada1.agregarPasajero(new Pasajero(102, "Hospital", System.currentTimeMillis()));
        parada2.agregarPasajero(new Pasajero(103, "Universidad", System.currentTimeMillis()));
        parada3.agregarPasajero(new Pasajero(104, "Centro", System.currentTimeMillis()));

        // 8. Ejecutar la simulación
        System.out.println("Iniciando simulación...\n");
        simulador.simular(50);  // Simular 50 ticks

        // 9. Imprimir estadísticas
        simulador.imprimirEstadisticas();

        // 10. Prueba de Dijkstra
        System.out.println("\n=== Prueba sistema DIJKSTRA ===");
        List<Nodo> ruta = grafo.dijkstra(parada1, parada3);
        System.out.println("Ruta más corta de " + parada1.getNombre() + " a " + parada3.getNombre() + ":");
        for (int i = 0; i < ruta.size(); i++) {
            System.out.print(ruta.get(i).getNombre());
            if (i < ruta.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
    }
}
