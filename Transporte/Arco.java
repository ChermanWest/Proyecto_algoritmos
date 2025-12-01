package Transporte;

/**
 * Representa una arista (conexión) entre dos paradas del grafo.
 */
public class Arco {
    private Nodo origen;
    private Nodo destino;
    private int tiempo;  // tiempo en minutos para recorrer esta arista

    public Arco(Nodo origen, Nodo destino, int tiempo) {
        this.origen = origen;
        this.destino = destino;
        this.tiempo = tiempo;
    }

    public Nodo getOrigen() {
        return origen;
    }

    public Nodo getDestino() {
        return destino;
    }

    public int getTiempo() {
        return tiempo;
    }
}
