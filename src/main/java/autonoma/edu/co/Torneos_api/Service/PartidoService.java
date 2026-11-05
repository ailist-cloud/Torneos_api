package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final TorneoService torneoService;
    private final EquipoService equipoService;

    public PartidoService(PartidoRepository partidoRepository,
                          TorneoService torneoService,
                          EquipoService equipoService) {
        this.partidoRepository = partidoRepository;
        this.torneoService = torneoService;
        this.equipoService = equipoService;
    }

    // ---------------------------------------------------------------- GET /api/partidos

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

    @Transactional(readOnly = true)
    public PartidoResponse consultar(Long id) {
        return PartidoResponse.desdeEntity(buscarEntidad(id));
    }

    // ---------------------------------------------------------------- POST /api/partidos

    @Transactional
    public PartidoResponse programarPartido(CrearPartidoRequest request) {
        if (request.equipoLocalId().equals(request.equipoVisitanteId())) {
            throw new SolicitudInvalidaException("El equipo local y el visitante deben ser diferentes");
        }

        TorneoEntity torneo = torneoService.buscarEntidad(request.torneoId());
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

        return PartidoResponse.desdeEntity(partidoRepository.save(partido));
    }

    // ---------------------------------------------------------------- PATCH /api/partidos/{id}/estado

    @Transactional
    public PartidoResponse cambiarEstado(Long id, CambiarEstadoPartidoRequest request) {
        PartidoEntity partido = buscarEntidad(id);
        EstadoPartido actual = partido.getEstado();
        EstadoPartido nuevo = request.estado();

        if (!actual.puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException("El partido no puede pasar de " + actual + " a " + nuevo
                    + ". Para finalizarlo use PATCH /api/partidos/" + id + "/resultado");
        }
        partido.setEstado(nuevo);
        return PartidoResponse.desdeEntity(partidoRepository.save(partido));
    }

    // ---------------------------------------------------------------- PATCH /api/partidos/{id}/resultado

    @Transactional
    public PartidoResponse registrarResultado(Long id, RegistrarResultadoRequest request) {
        PartidoEntity partido = buscarEntidad(id);

        // RN-02
        if (!partido.getEstado().aceptaResultado()) {
            throw new ReglaNegocioException("RN-02: el partido está " + partido.getEstado()
                    + "; solo acepta resultado si está PROGRAMADO o EN_JUEGO");
        }
        // RN-03 ya lo valida @Min(0) en el DTO

        partido.setGolesLocal(request.golesLocal());
        partido.setGolesVisitante(request.golesVisitante());
        partido.setEstado(EstadoPartido.FINALIZADO);
        return PartidoResponse.desdeEntity(partidoRepository.save(partido));
    }

    // ---------------------------------------------------------------- DELETE /api/partidos/{id}

    /** Solo se elimina un partido futuro, PROGRAMADO y sin resultado. */
    @Transactional
    public void eliminarPartido(Long id) {
        PartidoEntity partido = buscarEntidad(id);

        if (partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new ReglaNegocioException("Solo se pueden eliminar partidos PROGRAMADOS; este está " + partido.getEstado());
        }
        if (partido.getGolesLocal() != null || partido.getGolesVisitante() != null) {
            throw new ReglaNegocioException("El partido ya tiene resultado; no se puede eliminar");
        }
        if (!partido.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("El partido ya empezó o ya pasó (" + partido.getFechaHoraInicio()
                    + "); solo se eliminan partidos futuros");
        }
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
