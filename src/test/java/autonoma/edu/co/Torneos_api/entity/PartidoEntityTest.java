package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.DatosPrueba;
import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** RN-01: cálculo de cruces de horario. El partido base va de 10:00 a 11:30. */
class PartidoEntityTest {

    private PartidoEntity partido;

    @BeforeEach
    void prepararPartido() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.EN_CURSO);
        partido = DatosPrueba.partido(1L, torneo,
                DatosPrueba.equipo(1L, "Águilas"), DatosPrueba.equipo(2L, "Búhos"),
                LocalDateTime.of(2026, 12, 5, 10, 0), EstadoPartido.PROGRAMADO);
    }

    @Test
    @DisplayName("La hora de fin es inicio + duración")
    void calculaHoraDeFin() {
        assertEquals(LocalDateTime.of(2026, 12, 5, 11, 30), partido.calcularFechaHoraFin());
    }

    @Test
    @DisplayName("RN-01: 11:00-12:30 se cruza con 10:00-11:30")
    void detectaCruceParcial() {
        assertTrue(partido.seCruzaCon(LocalDateTime.of(2026, 12, 5, 11, 0), LocalDateTime.of(2026, 12, 5, 12, 30)));
    }

    @Test
    @DisplayName("RN-01: un partido que empieza antes y termina después también se cruza")
    void detectaCruceQueLoContiene() {
        assertTrue(partido.seCruzaCon(LocalDateTime.of(2026, 12, 5, 9, 0), LocalDateTime.of(2026, 12, 5, 13, 0)));
    }

    @Test
    @DisplayName("RN-01: partidos seguidos (11:30-13:00) NO se cruzan")
    void partidosSeguidosNoSeCruzan() {
        assertFalse(partido.seCruzaCon(LocalDateTime.of(2026, 12, 5, 11, 30), LocalDateTime.of(2026, 12, 5, 13, 0)));
    }

    @Test
    @DisplayName("RN-01: otro día no se cruza")
    void otroDiaNoSeCruza() {
        assertFalse(partido.seCruzaCon(LocalDateTime.of(2026, 12, 6, 10, 0), LocalDateTime.of(2026, 12, 6, 11, 30)));
    }

    @Test
    @DisplayName("tieneResultado y participa")
    void resultadoYParticipacion() {
        assertFalse(partido.tieneResultado());
        partido.setGolesLocal(2);
        partido.setGolesVisitante(1);
        assertTrue(partido.tieneResultado());

        assertTrue(partido.participa(1L));
        assertTrue(partido.participa(2L));
        assertFalse(partido.participa(3L));
    }
}
