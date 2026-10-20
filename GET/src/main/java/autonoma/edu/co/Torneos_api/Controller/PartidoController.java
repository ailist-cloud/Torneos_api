package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Service.PartidoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @GetMapping
    public List<PartidoResponse> listar(@RequestParam(required = false) Long torneoId,
                                        @RequestParam(required = false) Long equipoId,
                                        @RequestParam(required = false) String estado) {
        return partidoService.listar(torneoId, equipoId, estado);
    }
}
