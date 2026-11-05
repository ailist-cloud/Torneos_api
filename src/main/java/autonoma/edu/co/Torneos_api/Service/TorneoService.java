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
import autonoma.edu.co.Torneos_api.Excepciones.ConflictoException;
import autonoma.edu.co.Torneos_api.Excepciones.RecursoNoEncontradoException;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Excepciones.SolicitudInvalidaException;
import autonoma.edu.co.Torneos_api.Repository.CriterioDesempateRepository;
import autonoma.edu.co.Torneos_api.Repository.InscripcionRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TorneoService {

    private static final int MINIMO_EQUIPOS_PARA_INICIAR = 2;

    private final TorneoRepository torneoRepository;
    private final CriterioDesempateRepository criterioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PartidoRepository partidoRepository;
    private final UsuarioService usuarioService;
    private final EquipoService equipoService;

    public TorneoService(TorneoRepository torneoRepository,
                         CriterioDesempateRepository criterioRepository,
                         InscripcionRepository inscripcionRepository,
                         PartidoRepository partidoRepository,
                         UsuarioService usuarioService,
                         EquipoService equipoService) {
        this.torneoRepository = torneoRepository;
        this.criterioRepository = criterioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.partidoRepository = partidoRepository;
        this.usuarioService = usuarioService;
        this.equipoService = equipoService;
    }

    // ---------------------------------------------------------------- consultas

    @Transactional(readOnly = true)
    public List<TorneoResponse> listar() {
        List<TorneoResponse> respuesta = new ArrayList<>();
        for (TorneoEntity torneo : torneoRepository.findAllByOrderByFechaInicioAsc()) {
            respuesta.add(aRespuesta(torneo));
        }
        return respuesta;
    }

    @Transactional(readOnly = true)
    public TorneoResponse consultar(Long id) {
        return aRespuesta(buscarEntidad(id));
    }

    // ---------------------------------------------------------------- POST /api/torneos

    @Transactional
    public TorneoResponse crear(CrearTorneoRequest request) {
        validarFechas(request.fechaInicio(), request.fechaFin());
        validarPuntos(request.puntosVictoria(), request.puntosEmpate(), request.puntosDerrota());
        validarCriterios(request.criteriosDesempate());

        UsuarioEntity organizador = usuarioService.buscarEntidad(request.organizadorId());
        if (organizador.getRol() != RolUsuario.ORGANIZADOR) {
            throw new ReglaNegocioException("El usuario " + organizador.getNombre()
                    + " tiene rol " + organizador.getRol() + "; se necesita rol ORGANIZADOR");
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
        torneo.setEstado(EstadoTorneo.ABIERTO);
        torneo = torneoRepository.save(torneo);

        List<CriterioDesempateEntity> criterios = guardarCriterios(torneo, request.criteriosDesempate());
        return TorneoResponse.desdeEntity(torneo, criterios);
    }

    // ---------------------------------------------------------------- PUT /api/torneos/{id}

    @Transactional
    public TorneoResponse actualizar(Long id, ActualizarTorneoRequest request) {
        TorneoEntity torneo = buscarEntidad(id);
        if (!torneo.getEstado().admiteCambioDeConfiguracion()) {
            throw new ReglaNegocioException("El torneo " + torneo.getNombre() + " está " + torneo.getEstado()
                    + "; la configuración solo se puede reemplazar antes de iniciar");
        }
        validarFechas(request.fechaInicio(), request.fechaFin());
        validarPuntos(request.puntosVictoria(), request.puntosEmpate(), request.puntosDerrota());
        validarCriterios(request.criteriosDesempate());

        torneo.setNombre(request.nombre().trim());
        torneo.setDeporte(request.deporte().trim());
        torneo.setFechaInicio(request.fechaInicio());
        torneo.setFechaFin(request.fechaFin());
        torneo.setPuntosVictoria(request.puntosVictoria());
        torneo.setPuntosEmpate(request.puntosEmpate());
        torneo.setPuntosDerrota(request.puntosDerrota());
        torneo = torneoRepository.save(torneo);

        // Se reemplazan los criterios: se borran los viejos y se guardan los nuevos
        criterioRepository.eliminarPorTorneo(id);
        List<CriterioDesempateEntity> criterios = guardarCriterios(torneo, request.criteriosDesempate());
        return TorneoResponse.desdeEntity(torneo, criterios);
    }

    // ---------------------------------------------------------------- PATCH /api/torneos/{id}/estado

    @Transactional
    public TorneoResponse cambiarEstado(Long id, CambiarEstadoTorneoRequest request) {
        TorneoEntity torneo = buscarEntidad(id);
        EstadoTorneo actual = torneo.getEstado();
        EstadoTorneo nuevo = request.estado();

        if (!actual.puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException("El torneo no puede pasar de " + actual + " a " + nuevo);
        }
        if (nuevo == EstadoTorneo.EN_CURSO && inscripcionRepository.countByTorneoId(id) < MINIMO_EQUIPOS_PARA_INICIAR) {
            throw new ReglaNegocioException("Se necesitan al menos " + MINIMO_EQUIPOS_PARA_INICIAR
                    + " equipos inscritos para iniciar el torneo");
        }
        if (nuevo == EstadoTorneo.FINALIZADO && tienePartidosPendientes(id)) {
            throw new ReglaNegocioException("El torneo tiene partidos programados o en juego; no se puede finalizar");
        }

        torneo.setEstado(nuevo);
        return aRespuesta(torneoRepository.save(torneo));
    }

    // ---------------------------------------------------------------- inscripciones

    @Transactional
    public InscripcionResponse inscribirEquipo(Long torneoId, InscribirEquipoRequest request) {
        TorneoEntity torneo = buscarEntidad(torneoId);
        EquipoEntity equipo = equipoService.buscarEntidad(request.equipoId());

        if (!torneo.getEstado().admiteInscripciones()) {
            throw new ReglaNegocioException("RN-06: el torneo " + torneo.getNombre() + " está "
                    + torneo.getEstado() + " y no admite nuevos equipos");
        }
        if (inscripcionRepository.existsByTorneoIdAndEquipoId(torneoId, equipo.getId())) {
            throw new ConflictoException("El equipo " + equipo.getNombre()
                    + " ya está inscrito en el torneo " + torneo.getNombre());
        }

        InscripcionEntity inscripcion = new InscripcionEntity(torneo, equipo, LocalDate.now());
        return InscripcionResponse.desdeEntity(inscripcionRepository.save(inscripcion));
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> listarInscripciones(Long torneoId) {
        buscarEntidad(torneoId); // 404 si el torneo no existe
        List<InscripcionResponse> respuesta = new ArrayList<>();
        for (InscripcionEntity inscripcion : inscripcionRepository.findByTorneoIdOrderByFechaInscripcionAsc(torneoId)) {
            respuesta.add(InscripcionResponse.desdeEntity(inscripcion));
        }
        return respuesta;
    }

    // ---------------------------------------------------------------- métodos que usan otros servicios

    public TorneoEntity buscarEntidad(Long id) {
        Optional<TorneoEntity> torneo = torneoRepository.findById(id);
        if (torneo.isEmpty()) {
            throw new RecursoNoEncontradoException("torneo", id);
        }
        return torneo.get();
    }

    public boolean equipoEstaInscrito(Long torneoId, Long equipoId) {
        return inscripcionRepository.existsByTorneoIdAndEquipoId(torneoId, equipoId);
    }

    // ---------------------------------------------------------------- validaciones privadas

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (fin.isBefore(inicio)) {
            throw new SolicitudInvalidaException("fechaFin no puede ser anterior a fechaInicio");
        }
    }

    private void validarPuntos(int victoria, int empate, int derrota) {
        if (victoria <= empate || empate < derrota) {
            throw new SolicitudInvalidaException("Los puntos deben cumplir: victoria > empate >= derrota");
        }
    }

    /** RN-05: al menos un criterio (lo valida @NotEmpty) y sin repetidos. */
    private void validarCriterios(List<CriterioDesempate> criterios) {
        List<CriterioDesempate> vistos = new ArrayList<>();
        for (CriterioDesempate criterio : criterios) {
            if (criterio == null) {
                throw new SolicitudInvalidaException("RN-05: la lista de criterios tiene un valor vacío");
            }
            if (vistos.contains(criterio)) {
                throw new SolicitudInvalidaException("RN-05: el criterio " + criterio + " está repetido");
            }
            vistos.add(criterio);
        }
    }

    private List<CriterioDesempateEntity> guardarCriterios(TorneoEntity torneo, List<CriterioDesempate> criterios) {
        List<CriterioDesempateEntity> entidades = new ArrayList<>();
        int orden = 1;
        for (CriterioDesempate criterio : criterios) {
            entidades.add(new CriterioDesempateEntity(torneo, orden, criterio));
            orden++;
        }
        return criterioRepository.saveAll(entidades);
    }

    private boolean tienePartidosPendientes(Long torneoId) {
        return partidoRepository.existsByTorneoIdAndEstadoIn(torneoId,
                List.of(EstadoPartido.PROGRAMADO, EstadoPartido.EN_JUEGO));
    }

    private TorneoResponse aRespuesta(TorneoEntity torneo) {
        return TorneoResponse.desdeEntity(torneo, criterioRepository.findByTorneoIdOrderByOrdenAsc(torneo.getId()));
    }
}
