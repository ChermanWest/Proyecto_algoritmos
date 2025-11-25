package Transporte;

import java.util.List;

class Bus {
    int capacidad;                   // límite de pasajeros dentro del bus
    List<Pasajero> pasajeros;        // pasajeros que van a bordo
    private List<Node> ruta;                          // lista de paraderos (IDs) del camino más corto
    private boolean ida;  // índice en la ruta
}
