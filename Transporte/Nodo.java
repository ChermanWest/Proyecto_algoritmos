package Transporte;

public class Nodo {
    private String nombre;
    private Nodo siguiente;
    private Nodo anterior;

    public Nodo(String nombre) {
        this.nombre = nombre;
        this.siguiente = null;
        this.anterior = null;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public Nodo getAnterior() {
        return anterior;
    }
}