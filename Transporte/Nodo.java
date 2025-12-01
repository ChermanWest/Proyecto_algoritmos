package Transporte;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Representa una parada (paradero) en la red de transporte.
 * Cada parada mantiene una cola de pasajeros esperando subir al bus.
 */
public class Nodo {
    private int id;
    private String nombre;
    private Queue<Pasajero> cola;      // cola de pasajeros esperando
    private double posicionX;          // coordenada X (para visualización)
    private double posicionY;          // coordenada Y (para visualización)

    public Nodo(int id, String nombre, double posicionX, double posicionY) {
        this.id = id;
        this.nombre = nombre;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.cola = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPosicionX() {
        return posicionX;
    }

    public double getPosicionY() {
        return posicionY;
    }

    public Queue<Pasajero> getCola() {
        return cola;
    }

    /**
     * Agrega un pasajero a la cola de la parada.
     */
    public void agregarPasajero(Pasajero p) {
        cola.offer(p);
    }

    /**
     * Remueve y retorna el primer pasajero de la cola.
     */
    public Pasajero removerPasajero() {
        return cola.poll();
    }

    /**
     * Retorna la cantidad de pasajeros esperando en la parada.
     */
    public int getEsperandoCuantos() {
        return cola.size();
    }
}