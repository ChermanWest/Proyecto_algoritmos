package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un bus que se desplaza por una ruta
 * avanzando 10 minutos por tick.
 */
public class Bus {

    private int id;
    private int capacidadMax;
    private List<Pasajero> pasajeros;

    private List<Nodo> rutaIda;
    private List<Nodo> rutaVuelta;
    private List<Nodo> rutaActual;

    private int indiceActual;
    private boolean enIda;

    // Movimiento temporal
    private Nodo nodoActual;
    private Nodo nodoSiguiente;
    private int tiempoRestanteArco;
    private int tiempoTotalArco;

    public Bus(int id, int capacidadMax, List<Nodo> rutaIda, List<Nodo> rutaVuelta) {
        this.id = id;
        this.capacidadMax = capacidadMax;
        this.rutaIda = rutaIda;
        this.rutaVuelta = rutaVuelta;
        this.rutaActual = rutaIda;

        this.pasajeros = new ArrayList<>();
        this.indiceActual = 0;
        this.enIda = true;

        this.nodoActual = rutaActual.get(0);
        this.nodoSiguiente = rutaActual.get(1);
        this.tiempoRestanteArco = 0;
        this.tiempoTotalArco = 0;
    }

    // ================= GETTERS =================

    public int getId() {
        return id;
    }

    public int getCapacidadActual() {
        return pasajeros.size();
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public Nodo getNodoActual() {
        return nodoActual;
    }

    public Nodo getNodoSiguiente() {
        return nodoSiguiente;
    }

    /**
     * Retorna progreso del arco (0.0 a 1.0)
     */
    public double getProgreso() {
        if (tiempoTotalArco == 0) return 0.0;
        return 1.0 - ((double) tiempoRestanteArco / tiempoTotalArco);
    }

    // ================= MOVIMIENTO =================

    /**
     * Avanza 1 tick (10 minutos)
     */
    public void avanzar(Grafo grafo) {

        int minutosDisponibles = 10; // 1 tick = 10 min

        while (minutosDisponibles > 0) {

            // Si no estamos en un arco, iniciarlo
            if (tiempoRestanteArco <= 0) {
                Arco arco = grafo.getArco(nodoActual, nodoSiguiente);
                tiempoTotalArco = arco.getTiempo();
                tiempoRestanteArco = tiempoTotalArco;
            }

            // Caso 1: el arco se completa en este tick
            if (tiempoRestanteArco <= minutosDisponibles) {

                minutosDisponibles -= tiempoRestanteArco;
                tiempoRestanteArco = 0;

                // Llegar al nodo
                nodoActual = nodoSiguiente;
                bajarPasajeros();
                subirPasajeros();
                avanzarIndiceRuta();

            } else {
                // Caso 2: NO alcanza el tiempo para completar el arco
                tiempoRestanteArco -= minutosDisponibles;
                minutosDisponibles = 0;
            }
        }
    }

    private void avanzarIndiceRuta() {
        indiceActual++;

        if (indiceActual >= rutaActual.size() - 1) {
            enIda = !enIda;
            rutaActual = enIda ? rutaIda : rutaVuelta;
            indiceActual = 0;
        }

        nodoSiguiente = rutaActual.get(indiceActual + 1);
        tiempoRestanteArco = 0;
        tiempoTotalArco = 0;
    }

    // ================= PASAJEROS =================

    public void subirPasajeros() {
        while (!nodoActual.getCola().isEmpty() && pasajeros.size() < capacidadMax) {
            pasajeros.add(nodoActual.removerPasajero());
        }
    }

    public void bajarPasajeros() {
        List<Pasajero> bajar = new ArrayList<>();
        for (Pasajero p : pasajeros) {
            if (p.getDestino().equals(nodoActual.getNombre())) {
                bajar.add(p);
            }
        }
        pasajeros.removeAll(bajar);
    }
}
