package autonoma.edu.co.Torneos_api.Domain;

// Flujo: PROGRAMADO -> EN_JUEGO -> FINALIZADO. CANCELADO desde PROGRAMADO o EN_JUEGO.
public enum EstadoPartido {
    PROGRAMADO,
    EN_JUEGO,
    FINALIZADO,
    CANCELADO;

    // RN-02: solo se acepta resultado si está programado o en juego
    public boolean aceptaResultado() {
        return this == PROGRAMADO || this == EN_JUEGO;
    }

    // FINALIZADO solo se alcanza registrando el resultado
    public boolean puedeCambiarA(EstadoPartido nuevo) {
        return switch (this) {
            case PROGRAMADO -> nuevo == EN_JUEGO || nuevo == CANCELADO;
            case EN_JUEGO -> nuevo == CANCELADO;
            case FINALIZADO, CANCELADO -> false;
        };
    }
}
