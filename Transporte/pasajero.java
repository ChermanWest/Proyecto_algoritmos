
package Transporte;
class pasajero {
    /**
 * Representa a un pasajero dentro del sistema.
 * Cada pasajero posee un identificador único y un destino asociado.
 */
    int id;
    String Destino;
    String TiempoLlegada;

    
    public pasajero(int id, String Destino, String TiempoLlegada) {
        this.id = id;
        this.Destino = Destino;
        this.TiempoLlegada = TiempoLlegada;
    /**
     * Crea una nueva instancia de Pasajero con un id y un destino especificados.
     *
     * @param id       Identificador único del pasajero.
     * @param destino  Destino al que se dirige el pasajero.
     * @param tiempoLlegada Tiempo en que el pasajero llegó al sistema, que avanza de 10 en 10 mins.
     */
    }
}