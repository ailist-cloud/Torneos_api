package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.EstadoResponse;
import autonoma.edu.co.Torneos_api.Service.EstadoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estado")
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping
    public EstadoResponse consultarEstado() {
        return estadoService.consultarEstado();
    }
}