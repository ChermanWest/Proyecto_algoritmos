package Transporte;

import java.time.Instant;
import java.time.Duration;

/**
 * Representa a un pasajero dentro del sistema.
 * Cada pasajero posee un identificador único y un destino asociado.
 */
public class Pasajero {
    private int id;
    private String destino;
    private Instant tiempoInicio;
    private Instant tiempoLlegada; // null mientras espera / en viaje

    /**
     * Crea una nueva instancia de Pasajero con un id y un destino especificados.
     *
     * @param id            Identificador único del pasajero.
     * @param destino       Destino al que se dirige el pasajero.
     * @param tiempoInicio  Tiempo (en ms) en que el pasajero comenzó su viaje.
     */
    /**
     * Constructor histórico compatible que acepta milisegundos desde epoch.
     */
    public Pasajero(int id, String destino, long tiempoInicio) {
        this.id = id;
        this.destino = destino;
        this.tiempoInicio = Instant.ofEpochMilli(tiempoInicio);
        this.tiempoLlegada = null; // Se asigna cuando sube al bus
    }

    /**
     * Constructor más conveniente que añade el tiempo de inicio actual.
     */
    public Pasajero(int id, String destino) {
        this(id, destino, System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public String getDestino() {
        return destino;
    }


    /**
     * Retorna el tiempo de inicio en milisegundos (epoch) por compatibilidad.
     */
    public long getTiempoInicio() {
        return tiempoInicio.toEpochMilli();
    }

    /**
     * Retorna el instante de inicio como `Instant`.
     */
    public Instant getTiempoInicioInstant() {
        return tiempoInicio;
    }

    /**
     * Retorna el instante de llegada en milisegundos (epoch) o -1 si no ha llegado.
     */
    public long getTiempoLlegada() {
        return tiempoLlegada == null ? -1 : tiempoLlegada.toEpochMilli();
    }

    /**
     * Retorna el instante de llegada como `Instant`, o null si aún no ha llegado.
     */
    public Instant getTiempoLlegadaInstant() {
        return tiempoLlegada;
    }

    public void setTiempoLlegada(long tiempoLlegada) {
        this.tiempoLlegada = Instant.ofEpochMilli(tiempoLlegada);
    }

    /**
     * Establece la llegada a partir de un Instant (más idiomático para Java 8+).
     */
    public void setTiempoLlegadaInstant(Instant tiempoLlegada) {
        this.tiempoLlegada = tiempoLlegada;
    }

    /**
     * Retorna el tiempo de espera (en milisegundos) desde que creó el pasajero hasta su llegada
     * o hasta el momento actual si aún no ha llegado.
     */
    public long getTiempoEsperaMillis() {
        Instant fin = tiempoLlegada == null ? Instant.now() : tiempoLlegada;
        return Duration.between(tiempoInicio, fin).toMillis();
    }

    /**
     * Retorna el tiempo de espera en minutos redondeando hacia abajo.
     */
    public long getTiempoEsperaMinutes() {
        return getTiempoEsperaMillis() / 60000;
    }
}