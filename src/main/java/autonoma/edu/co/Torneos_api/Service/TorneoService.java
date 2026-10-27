package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.Dto.ActualizarTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscribirEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscripcionResponse;
import autonoma.edu.co.Torneos_api.Dto.TorneoResponse;
import autonoma.edu.co.Torneos_api.Repository.CriterioDesempateRepository;
import autonoma.edu.co.Torneos_api.Repository.EquipoRepository;
import autonoma.edu.co.Torneos_api.Repository.InscripcionRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import autonoma.edu.co.Torneos_api.Repository.UsuarioRepository;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionIdEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TorneoService {

    private static final int MINIMO_EQUIPOS_PARA_INICIAR = 2;

    private final TorneoRepository torneoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CriterioDesempateRepository criterioDesempateRepository;
    private final PartidoRepository partidoRepository;

    public TorneoService(TorneoRepository torneoRepository,
                         UsuarioRepository usuarioRepository,
                         EquipoRepository equipoRepository,
                         InscripcionRepository inscripcionRepository,
                         CriterioDesempateRepository criterioDesempateRepository,
                         PartidoRepository partidoRepository) {
        this.torneoRepository = torneoRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.criterioDesempateRepository = criterioDesempateRepository;
        this.partidoRepository = partidoRepository;
    }
    @Transactional(readOnly = true)
    public List<TorneoResponse> listar() {
        return torneoRepository.findAll(Sort.by("fechaInicio"))
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public TorneoResponse consultar(Long id) {
        return aRespuesta(buscarTorneo(id));
    }

    @Transactional
    public TorneoResponse crear(CrearTorneoRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es obligatorio");
        }
        List<CriterioDesempate> criterios = validarConfiguracion(request.nombre(), request.deporte(),
                request.fechaInicio(), request.fechaFin(), request.puntosVictoria(), request.puntosEmpate(),
                request.puntosDerrota(), request.criteriosDesempate());
        if (request.organizadorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "organizadorId es obligatorio");
        }
        UsuarioEntity organizador = usuarioRepository.findById(request.organizadorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El usuario " + request.organizadorId() + " no existe"));
        if (!RolUsuario.ORGANIZADOR.name().equalsIgnoreCase(organizador.getRol())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El usuario " + organizador.getNombre() + " no tiene rol ORGANIZADOR");
        }

        TorneoEntity torneo = new TorneoEntity();
        torneo.setNombre(request.nombre().trim());
        torneo.setDeporte(request.deporte().trim());
        torneo.setFechaInicio(request.fechaInicio());
        torneo.setFechaFin(request.fechaFin());
        torneo.setPuntosVictoria(request.puntosVictoria());
        torneo.setPuntosEmpate(request.puntosEmpate());
        torneo.setPuntosDerrota(request.puntosDerrota());
        torneo.setOrganizador(organizador);
        torneo.setEstado(EstadoTorneo.ABIERTO.name());

        TorneoEntity guardado = torneoRepository.save(torneo);
        return TorneoResponse.desdeEntity(guardado, guardarCriterios(guardado, criterios));
    }

    @Transactional
    public TorneoResponse actualizar(Long id, ActualizarTorneoRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es obligatorio");
        }
        TorneoEntity torneo = buscarTorneo(id);
        EstadoTorneo estado = convertirEstadoGuardado(torneo);
        if (!estado.admiteCambioDeConfiguracion()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El torneo " + torneo.getNombre() + " está " + estado
                            + "; la configuración solo se puede reemplazar antes de iniciar");
        }
        List<CriterioDesempate> criterios = validarConfiguracion(request.nombre(), request.deporte(),
                request.fechaInicio(), request.fechaFin(), request.puntosVictoria(), request.puntosEmpate(),
                request.puntosDerrota(), request.criteriosDesempate());

        torneo.setNombre(request.nombre().trim());
        torneo.setDeporte(request.deporte().trim());
        torneo.setFechaInicio(request.fechaInicio());
        torneo.setFechaFin(request.fechaFin());
        torneo.setPuntosVictoria(request.puntosVictoria());
        torneo.setPuntosEmpate(request.puntosEmpate());
        torneo.setPuntosDerrota(request.puntosDerrota());
        TorneoEntity actualizado = torneoRepository.save(torneo);

        criterioDesempateRepository.eliminarPorTorneo(id);
        return TorneoResponse.desdeEntity(actualizado, guardarCriterios(actualizado, criterios));
    }

    @Transactional
    public TorneoResponse cambiarEstado(Long id, CambiarEstadoTorneoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "estado es obligatorio");
        }
        TorneoEntity torneo = buscarTorneo(id);
        EstadoTorneo actual = convertirEstadoGuardado(torneo);
        EstadoTorneo nuevo = convertirEstado(request.estado());

        if (!actual.puedeCambiarA(nuevo)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El torneo no puede pasar de " + actual + " a " + nuevo);
        }
        if (nuevo == EstadoTorneo.EN_CURSO
                && inscripcionRepository.findByTorneoentity_Id(id).size() < MINIMO_EQUIPOS_PARA_INICIAR) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Se necesitan al menos " + MINIMO_EQUIPOS_PARA_INICIAR + " equipos inscritos para iniciar");
        }
        if (nuevo == EstadoTorneo.FINALIZADO && tienePartidosPendientes(id)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El torneo tiene partidos programados o en juego; no se puede finalizar");
        }

        torneo.setEstado(nuevo.name());
        return aRespuesta(torneoRepository.save(torneo));
    }

    @Transactional
    public InscripcionResponse inscribirEquipo(Long torneoId, InscribirEquipoRequest request) {
        if (request == null || request.equipoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "equipoId es obligatorio");
        }
        TorneoEntity torneo = buscarTorneo(torneoId);
        EquipoEntity equipo = equipoRepository.findById(request.equipoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El equipo " + request.equipoId() + " no existe"));

        EstadoTorneo estado = convertirEstadoGuardado(torneo);
        if (estado != EstadoTorneo.ABIERTO) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "RN-06: el torneo " + torneo.getNombre() + " está " + estado + " y no admite nuevos equipos");
        }
        if (inscripcionRepository.existsByTorneoentity_IdAndEquipoentity_Id(torneoId, equipo.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El equipo " + equipo.getNombre() + " ya está inscrito en el torneo " + torneo.getNombre());
        }

        InscripcionEntity inscripcion = new InscripcionEntity();
        inscripcion.setId(new InscripcionIdEntity(torneo.getId(), equipo.getId()));
        inscripcion.setTorneoentity(torneo);
        inscripcion.setEquipoentity(equipo);
        inscripcion.setFechaInscripcion(LocalDate.now());

        InscripcionEntity guardada = inscripcionRepository.save(inscripcion);
        return InscripcionResponse.desdeEntity(guardada);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> listarInscripciones(Long torneoId) {
        buscarTorneo(torneoId);
        return inscripcionRepository.findByTorneoentity_Id(torneoId)
                .stream()
                .map(InscripcionResponse::desdeEntity)
                .toList();
    }


    private List<CriterioDesempate> validarConfiguracion(String nombre, String deporte,
                                                         LocalDate fechaInicio, LocalDate fechaFin,
                                                         Integer puntosVictoria, Integer puntosEmpate,
                                                         Integer puntosDerrota, List<String> criterios) {
        if (nombre == null || nombre.isBlank() || deporte == null || deporte.isBlank()
                || fechaInicio == null || fechaFin == null
                || puntosVictoria == null || puntosEmpate == null || puntosDerrota == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nombre, deporte, fechaInicio, fechaFin, puntosVictoria, puntosEmpate y puntosDerrota son obligatorios");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaFin no puede ser anterior a fechaInicio");
        }
        if (puntosVictoria < 0 || puntosEmpate < 0 || puntosDerrota < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los puntos no pueden ser negativos");
        }
        if (puntosVictoria <= puntosEmpate || puntosEmpate < puntosDerrota) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Los puntos deben cumplir: victoria > empate >= derrota");
        }
        if (criterios == null || criterios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "RN-05: se debe definir al menos un criterio de desempate. Valores permitidos: "
                            + Arrays.toString(CriterioDesempate.values()));
        }
        List<CriterioDesempate> resultado = new ArrayList<>();
        for (String texto : criterios) {
            CriterioDesempate criterio = convertirCriterio(texto);
            if (resultado.contains(criterio)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "RN-05: el criterio " + criterio + " está repetido");
            }
            resultado.add(criterio);
        }
        return resultado;
    }

    private List<CriterioDesempateEntity> guardarCriterios(TorneoEntity torneo, List<CriterioDesempate> criterios) {
        List<CriterioDesempateEntity> entidades = new ArrayList<>();
        for (int i = 0; i < criterios.size(); i++) {
            CriterioDesempateEntity entidad = new CriterioDesempateEntity();
            entidad.setTorneoentity(torneo);
            entidad.setOrden(i + 1);
            entidad.setCriterio(criterios.get(i).name());
            entidades.add(entidad);
        }
        return criterioDesempateRepository.saveAll(entidades);
    }

    private boolean tienePartidosPendientes(Long torneoId) {
        return !partidoRepository.findByTorneoentity_IdAndEstado(torneoId, EstadoPartido.PROGRAMADO.name()).isEmpty()
                || !partidoRepository.findByTorneoentity_IdAndEstado(torneoId, EstadoPartido.EN_JUEGO.name()).isEmpty();
    }

    private TorneoEntity buscarTorneo(Long id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El torneo " + id + " no existe"));
    }

    private TorneoResponse aRespuesta(TorneoEntity torneo) {
        return TorneoResponse.desdeEntity(torneo,
                criterioDesempateRepository.findByTorneoentity_IdOrderByOrdenAsc(torneo.getId()));
    }

    private EstadoTorneo convertirEstado(String estado) {
        try {
            return EstadoTorneo.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado de torneo inválido: " + estado + ". Valores permitidos: "
                            + Arrays.toString(EstadoTorneo.values()));
        }
    }

    private EstadoTorneo convertirEstadoGuardado(TorneoEntity torneo) {
        try {
            return EstadoTorneo.valueOf(torneo.getEstado().trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El torneo " + torneo.getId() + " tiene un estado desconocido en la BD: " + torneo.getEstado());
        }
    }

    private CriterioDesempate convertirCriterio(String criterio) {
        try {
            return CriterioDesempate.valueOf(criterio.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "RN-05: criterio de desempate inválido: " + criterio + ". Valores permitidos: "
                            + Arrays.toString(CriterioDesempate.values()));
        }
    }
}
