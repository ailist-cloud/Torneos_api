package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.DatosPrueba;
import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Dto.ActualizarTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscribirEquipoRequest;
import autonoma.edu.co.Torneos_api.Excepciones.ConflictoException;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Excepciones.SolicitudInvalidaException;
import autonoma.edu.co.Torneos_api.Repository.CriterioDesempateRepository;
import autonoma.edu.co.Torneos_api.Repository.InscripcionRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TorneoServiceTest {

    @Mock
    private TorneoRepository torneoRepository;
    @Mock
    private CriterioDesempateRepository criterioRepository;
    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private EquipoService equipoService;
    @Mock
    private AutorizacionService autorizacionService;
    @Mock
    private HistorialService historialService;

    @InjectMocks
    private TorneoService torneoService;

    private UsuarioEntity laura;
    private EquipoEntity aguilas;

    @BeforeEach
    void prepararDatos() {
        laura = DatosPrueba.organizadora();
        aguilas = DatosPrueba.equipo(1L, "Águilas");
    }

    // ------------------------------------------------------------ RN-06 inscripciones

    @Test
    @DisplayName("RN-06: un torneo CERRADO no admite nuevos equipos")
    void torneoCerradoNoAdmiteEquipos() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.CERRADO);
        prepararInscripcion(torneo);

        assertThrows(ReglaNegocioException.class,
                () -> torneoService.inscribirEquipo(1L, new InscribirEquipoRequest(1L), 1L));
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-06: un torneo EN_CURSO tampoco admite nuevos equipos")
    void torneoEnCursoNoAdmiteEquipos() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.EN_CURSO);
        prepararInscripcion(torneo);

        assertThrows(ReglaNegocioException.class,
                () -> torneoService.inscribirEquipo(1L, new InscribirEquipoRequest(1L), 1L));
    }

    @Test
    @DisplayName("No se inscribe dos veces el mismo equipo en un torneo")
    void noInscribeDosVeces() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.ABIERTO);
        prepararInscripcion(torneo);
        when(inscripcionRepository.existsByTorneoIdAndEquipoId(1L, 1L)).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> torneoService.inscribirEquipo(1L, new InscribirEquipoRequest(1L), 1L));
        verify(inscripcionRepository, never()).save(any());
    }

    // ------------------------------------------------------------ RN-05 y RN-03 al crear

    @Test
    @DisplayName("RN-05: no se aceptan criterios de desempate repetidos")
    void rechazaCriteriosRepetidos() {
        when(autorizacionService.exigirOrganizador(1L)).thenReturn(laura);
        CrearTorneoRequest request = crearRequest(3, 1, 0,
                List.of(CriterioDesempate.DIFERENCIA_GOLES, CriterioDesempate.DIFERENCIA_GOLES));

        assertThrows(SolicitudInvalidaException.class, () -> torneoService.crear(request, 1L));
        verify(torneoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Los puntos deben cumplir victoria > empate >= derrota")
    void rechazaPuntosIncoherentes() {
        when(autorizacionService.exigirOrganizador(1L)).thenReturn(laura);
        CrearTorneoRequest request = crearRequest(1, 3, 0, List.of(CriterioDesempate.DIFERENCIA_GOLES));

        assertThrows(SolicitudInvalidaException.class, () -> torneoService.crear(request, 1L));
    }

    @Test
    @DisplayName("fechaFin no puede ser anterior a fechaInicio")
    void rechazaFechasInvertidas() {
        when(autorizacionService.exigirOrganizador(1L)).thenReturn(laura);
        CrearTorneoRequest request = new CrearTorneoRequest("Copa", "Fútbol",
                LocalDate.of(2026, 12, 31), LocalDate.of(2026, 1, 1), 3, 1, 0,
                List.of(CriterioDesempate.DIFERENCIA_GOLES));

        assertThrows(SolicitudInvalidaException.class, () -> torneoService.crear(request, 1L));
    }

    // ------------------------------------------------------------ PUT y cambios de estado

    @Test
    @DisplayName("PUT: no se reemplaza la configuración de un torneo que ya inició")
    void noActualizaTorneoIniciado() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.EN_CURSO);
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        ActualizarTorneoRequest request = new ActualizarTorneoRequest("Copa", "Fútbol",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 3, 1, 0,
                List.of(CriterioDesempate.DIFERENCIA_GOLES));

        assertThrows(ReglaNegocioException.class, () -> torneoService.actualizar(1L, request, 1L));
        verify(torneoRepository, never()).save(any());
    }

    @Test
    @DisplayName("No inicia un torneo con menos de 2 equipos inscritos")
    void noIniciaConMenosDeDosEquipos() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.CERRADO);
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(inscripcionRepository.countByTorneoId(1L)).thenReturn(1L);

        assertThrows(ReglaNegocioException.class,
                () -> torneoService.cambiarEstado(1L, new CambiarEstadoTorneoRequest(EstadoTorneo.EN_CURSO), 1L));
    }

    @Test
    @DisplayName("No se puede saltar de ABIERTO a FINALIZADO")
    void noSaltaEstados() {
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.ABIERTO);
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        assertThrows(ReglaNegocioException.class,
                () -> torneoService.cambiarEstado(1L, new CambiarEstadoTorneoRequest(EstadoTorneo.FINALIZADO), 1L));
    }

    // ------------------------------------------------------------ ayudas

    private void prepararInscripcion(TorneoEntity torneo) {
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(equipoService.buscarEntidad(1L)).thenReturn(aguilas);
    }

    private CrearTorneoRequest crearRequest(int victoria, int empate, int derrota, List<CriterioDesempate> criterios) {
        return new CrearTorneoRequest("Copa", "Fútbol", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                victoria, empate, derrota, criterios);
    }
}
