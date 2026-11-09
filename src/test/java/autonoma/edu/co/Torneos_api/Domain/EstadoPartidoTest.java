package autonoma.edu.co.Torneos_api.Domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoPartidoTest {

    @Test
    @DisplayName("RN-02: solo PROGRAMADO y EN_JUEGO aceptan resultado")
    void soloProgramadoYEnJuegoAceptanResultado() {
        assertTrue(EstadoPartido.PROGRAMADO.aceptaResultado());
        assertTrue(EstadoPartido.EN_JUEGO.aceptaResultado());
        assertFalse(EstadoPartido.FINALIZADO.aceptaResultado());
        assertFalse(EstadoPartido.CANCELADO.aceptaResultado());
    }

    @Test
    @DisplayName("Transiciones válidas: PROGRAMADO -> EN_JUEGO -> FINALIZADO, y cancelar")
    void transicionesValidas() {
        assertTrue(EstadoPartido.PROGRAMADO.puedeCambiarA(EstadoPartido.EN_JUEGO));
        assertTrue(EstadoPartido.PROGRAMADO.puedeCambiarA(EstadoPartido.CANCELADO));
        assertTrue(EstadoPartido.EN_JUEGO.puedeCambiarA(EstadoPartido.FINALIZADO));
        assertTrue(EstadoPartido.EN_JUEGO.puedeCambiarA(EstadoPartido.CANCELADO));
    }

    @Test
    @DisplayName("Transiciones inválidas: no se salta EN_JUEGO ni se sale de FINALIZADO o CANCELADO")
    void transicionesInvalidas() {
        assertFalse(EstadoPartido.PROGRAMADO.puedeCambiarA(EstadoPartido.FINALIZADO));
        assertFalse(EstadoPartido.FINALIZADO.puedeCambiarA(EstadoPartido.EN_JUEGO));
        assertFalse(EstadoPartido.FINALIZADO.puedeCambiarA(EstadoPartido.CANCELADO));
        assertFalse(EstadoPartido.CANCELADO.puedeCambiarA(EstadoPartido.PROGRAMADO));
    }
}
