package Transporte;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un bus en la red de transporte.
 * El bus se mueve a través de una ruta predefinida, recogiendo y dejando pasajeros.
 */
public class Bus {
    private int id;
    private int capacidadMax;           // límite de pasajeros dentro del bus
    private List<Pasajero> pasajeros;  // pasajeros que van a bordo
    private List<Nodo> rutaIda;        // ruta de ida
    private List<Nodo> rutaVuelta;     // ruta de vuelta
    private int indiceActual;          // índice actual en la ruta
    private boolean enIda;             // true si está en ida, false si está en vuelta

    /**
     * Crea una nueva instancia de Bus con una capacidad específica y rutas definidas.
     *
     * @param id            Identificador único del bus.
     * @param capacidadMax  Capacidad máxima de pasajeros.
     * @param rutaIda       Lista de nodos para la ruta de ida.
     * @param rutaVuelta    Lista de nodos para la ruta de vuelta.
     */
    public Bus(int id, int capacidadMax, List<Nodo> rutaIda, List<Nodo> rutaVuelta) {
        this.id = id;
        this.capacidadMax = capacidadMax;
        this.rutaIda = rutaIda;
        this.rutaVuelta = rutaVuelta;
        this.pasajeros = new ArrayList<>();
        this.indiceActual = 0;
        this.enIda = true;  // comienza en ida
    }

    public int getId() {
        return id;
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public int getCapacidadActual() {
        return pasajeros.size();
    }

    public List<Pasajero> getPasajeros() {
        return pasajeros;
    }

    public Nodo getNodoActual() {
        if (enIda) {
            return rutaIda.get(indiceActual);
        } else {
            return rutaVuelta.get(indiceActual);
        }
    }

    /**
     * El bus avanza a la siguiente parada en su ruta.
     */
    public void avanzar() {
        List<Nodo> rutaActual = enIda ? rutaIda : rutaVuelta;
        if (indiceActual < rutaActual.size() - 1) {
            indiceActual++;
        } else {
            // cambiar de dirección
            enIda = !enIda;
            indiceActual = 0;
        }
    }

    /**
     * El bus intenta recoger pasajeros de la parada actual.
     */
    public void subirPasajeros() {
        Nodo actual = getNodoActual();
        while (!actual.getCola().isEmpty() && pasajeros.size() < capacidadMax) {
            Pasajero p = actual.removerPasajero();
            pasajeros.add(p);
        }
    }

    /**
     * El bus deja bajar a los pasajeros cuyo destino es la parada actual.
     */
    public void bajarPasajeros() {
        Nodo actual = getNodoActual();
        List<Pasajero> bajar = new ArrayList<>();
        for (Pasajero p : pasajeros) {
            if (p.getDestino().equals(actual.getNombre())) {
                p.setTiempoLlegada(System.currentTimeMillis());
                bajar.add(p);
            }
        }
        pasajeros.removeAll(bajar);
    }
}