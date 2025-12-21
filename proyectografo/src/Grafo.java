package src;

import java.util.*;

/**
 * Representa el grafo de paradas en la red de transporte.
 * Permite calcular rutas óptimas entre paradas usando algoritmo de Dijkstra.
 */
public class Grafo {
    private List<Nodo> paraderos;
    private Map<Nodo, List<Arco>> adj;  // lista de adyacencia

    public Grafo() {
        this.paraderos = new ArrayList<>();
        this.adj = new HashMap<>();
    }

    /**
     * Agrega una parada (nodo) al grafo.
     */
    public void agregarParadero(Nodo nodo) {
        paraderos.add(nodo);
        adj.put(nodo, new ArrayList<>());
    }

    /**
     * Agrega una arista (conexión) entre dos paradas con un tiempo específico.
     */
    public void agregarArco(Nodo origen, Nodo destino, int tiempo) {
        Arco arco = new Arco(origen, destino, tiempo);
        adj.get(origen).add(arco);
    }

    public List<Nodo> getParaderos() {
        return paraderos;
    }

    public Nodo getParadero(int id) {
        for (Nodo n : paraderos) {
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }

    public List<Arco> getAdyacentes(Nodo nodo) {
        return adj.getOrDefault(nodo, new ArrayList<>());
    }

    /**
     * Implementa el algoritmo de Dijkstra para encontrar la ruta más corta
     * entre dos paradas.
     */
    public List<Nodo> dijkstra(Nodo origen, Nodo destino) {
        Map<Nodo, Integer> distancias = new HashMap<>();
        Map<Nodo, Nodo> anterior = new HashMap<>();
        PriorityQueue<NodoDistancia> pq = new PriorityQueue<>();

        // Inicializar distancias
        for (Nodo n : paraderos) {
            distancias.put(n, Integer.MAX_VALUE);
            anterior.put(n, null);
        }
        distancias.put(origen, 0);
        pq.offer(new NodoDistancia(origen, 0));

        // Dijkstra
        while (!pq.isEmpty()) {
            NodoDistancia actual = pq.poll();
            if (actual.distancia > distancias.get(actual.nodo)) {
                continue;
            }

            for (Arco arco : adj.get(actual.nodo)) {
                Nodo vecino = arco.getDestino();
                int nuevaDistancia = distancias.get(actual.nodo) + arco.getTiempo();

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    anterior.put(vecino, actual.nodo);
                    pq.offer(new NodoDistancia(vecino, nuevaDistancia));
                }
            }
        }

        // Reconstruir camino
        List<Nodo> camino = new ArrayList<>();
        Nodo actual = destino;
        while (actual != null) {
            camino.add(0, actual);
            actual = anterior.get(actual);
        }

        return camino;
    }
    /**
     * Obtiene el arco entre dos nodos, si existe.
     */
    public Arco getArco(Nodo origen, Nodo destino) {
        for (Arco a : getAdyacentes(origen)) {
            if (a.getDestino().equals(destino)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Clase auxiliar para el algoritmo de Dijkstra.
     */
    private static class NodoDistancia implements Comparable<NodoDistancia> {
        Nodo nodo;
        int distancia;

        NodoDistancia(Nodo nodo, int distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDistancia otra) {
            return Integer.compare(this.distancia, otra.distancia);
        }
    }
}
