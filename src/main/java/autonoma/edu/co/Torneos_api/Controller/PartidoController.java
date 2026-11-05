package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Dto.RegistrarResultadoRequest;
import autonoma.edu.co.Torneos_api.Service.PartidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidoResponse programarPartido(@Valid @RequestBody CrearPartidoRequest request) {
        return partidoService.programarPartido(request);
    }

    @PatchMapping("/{id}/estado")
    public PartidoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoPartidoRequest request) {
        return partidoService.cambiarEstado(id, request);
    }

    @PatchMapping("/{id}/resultado")
    public PartidoResponse registrarResultado(@PathVariable Long id, @Valid @RequestBody RegistrarResultadoRequest request) {
        return partidoService.registrarResultado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPartido(@PathVariable Long id) {
        partidoService.eliminarPartido(id);
    }
}
