package autonoma.edu.co.Torneos_api.Domain;

import java.util.Set;

public enum EstadoTorneo {
    ABIERTO,
    CERRADO,
    EN_CURSO,
    FINALIZADO;

    public boolean puedeCambiarA(EstadoTorneo nuevo) {
        return switch (this) {
            case ABIERTO -> nuevo == CERRADO;
            case CERRADO -> Set.of(ABIERTO, EN_CURSO).contains(nuevo);
            case EN_CURSO -> nuevo == FINALIZADO;
            case FINALIZADO -> false;
        };
    }

    public boolean admiteCambioDeConfiguracion() {
        return this == ABIERTO || this == CERRADO;
    }
}
