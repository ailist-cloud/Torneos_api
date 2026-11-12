package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.HistorialResponse;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Dto.RegistrarResultadoRequest;
import autonoma.edu.co.Torneos_api.Excepciones.ConflictoException;
import autonoma.edu.co.Torneos_api.Excepciones.RecursoNoEncontradoException;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Excepciones.SolicitudInvalidaException;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

/**
 * Lógica de partidos: programación sin cruces (RN-01), estados, registro y corrección de resultado (RN-02)
 * y eliminación de partidos futuros. Cada cambio queda en el historial y solo lo hace el organizador del torneo.
 */
@RequiredArgsConstructor
@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final TorneoService torneoService;
    private final EquipoService equipoService;
    private final AutorizacionService autorizacionService;
    private final HistorialService historialService;

    // ---------------------------------------------------------------- GET (públicos)

    /**
     * Programación de partidos. Los filtros son opcionales:
     *   /api/partidos                  -> todos
     *   /api/partidos?torneoId=1       -> los del torneo 1
     *   /api/partidos?equipoId=3       -> los del equipo 3 (local o visitante)
     *   /api/partidos?torneoId=1&equipoId=3 -> los del equipo 3 en el torneo 1
     */
    @Transactional(readOnly = true)
    public List<PartidoResponse> consultarProgramacion(Long torneoId, Long equipoId) {
        List<PartidoEntity> partidos;
        if (equipoId != null) {
            equipoService.buscarEntidad(equipoId); // 404 si no existe
            partidos = partidoRepository.buscarPorEquipo(equipoId);
        } else if (torneoId != null) {
            torneoService.buscarEntidad(torneoId);
            partidos = partidoRepository.findByTorneoIdOrderByFechaHoraInicioAsc(torneoId);
        } else {
            partidos = partidoRepository.findAllByOrderByFechaHoraInicioAsc();
        }

        List<PartidoResponse> respuesta = new ArrayList<>();
        for (PartidoEntity partido : partidos) {
            boolean coincideTorneo = torneoId == null || partido.getTorneo().getId().equals(torneoId);
            if (coincideTorneo) {
                respuesta.add(PartidoResponse.desdeEntity(partido));
            }
        }
        return respuesta;
    }

    /** Consulta un partido; lanza 404 si no existe. */
    @Transactional(readOnly = true)
    public PartidoResponse consultar(Long id) {
        return PartidoResponse.desdeEntity(buscarEntidad(id));
    }

    /** Trazabilidad. Funciona aunque el partido ya se haya eliminado. */
    @Transactional(readOnly = true)
    public List<HistorialResponse> consultarHistorial(Long id) {
        List<HistorialResponse> historial = historialService.consultar(HistorialService.PARTIDO, id);
        if (historial.isEmpty() && !partidoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("partido", id);
        }
        return historial;
    }

    // ---------------------------------------------------------------- POST /api/partidos

    /**
     * Programa un partido nuevo en estado PROGRAMADO.
     * Valida que los equipos sean distintos, estén inscritos, la fecha esté dentro del torneo
     * y que ninguno de los dos tenga otro partido a esa hora (RN-01, 409).
     */
    @Transactional
    public PartidoResponse programarPartido(CrearPartidoRequest request, Long usuarioId) {
        if (request.equipoLocalId().equals(request.equipoVisitanteId())) {
            throw new SolicitudInvalidaException("El equipo local y el visitante deben ser diferentes");
        }

        TorneoEntity torneo = torneoService.buscarEntidad(request.torneoId());
        UsuarioEntity usuario = autorizacionService.exigirOrganizadorDelTorneo(usuarioId, torneo);
        EquipoEntity local = equipoService.buscarEntidad(request.equipoLocalId());
        EquipoEntity visitante = equipoService.buscarEntidad(request.equipoVisitanteId());

        if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
            throw new ReglaNegocioException("El torneo " + torneo.getNombre() + " ya finalizó; no se pueden programar partidos");
        }
        validarInscrito(torneo, local);
        validarInscrito(torneo, visitante);
        validarFechaDentroDelTorneo(torneo, request.fechaHoraInicio());

        LocalDateTime inicio = request.fechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(request.duracionMin());
        validarSinCruces(local, inicio, fin);
        validarSinCruces(visitante, inicio, fin);

        PartidoEntity partido = new PartidoEntity();
        partido.setTorneo(torneo);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setFechaHoraInicio(inicio);
        partido.setDuracionMin(request.duracionMin());
        partido.setEstado(EstadoPartido.PROGRAMADO);
        partido = partidoRepository.save(partido);

        historialService.registrar(HistorialService.PARTIDO, partido.getId(), "PROGRAMADO",
                local.getNombre() + " vs " + visitante.getNombre() + " el " + inicio, usuario);
        return PartidoResponse.desdeEntity(partido);
    }

    // ---------------------------------------------------------------- PATCH /api/partidos/{id}/estado

    /**
     * Cambia el estado del partido (PROGRAMADO → EN_JUEGO → FINALIZADO, o CANCELADO).
     * Para finalizar, el partido debe tener resultado.
     */
    @Transactional
    public PartidoResponse cambiarEstado(Long id, CambiarEstadoPartidoRequest request, Long usuarioId) {
        PartidoEntity partido = buscarEntidad(id);
        UsuarioEntity usuario = autorizacionService.exigirOrganizadorDelTorneo(usuarioId, partido.getTorneo());
        EstadoPartido actual = partido.getEstado();
        EstadoPartido nuevo = request.estado();

        if (!actual.puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException("El partido no puede pasar de " + actual + " a " + nuevo);
        }
        if (nuevo == EstadoPartido.FINALIZADO && !partido.tieneResultado()) {
            throw new ReglaNegocioException("No se puede finalizar sin resultado. Primero use PATCH /api/partidos/"
                    + id + "/resultado");
        }

        partido.setEstado(nuevo);
        partido = partidoRepository.save(partido);
        historialService.registrar(HistorialService.PARTIDO, id, "CAMBIO_ESTADO", actual + " -> " + nuevo, usuario);
        return PartidoResponse.desdeEntity(partido);
    }

    // ---------------------------------------------------------------- PATCH /api/partidos/{id}/resultado

    /**
     * Registra o corrige el resultado (solo el organizador del torneo).
     * RN-02: solo mientras el partido está PROGRAMADO o EN_JUEGO; se puede corregir las veces necesarias.
     * El partido NO se finaliza aquí: se finaliza con PATCH /estado {"estado":"FINALIZADO"}.
     * Solo los partidos FINALIZADOS cuentan para la tabla.
     */
    @Transactional
    public PartidoResponse registrarResultado(Long id, RegistrarResultadoRequest request, Long usuarioId) {
        PartidoEntity partido = buscarEntidad(id);
        UsuarioEntity usuario = autorizacionService.exigirOrganizadorDelTorneo(usuarioId, partido.getTorneo());

        // RN-02
        if (!partido.getEstado().aceptaResultado()) {
            throw new ReglaNegocioException("RN-02: el partido está " + partido.getEstado()
                    + "; solo acepta resultado si está PROGRAMADO o EN_JUEGO");
        }
        // RN-03 ya lo valida @Min(0) en el DTO

        String nuevoMarcador = request.golesLocal() + "-" + request.golesVisitante();
        String accion;
        String detalle;
        if (partido.tieneResultado()) {
            accion = "RESULTADO_CORREGIDO";
            detalle = partido.getGolesLocal() + "-" + partido.getGolesVisitante() + " -> " + nuevoMarcador;
        } else {
            accion = "RESULTADO_REGISTRADO";
            detalle = nuevoMarcador;
        }

        partido.setGolesLocal(request.golesLocal());
        partido.setGolesVisitante(request.golesVisitante());
        partido = partidoRepository.save(partido);
        historialService.registrar(HistorialService.PARTIDO, id, accion, detalle, usuario);
        return PartidoResponse.desdeEntity(partido);
    }

    // ---------------------------------------------------------------- DELETE /api/partidos/{id}

    /** Solo se elimina un partido futuro, PROGRAMADO y sin resultado. El historial se conserva. */
    @Transactional
    public void eliminarPartido(Long id, Long usuarioId) {
        PartidoEntity partido = buscarEntidad(id);
        UsuarioEntity usuario = autorizacionService.exigirOrganizadorDelTorneo(usuarioId, partido.getTorneo());

        if (partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new ReglaNegocioException("Solo se pueden eliminar partidos PROGRAMADOS; este está " + partido.getEstado());
        }
        if (partido.tieneResultado()) {
            throw new ReglaNegocioException("El partido ya tiene resultado; no se puede eliminar");
        }
        if (!partido.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("El partido ya empezó o ya pasó (" + partido.getFechaHoraInicio()
                    + "); solo se eliminan partidos futuros");
        }

        historialService.registrar(HistorialService.PARTIDO, id, "ELIMINADO",
                partido.getEquipoLocal().getNombre() + " vs " + partido.getEquipoVisitante().getNombre()
                        + " del " + partido.getFechaHoraInicio(), usuario);
        partidoRepository.delete(partido);
    }

    // ---------------------------------------------------------------- privados

    private PartidoEntity buscarEntidad(Long id) {
        Optional<PartidoEntity> partido = partidoRepository.findById(id);
        if (partido.isEmpty()) {
            throw new RecursoNoEncontradoException("partido", id);
        }
        return partido.get();
    }

    private void validarInscrito(TorneoEntity torneo, EquipoEntity equipo) {
        if (!torneoService.equipoEstaInscrito(torneo.getId(), equipo.getId())) {
            throw new ReglaNegocioException("El equipo " + equipo.getNombre()
                    + " no está inscrito en el torneo " + torneo.getNombre());
        }
    }

    private void validarFechaDentroDelTorneo(TorneoEntity torneo, LocalDateTime fechaHoraInicio) {
        LocalDate dia = fechaHoraInicio.toLocalDate();
        if (dia.isBefore(torneo.getFechaInicio()) || dia.isAfter(torneo.getFechaFin())) {
            throw new ReglaNegocioException("La fecha " + dia + " está fuera del torneo ("
                    + torneo.getFechaInicio() + " a " + torneo.getFechaFin() + ")");
        }
    }

    /** RN-01: un equipo no puede jugar dos partidos al mismo tiempo (en ningún torneo). */
    private void validarSinCruces(EquipoEntity equipo, LocalDateTime inicio, LocalDateTime fin) {
        for (PartidoEntity existente : partidoRepository.buscarPorEquipo(equipo.getId())) {
            if (existente.getEstado() == EstadoPartido.CANCELADO) {
                continue; // un partido cancelado no ocupa horario
            }
            if (existente.seCruzaCon(inicio, fin)) {
                throw new ConflictoException("RN-01: el equipo " + equipo.getNombre()
                        + " ya juega el partido " + existente.getId() + " de "
                        + existente.getFechaHoraInicio() + " a " + existente.calcularFechaHoraFin());
            }
        }
    }
}
