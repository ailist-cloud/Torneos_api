package autonoma.edu.co.Torneos_api.Domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoTorneoTest {

    @Test
    @DisplayName("RN-06: solo un torneo ABIERTO admite nuevos equipos")
    void soloAbiertoAdmiteInscripciones() {
        assertTrue(EstadoTorneo.ABIERTO.admiteInscripciones());
        assertFalse(EstadoTorneo.CERRADO.admiteInscripciones());
        assertFalse(EstadoTorneo.EN_CURSO.admiteInscripciones());
        assertFalse(EstadoTorneo.FINALIZADO.admiteInscripciones());
    }

    @Test
    @DisplayName("La configuración solo se reemplaza antes de iniciar (ABIERTO o CERRADO)")
    void configuracionAntesDeIniciar() {
        assertTrue(EstadoTorneo.ABIERTO.admiteCambioDeConfiguracion());
        assertTrue(EstadoTorneo.CERRADO.admiteCambioDeConfiguracion());
        assertFalse(EstadoTorneo.EN_CURSO.admiteCambioDeConfiguracion());
        assertFalse(EstadoTorneo.FINALIZADO.admiteCambioDeConfiguracion());
    }

    @Test
    @DisplayName("Ciclo de vida: ABIERTO -> CERRADO -> EN_CURSO -> FINALIZADO")
    void cicloDeVida() {
        assertTrue(EstadoTorneo.ABIERTO.puedeCambiarA(EstadoTorneo.CERRADO));
        assertTrue(EstadoTorneo.CERRADO.puedeCambiarA(EstadoTorneo.EN_CURSO));
        assertTrue(EstadoTorneo.CERRADO.puedeCambiarA(EstadoTorneo.ABIERTO));
        assertTrue(EstadoTorneo.EN_CURSO.puedeCambiarA(EstadoTorneo.FINALIZADO));

        assertFalse(EstadoTorneo.ABIERTO.puedeCambiarA(EstadoTorneo.EN_CURSO));
        assertFalse(EstadoTorneo.FINALIZADO.puedeCambiarA(EstadoTorneo.ABIERTO));
    }
}
