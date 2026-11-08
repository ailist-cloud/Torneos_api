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

/**
 * Los GET son públicos. Las operaciones que modifican datos exigen el header
 * X-Usuario-Id con el organizador del torneo.
 */
@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    /** GET /api/partidos  |  ?torneoId=1  |  ?equipoId=3 */
    @GetMapping
    public List<PartidoResponse> consultarProgramacion(@RequestParam(required = false) Long torneoId,
                                                       @RequestParam(required = false) Long equipoId) {
        return partidoService.consultarProgramacion(torneoId, equipoId);
    }

    @GetMapping("/{id}")
    public PartidoResponse consultar(@PathVariable Long id) {
        return partidoService.consultar(id);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialResponse> consultarHistorial(@PathVariable Long id) {
        return partidoService.consultarHistorial(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidoResponse programarPartido(@Valid @RequestBody CrearPartidoRequest request,
                                            @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return partidoService.programarPartido(request, usuarioId);
    }

    @PatchMapping("/{id}/estado")
    public PartidoResponse cambiarEstado(@PathVariable Long id,
                                         @Valid @RequestBody CambiarEstadoPartidoRequest request,
                                         @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return partidoService.cambiarEstado(id, request, usuarioId);
    }

    @PatchMapping("/{id}/resultado")
    public PartidoResponse registrarResultado(@PathVariable Long id,
                                              @Valid @RequestBody RegistrarResultadoRequest request,
                                              @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return partidoService.registrarResultado(id, request, usuarioId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPartido(@PathVariable Long id,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        partidoService.eliminarPartido(id, usuarioId);
    }
}
