package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.DatosPrueba;
import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Dto.PosicionResponse;
import autonoma.edu.co.Torneos_api.Dto.TablaPosicionesResponse;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Repository.CriterioDesempateRepository;
import autonoma.edu.co.Torneos_api.Repository.InscripcionRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * RN-04: la tabla se calcula a partir de los partidos FINALIZADOS.
 * RN-05: sin criterios de desempate no se puede calcular.
 *
 * Escenario (puntos 3/1/0):
 *   Águilas 2-0 Búhos | Cóndores 3-0 Delfines | Águilas 1-1 Cóndores | Búhos 1-0 Delfines
 *   => Cóndores 4 pts (DG +3), Águilas 4 pts (DG +2), Búhos 3 pts, Delfines 0 pts
 */
@ExtendWith(MockitoExtension.class)
class TablaPosicionesServiceTest {

    @Mock
    private TorneoService torneoService;
    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private CriterioDesempateRepository criterioRepository;

    @InjectMocks
    private TablaPosicionesService tablaService;

    private TorneoEntity torneo;
    private EquipoEntity aguilas;
    private EquipoEntity buhos;
    private EquipoEntity condores;
    private EquipoEntity delfines;

    @BeforeEach
    void prepararDatos() {
        torneo = DatosPrueba.torneo(EstadoTorneo.EN_CURSO);
        aguilas = DatosPrueba.equipo(1L, "Águilas");
        buhos = DatosPrueba.equipo(2L, "Búhos");
        condores = DatosPrueba.equipo(3L, "Cóndores");
        delfines = DatosPrueba.equipo(4L, "Delfines");
    }

    @Test
    @DisplayName("RN-05: sin criterios de desempate no se calcula la tabla")
    void sinCriteriosNoSeCalcula() {
        when(torneoService.buscarEntidad(1L)).thenReturn(torneo);
        when(criterioRepository.findByTorneoIdOrderByOrdenAsc(1L)).thenReturn(List.of());

        assertThrows(ReglaNegocioException.class, () -> tablaService.calcular(1L));
    }

    @Test
    @DisplayName("RN-04: suma puntos, goles y ordena desempatando por diferencia de goles")
    void calculaYOrdenaLaTabla() {
        prepararTorneo(CriterioDesempate.DIFERENCIA_GOLES);

        TablaPosicionesResponse tabla = tablaService.calcular(1L);
        List<PosicionResponse> posiciones = tabla.posiciones();

        assertEquals(4, posiciones.size());
        verificarFila(posiciones.get(0), 1, "Cóndores", 4, 3);
        verificarFila(posiciones.get(1), 2, "Águilas", 4, 2);
        verificarFila(posiciones.get(2), 3, "Búhos", 3, -1);
        verificarFila(posiciones.get(3), 4, "Delfines", 0, -4);

        // Detalle de una fila completa: Águilas PJ 2, G 1, E 1, P 0, GF 3, GC 1
        PosicionResponse aguilasFila = posiciones.get(1);
        assertEquals(2, (int) aguilasFila.partidosJugados());
        assertEquals(1, (int) aguilasFila.ganados());
        assertEquals(1, (int) aguilasFila.empatados());
        assertEquals(0, (int) aguilasFila.perdidos());
        assertEquals(3, (int) aguilasFila.golesAFavor());
        assertEquals(1, (int) aguilasFila.golesEnContra());
    }

    @Test
    @DisplayName("RN-05: el orden de los criterios cambia el resultado del desempate")
    void elCriterioDecideElDesempate() {
        // Con DIFERENCIA_GOLES, Cóndores va primero (+3 contra +2).
        // Con PARTIDOS_GANADOS y GOLES_EN_CONTRA, Águilas y Cóndores empatan en todo
        // (1 victoria y 1 gol recibido cada uno) y queda el orden alfabético: Águilas primero.
        prepararTorneo(CriterioDesempate.PARTIDOS_GANADOS, CriterioDesempate.GOLES_EN_CONTRA);

        List<PosicionResponse> posiciones = tablaService.calcular(1L).posiciones();

        assertEquals("Águilas", posiciones.get(0).equipo());
        assertEquals("Cóndores", posiciones.get(1).equipo());
    }

    @Test
    @DisplayName("Un equipo inscrito que no ha jugado aparece con 0 en todo")
    void equipoSinPartidosApareceEnCero() {
        when(torneoService.buscarEntidad(1L)).thenReturn(torneo);
        when(criterioRepository.findByTorneoIdOrderByOrdenAsc(1L))
                .thenReturn(List.of(new CriterioDesempateEntity(torneo, 1, CriterioDesempate.DIFERENCIA_GOLES)));
        when(inscripcionRepository.findByTorneoIdOrderByFechaInscripcionAsc(1L))
                .thenReturn(List.of(new InscripcionEntity(torneo, aguilas, LocalDate.now())));
        when(partidoRepository.findByTorneoIdAndEstado(1L, EstadoPartido.FINALIZADO)).thenReturn(List.of());

        PosicionResponse fila = tablaService.calcular(1L).posiciones().get(0);

        assertEquals("Águilas", fila.equipo());
        assertEquals(0, (int) fila.partidosJugados());
        assertEquals(0, (int) fila.puntos());
    }

    // ------------------------------------------------------------ ayudas

    private void prepararTorneo(CriterioDesempate... criterios) {
        when(torneoService.buscarEntidad(1L)).thenReturn(torneo);

        List<CriterioDesempateEntity> entidades = new ArrayList<>();
        int orden = 1;
        for (CriterioDesempate criterio : criterios) {
            entidades.add(new CriterioDesempateEntity(torneo, orden, criterio));
            orden++;
        }
        when(criterioRepository.findByTorneoIdOrderByOrdenAsc(1L)).thenReturn(entidades);

        List<InscripcionEntity> inscripciones = new ArrayList<>();
        for (EquipoEntity equipo : List.of(aguilas, buhos, condores, delfines)) {
            inscripciones.add(new InscripcionEntity(torneo, equipo, LocalDate.now()));
        }
        when(inscripcionRepository.findByTorneoIdOrderByFechaInscripcionAsc(1L)).thenReturn(inscripciones);

        List<PartidoEntity> partidos = List.of(
                DatosPrueba.finalizado(torneo, aguilas, buhos, 2, 0),
                DatosPrueba.finalizado(torneo, condores, delfines, 3, 0),
                DatosPrueba.finalizado(torneo, aguilas, condores, 1, 1),
                DatosPrueba.finalizado(torneo, buhos, delfines, 1, 0));
        when(partidoRepository.findByTorneoIdAndEstado(1L, EstadoPartido.FINALIZADO)).thenReturn(partidos);
    }

    private void verificarFila(PosicionResponse fila, int posicion, String equipo, int puntos, int diferencia) {
        assertEquals(posicion, (int) fila.posicion());
        assertEquals(equipo, fila.equipo());
        assertEquals(puntos, (int) fila.puntos());
        assertEquals(diferencia, (int) fila.diferenciaGoles());
    }
}
