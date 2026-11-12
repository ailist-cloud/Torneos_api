package autonoma.edu.co.Torneos_api.Domain;

public enum EstadoPartido {
    PROGRAMADO,
    EN_JUEGO,
    FINALIZADO,
    CANCELADO;


    public boolean aceptaResultado() {
        return this == PROGRAMADO || this == EN_JUEGO;
    }


    public boolean puedeCambiarA(EstadoPartido nuevo) {
        return switch (this) {
            case PROGRAMADO -> nuevo == EN_JUEGO || nuevo == CANCELADO;
            case EN_JUEGO -> nuevo == CANCELADO;
            case FINALIZADO, CANCELADO -> false;
        };
    }
}
