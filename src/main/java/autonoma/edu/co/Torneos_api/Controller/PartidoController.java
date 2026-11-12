package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.HistorialResponse;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Dto.RegistrarResultadoRequest;
import autonoma.edu.co.Torneos_api.Service.AutorizacionService;
import autonoma.edu.co.Torneos_api.Service.PartidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Los GET son públicos. Las operaciones que modifican datos exigen el header
 * X-Usuario-Id con el organizador del torneo.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    /**
     * GET /api/partidos  |  ?torneoId=1  |  ?equipoId=3
     *
     * @param torneoId id del torneo
     * @param equipoId id del equipo
     * @return lista con los partidos
     */
    @GetMapping
    public List<PartidoResponse> consultarProgramacion(@RequestParam(required = false) Long torneoId,
                                                       @RequestParam(required = false) Long equipoId) {
        return partidoService.consultarProgramacion(torneoId, equipoId);
    }

    /**
     * Consultar.
     *
     * @param id id del partido
     * @return los datos del partido
     */
    @GetMapping("/{id}")
    public PartidoResponse consultar(@PathVariable Long id) {
        return partidoService.consultar(id);
    }

    /**
     * Consultar historial.
     *
     * @param id id del partido
     * @return lista con los cambios registrados, del más antiguo al más reciente
     */
    @GetMapping("/{id}/historial")
    public List<HistorialResponse> consultarHistorial(@PathVariable Long id) {
        return partidoService.consultarHistorial(id);
    }

    /**
     * Programar partido.
     *
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del partido
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidoResponse programarPartido(@Valid @RequestBody CrearPartidoRequest request,
                                            @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return partidoService.programarPartido(request, usuarioId);
    }

    /**
     * Cambiar estado.
     *
     * @param id id del partido
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del partido
     */
    @PatchMapping("/{id}/estado")
    public PartidoResponse cambiarEstado(@PathVariable Long id,
                                         @Valid @RequestBody CambiarEstadoPartidoRequest request,
                                         @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return partidoService.cambiarEstado(id, request, usuarioId);
    }

    /**
     * Registrar resultado.
     *
     * @param id id del partido
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del partido
     */
    @PatchMapping("/{id}/resultado")
    public PartidoResponse registrarResultado(@PathVariable Long id,
                                              @Valid @RequestBody RegistrarResultadoRequest request,
                                              @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return partidoService.registrarResultado(id, request, usuarioId);
    }

    /**
     * Eliminar partido.
     *
     * @param id id del partido
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPartido(@PathVariable Long id,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        partidoService.eliminarPartido(id, usuarioId);
    }
}
