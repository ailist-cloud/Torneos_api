package autonoma.edu.co.Torneos_api.Domain;

public enum EstadoPartido {
    PROGRAMADO,
    EN_JUEGO,
    FINALIZADO,
    CANCELADO;

    /** RN-02: un partido solo acepta resultado si está programado o en juego. */
    public boolean aceptaResultado() {
        return this == PROGRAMADO || this == EN_JUEGO;
    }

    /**
     * Cambios permitidos con PATCH /api/partidos/{id}/estado:
     *   PROGRAMADO -> EN_JUEGO o CANCELADO
     *   EN_JUEGO   -> FINALIZADO (debe tener resultado) o CANCELADO
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
