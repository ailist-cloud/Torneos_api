package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Dto.RegistrarResultadoRequest;
import autonoma.edu.co.Torneos_api.Service.PartidoService;
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

    @GetMapping
    public List<PartidoResponse> consultarProgramacion() {
        return partidoService.consultarProgramacion();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidoResponse programarPartido(@RequestBody CrearPartidoRequest request) {
        return partidoService.programarPartido(request);
    }

    @PatchMapping("/{id}/resultado")
    public PartidoResponse registrarResultado(@PathVariable Long id, @RequestBody RegistrarResultadoRequest request) {
        return partidoService.registrarResultado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPartido(@PathVariable Long id) {
        partidoService.eliminarPartido(id);
    }
}