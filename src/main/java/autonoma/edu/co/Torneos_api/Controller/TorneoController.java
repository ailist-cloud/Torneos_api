package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.ActualizarTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.HistorialResponse;
import autonoma.edu.co.Torneos_api.Dto.InscribirEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscripcionResponse;
import autonoma.edu.co.Torneos_api.Dto.TablaPosicionesResponse;
import autonoma.edu.co.Torneos_api.Dto.TorneoResponse;
import autonoma.edu.co.Torneos_api.Service.AutorizacionService;
import autonoma.edu.co.Torneos_api.Service.TablaPosicionesService;
import autonoma.edu.co.Torneos_api.Service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de torneos: {@code /api/torneos}.
 * Incluye configuración (PUT), estados, inscripciones (RN-06), historial y la tabla de posiciones (RN-04).
 * Los GET son públicos; las operaciones que modifican exigen el header {@code X-Usuario-Id}.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final TablaPosicionesService tablaPosicionesService;

    /**
     * GET /api/torneos — lista los torneos con sus criterios de desempate.
     *
     * @return lista con los torneos
     */
    @GetMapping
    public List<TorneoResponse> listar() {
        return torneoService.listar();
    }

    /**
     * GET /api/torneos/{id} — consulta un torneo (404 si no existe).
     *
     * @param id id del torneo
     * @return los datos del torneo con sus criterios
     */
    @GetMapping("/{id}")
    public TorneoResponse consultar(@PathVariable Long id) {
        return torneoService.consultar(id);
    }

    /**
     * POST /api/torneos — crea un torneo ABIERTO; el organizador es el usuario del header X-Usuario-Id.
     *
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del torneo con sus criterios
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TorneoResponse crear(@Valid @RequestBody CrearTorneoRequest request,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.crear(request, usuarioId);
    }

    /**
     * PUT /api/torneos/{id} — reemplaza la configuración antes de iniciar (422 si ya está EN_CURSO o FINALIZADO).
     *
     * @param id id del torneo
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del torneo con sus criterios
     */
    @PutMapping("/{id}")
    public TorneoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarTorneoRequest request,
                                     @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.actualizar(id, request, usuarioId);
    }

    /**
     * PATCH /api/torneos/{id}/estado — cambia el estado del torneo (ABIERTO → CERRADO → EN_CURSO → FINALIZADO).
     *
     * @param id id del torneo
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del torneo con sus criterios
     */
    @PatchMapping("/{id}/estado")
    public TorneoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoTorneoRequest request,
                                        @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.cambiarEstado(id, request, usuarioId);
    }

    /**
     * GET /api/torneos/{id}/historial — trazabilidad de los cambios del torneo.
     *
     * @param id id del torneo
     * @return lista con los cambios registrados, del más antiguo al más reciente
     */
    @GetMapping("/{id}/historial")
    public List<HistorialResponse> consultarHistorial(@PathVariable Long id) {
        return torneoService.consultarHistorial(id);
    }

    /**
     * GET /api/torneos/{id}/tabla — calcula la tabla de posiciones desde los resultados (RN-04, RN-05).
     *
     * @param id id del torneo
     * @return la tabla de posiciones calculada
     */
    @GetMapping("/{id}/tabla")
    public TablaPosicionesResponse calcularTabla(@PathVariable Long id) {
        return tablaPosicionesService.calcular(id);
    }

    /**
     * POST /api/torneos/{id}/inscripciones — inscribe un equipo (422 si el torneo no está ABIERTO, RN-06).
     *
     * @param id id del torneo
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos de la inscripción
     */
    @PostMapping("/{id}/inscripciones")
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse inscribirEquipo(@PathVariable Long id, @Valid @RequestBody InscribirEquipoRequest request,
                                               @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.inscribirEquipo(id, request, usuarioId);
    }

    /**
     * GET /api/torneos/{id}/inscripciones — lista los equipos inscritos en el torneo.
     *
     * @param id id del torneo
     * @return lista con las inscripciones
     */
    @GetMapping("/{id}/inscripciones")
    public List<InscripcionResponse> listarInscripciones(@PathVariable Long id) {
        return torneoService.listarInscripciones(id);
    }
}
