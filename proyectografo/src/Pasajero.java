package src;

/**
 * Representa a un pasajero dentro del sistema.
 * Cada pasajero posee un identificador único y un destino asociado.
 */
public class Pasajero {
    private int id;
    private String destino;
    private long tiempoInicio;
    private long tiempoLlegada;

    /**
     * Crea una nueva instancia de Pasajero con un id y un destino especificados.
     *
     * @param id            Identificador único del pasajero.
     * @param destino       Destino al que se dirige el pasajero.
     * @param tiempoInicio  Tiempo (en ms) en que el pasajero comenzó su viaje.
     */
    public Pasajero(int id, String destino, long tiempoInicio) {
        this.id = id;
        this.destino = destino;
        this.tiempoInicio = tiempoInicio;
        this.tiempoLlegada = -1; // Se asigna cuando sube al bus
    }

    public int getId() {
        return id;
    }

    public String getDestino() {
        return destino;
    }

    public long getTiempoInicio() {
        return tiempoInicio;
    }

    public long getTiempoLlegada() {
        return tiempoLlegada;
    }

    public void setTiempoLlegada(long tiempoLlegada) {
        this.tiempoLlegada = tiempoLlegada;
    }
}