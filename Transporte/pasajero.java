
package Transporte;
class Pasajero {
    /**
 * Representa a un pasajero dentro del sistema.
 * Cada pasajero posee un identificador único y un destino asociado.
 */
    int id;
    String Destino;
    String TiempoInicio;
    String TiempoLlegada;

    
    public Pasajero(int id, String Destino, String TiempoInicio, String TiempoLlegada) {
        this.id = id;
        this.Destino = Destino;
        this.TiempoInicio = TiempoInicio;
        this.TiempoLlegada = TiempoLlegada;
    /**
     * Crea una nueva instancia de Pasajero con un id y un destino especificados.
     *
     * @param id       Identificador único del pasajero.
     * @param destino  Destino al que se dirige el pasajero.
     * @param tiempoInicio Tiempo en que el pasajero comenzó su viaje.
     */
    }
}