package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.DatosPrueba;
import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Dto.RegistrarResultadoRequest;
import autonoma.edu.co.Torneos_api.Excepciones.AccesoDenegadoException;
import autonoma.edu.co.Torneos_api.Excepciones.ConflictoException;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Excepciones.SolicitudInvalidaException;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de PartidoService.
 * Los repositorios y los otros servicios son "mocks" (objetos simulados con Mockito):
 * no se usa la base de datos, solo se prueba la lógica de las reglas.
 *
 * Nota sobre assertThrows(X.class, () -> metodo()):
 *   "() -> metodo()" significa "ejecuta este método"; assertThrows comprueba que lance la excepción X.
 */
@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private TorneoService torneoService;
    @Mock
    private EquipoService equipoService;
    @Mock
    private AutorizacionService autorizacionService;
    @Mock
    private HistorialService historialService;

    @InjectMocks
    private PartidoService partidoService;

    private UsuarioEntity laura;
    private TorneoEntity torneo;
    private EquipoEntity aguilas;
    private EquipoEntity buhos;

    @BeforeEach
    void prepararDatos() {
        laura = DatosPrueba.organizadora();
        torneo = DatosPrueba.torneo(EstadoTorneo.EN_CURSO);
        aguilas = DatosPrueba.equipo(1L, "Águilas");
        buhos = DatosPrueba.equipo(2L, "Búhos");
    }

    // ------------------------------------------------------------ POST /api/partidos

    @Test
    @DisplayName("No se puede programar un partido de un equipo contra sí mismo")
    void rechazaMismoEquipo() {
        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 1L, LocalDateTime.of(2026, 12, 5, 10, 0), 90);

        assertThrows(SolicitudInvalidaException.class, () -> partidoService.programarPartido(request, 1L));
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-01: rechaza un partido si un equipo ya juega a esa hora")
    void rechazaCruceDeHorario() {
        prepararProgramacion();
        PartidoEntity yaProgramado = DatosPrueba.partido(5L, torneo, aguilas, DatosPrueba.equipo(4L, "Delfines"),
                LocalDateTime.of(2026, 12, 5, 10, 0), EstadoPartido.PROGRAMADO);
        when(partidoRepository.buscarPorEquipo(1L)).thenReturn(List.of(yaProgramado));

        // Nuevo partido a las 11:00: se cruza con el de 10:00 a 11:30
        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 2L, LocalDateTime.of(2026, 12, 5, 11, 0), 90);

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> partidoService.programarPartido(request, 1L));
        assertTrue(error.getMessage().startsWith("RN-01"));
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-01: un partido CANCELADO no ocupa el horario")
    void partidoCanceladoNoBloquea() {
        prepararProgramacion();
        PartidoEntity cancelado = DatosPrueba.partido(5L, torneo, aguilas, DatosPrueba.equipo(4L, "Delfines"),
                LocalDateTime.of(2026, 12, 5, 10, 0), EstadoPartido.CANCELADO);
        when(partidoRepository.buscarPorEquipo(1L)).thenReturn(List.of(cancelado));
        when(partidoRepository.buscarPorEquipo(2L)).thenReturn(List.of());
        when(partidoRepository.save(any(PartidoEntity.class))).then(returnsFirstArg());

        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 2L, LocalDateTime.of(2026, 12, 5, 11, 0), 90);
        PartidoResponse respuesta = partidoService.programarPartido(request, 1L);

        assertEquals(EstadoPartido.PROGRAMADO, respuesta.estado());
    }

    @Test
    @DisplayName("Programa el partido y deja registro en el historial (trazabilidad)")
    void programaPartidoYRegistraHistorial() {
        prepararProgramacion();
        when(partidoRepository.buscarPorEquipo(1L)).thenReturn(List.of());
        when(partidoRepository.buscarPorEquipo(2L)).thenReturn(List.of());
        when(partidoRepository.save(any(PartidoEntity.class))).then(returnsFirstArg());

        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 2L, LocalDateTime.of(2026, 12, 5, 10, 0), 90);
        PartidoResponse respuesta = partidoService.programarPartido(request, 1L);

        assertEquals("Águilas", respuesta.equipoLocal());
        assertEquals("Búhos", respuesta.equipoVisitante());
        assertEquals(LocalDateTime.of(2026, 12, 5, 11, 30), respuesta.fechaHoraFin());
        verify(historialService).registrar(eq(HistorialService.PARTIDO), any(), eq("PROGRAMADO"), anyString(), eq(laura));
    }

    @Test
    @DisplayName("No se programa un partido con un equipo que no está inscrito en el torneo")
    void rechazaEquipoNoInscrito() {
        when(torneoService.buscarEntidad(1L)).thenReturn(torneo);
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(equipoService.buscarEntidad(1L)).thenReturn(aguilas);
        when(equipoService.buscarEntidad(2L)).thenReturn(buhos);
        when(torneoService.equipoEstaInscrito(1L, 1L)).thenReturn(false);

        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 2L, LocalDateTime.of(2026, 12, 5, 10, 0), 90);

        assertThrows(ReglaNegocioException.class, () -> partidoService.programarPartido(request, 1L));
    }

    @Test
    @DisplayName("Autorización: si no es el organizador del torneo, no se programa nada")
    void rechazaUsuarioSinPermiso() {
        when(torneoService.buscarEntidad(1L)).thenReturn(torneo);
        when(autorizacionService.exigirOrganizadorDelTorneo(2L, torneo))
                .thenThrow(new AccesoDenegadoException("no es el organizador"));

        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 2L, LocalDateTime.of(2026, 12, 5, 10, 0), 90);

        assertThrows(AccesoDenegadoException.class, () -> partidoService.programarPartido(request, 2L));
        verify(partidoRepository, never()).save(any());
    }

    // ------------------------------------------------------------ PATCH /resultado

    @Test
    @DisplayName("RN-02: un partido FINALIZADO no acepta resultado")
    void finalizadoNoAceptaResultado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.FINALIZADO);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        assertThrows(ReglaNegocioException.class,
                () -> partidoService.registrarResultado(5L, new RegistrarResultadoRequest(1, 0), 1L));
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-02: un partido CANCELADO no acepta resultado")
    void canceladoNoAceptaResultado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.CANCELADO);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        assertThrows(ReglaNegocioException.class,
                () -> partidoService.registrarResultado(5L, new RegistrarResultadoRequest(1, 0), 1L));
    }

    @Test
    @DisplayName("Registra el resultado y el partido sigue EN_JUEGO (no se finaliza solo)")
    void registraResultado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.EN_JUEGO);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(partidoRepository.save(partido)).thenReturn(partido);

        PartidoResponse respuesta = partidoService.registrarResultado(5L, new RegistrarResultadoRequest(1, 1), 1L);

        assertEquals(1, (int) respuesta.golesLocal());
        assertEquals(1, (int) respuesta.golesVisitante());
        assertEquals(EstadoPartido.EN_JUEGO, respuesta.estado());
        verify(historialService).registrar(HistorialService.PARTIDO, 5L, "RESULTADO_REGISTRADO", "1-1", laura);
    }

    @Test
    @DisplayName("Corrige el resultado y la trazabilidad guarda el marcador anterior y el nuevo")
    void corrigeResultado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.EN_JUEGO);
        partido.setGolesLocal(1);
        partido.setGolesVisitante(1);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(partidoRepository.save(partido)).thenReturn(partido);

        PartidoResponse respuesta = partidoService.registrarResultado(5L, new RegistrarResultadoRequest(2, 1), 1L);

        assertEquals(2, (int) respuesta.golesLocal());
        verify(historialService).registrar(HistorialService.PARTIDO, 5L, "RESULTADO_CORREGIDO", "1-1 -> 2-1", laura);
    }

    // ------------------------------------------------------------ PATCH /estado

    @Test
    @DisplayName("No se puede finalizar un partido sin resultado")
    void noFinalizaSinResultado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.EN_JUEGO);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        assertThrows(ReglaNegocioException.class, () -> partidoService.cambiarEstado(5L,
                new CambiarEstadoPartidoRequest(EstadoPartido.FINALIZADO), 1L));
    }

    @Test
    @DisplayName("Finaliza un partido EN_JUEGO con resultado y lo registra en el historial")
    void finalizaConResultado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.EN_JUEGO);
        partido.setGolesLocal(2);
        partido.setGolesVisitante(0);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(partidoRepository.save(partido)).thenReturn(partido);

        PartidoResponse respuesta = partidoService.cambiarEstado(5L,
                new CambiarEstadoPartidoRequest(EstadoPartido.FINALIZADO), 1L);

        assertEquals(EstadoPartido.FINALIZADO, respuesta.estado());
        verify(historialService).registrar(HistorialService.PARTIDO, 5L, "CAMBIO_ESTADO", "EN_JUEGO -> FINALIZADO", laura);
    }

    // ------------------------------------------------------------ DELETE

    @Test
    @DisplayName("Elimina un partido futuro, PROGRAMADO y sin resultado")
    void eliminaPartidoFuturo() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.PROGRAMADO);
        partido.setFechaHoraInicio(LocalDateTime.now().plusDays(10));
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        partidoService.eliminarPartido(5L, 1L);

        verify(partidoRepository).delete(partido);
        verify(historialService).registrar(eq(HistorialService.PARTIDO), eq(5L), eq("ELIMINADO"), anyString(), eq(laura));
    }

    @Test
    @DisplayName("No elimina un partido que ya pasó")
    void noEliminaPartidoPasado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.PROGRAMADO);
        partido.setFechaHoraInicio(LocalDateTime.now().minusDays(1));
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        assertThrows(ReglaNegocioException.class, () -> partidoService.eliminarPartido(5L, 1L));
        verify(partidoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("No elimina un partido FINALIZADO")
    void noEliminaPartidoFinalizado() {
        PartidoEntity partido = partidoEnEstado(EstadoPartido.FINALIZADO);
        when(partidoRepository.findById(5L)).thenReturn(Optional.of(partido));
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);

        assertThrows(ReglaNegocioException.class, () -> partidoService.eliminarPartido(5L, 1L));
        verify(partidoRepository, never()).delete(any());
    }

    // ------------------------------------------------------------ ayudas

    /** Deja listos los mocks que usa programarPartido hasta la validación de cruces. */
    private void prepararProgramacion() {
        when(torneoService.buscarEntidad(1L)).thenReturn(torneo);
        when(autorizacionService.exigirOrganizadorDelTorneo(1L, torneo)).thenReturn(laura);
        when(equipoService.buscarEntidad(1L)).thenReturn(aguilas);
        when(equipoService.buscarEntidad(2L)).thenReturn(buhos);
        when(torneoService.equipoEstaInscrito(anyLong(), anyLong())).thenReturn(true);
    }

    private PartidoEntity partidoEnEstado(EstadoPartido estado) {
        return DatosPrueba.partido(5L, torneo, aguilas, buhos, LocalDateTime.of(2026, 12, 5, 10, 0), estado);
    }
}
