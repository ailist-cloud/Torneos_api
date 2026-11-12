package autonoma.edu.co.Torneos_api.Domain;

/**
 * Estados de un torneo (ciclo de vida) y las transiciones permitidas.
 * Define cuándo se puede reemplazar la configuración y cuándo se admiten inscripciones (RN-06).
 */
public enum EstadoTorneo {
    ABIERTO,     // recibe inscripciones
    CERRADO,     // inscripciones cerradas, se arma el calendario
    EN_CURSO,    // se están jugando los partidos
    FINALIZADO;  // ya no se modifica nada

    public boolean puedeCambiarA(EstadoTorneo nuevo) {
        switch (this) {
            case ABIERTO:
                return nuevo == CERRADO;
            case CERRADO:
                return nuevo == ABIERTO || nuevo == EN_CURSO;
            case EN_CURSO:
                return nuevo == FINALIZADO;
            default:
                return false;
        }
    }

    /** PUT /api/torneos/{id}: la configuración solo se reemplaza antes de iniciar. */
    public boolean admiteCambioDeConfiguracion() {
        return this == ABIERTO || this == CERRADO;
    }

    /** RN-06: un torneo cerrado (o posterior) no admite nuevos equipos. */
    public boolean admiteInscripciones() {
        return this == ABIERTO;
    }
}
