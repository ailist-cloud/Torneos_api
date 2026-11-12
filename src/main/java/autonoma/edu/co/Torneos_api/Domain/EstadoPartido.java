package autonoma.edu.co.Torneos_api.Domain;

/**
 * Estados de un partido y las transiciones permitidas entre ellos.
 * También define qué estados aceptan resultado (RN-02).
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public enum EstadoPartido {
    PROGRAMADO,
    EN_JUEGO,
    FINALIZADO,
    CANCELADO;

    /**
     * RN-02: un partido solo acepta resultado si está programado o en juego.
     *
     * @return {@code true} si se cumple la condición
     */
    public boolean aceptaResultado() {
        return this == PROGRAMADO || this == EN_JUEGO;
    }

    /**
     * Cambios permitidos con PATCH /api/partidos/{id}/estado:
     *   PROGRAMADO -> EN_JUEGO o CANCELADO
     *   EN_JUEGO   -> FINALIZADO (debe tener resultado) o CANCELADO
     *
     * @param nuevo estado al que se quiere pasar
     * @return {@code true} si se cumple la condición
     */
    public boolean puedeCambiarA(EstadoPartido nuevo) {
        switch (this) {
            case PROGRAMADO:
                return nuevo == EN_JUEGO || nuevo == CANCELADO;
            case EN_JUEGO:
                return nuevo == FINALIZADO || nuevo == CANCELADO;
            default:
                return false;
        }
    }
}
