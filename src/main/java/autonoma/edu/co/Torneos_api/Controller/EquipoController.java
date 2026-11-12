package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.CrearEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.EquipoResponse;
import autonoma.edu.co.Torneos_api.Service.AutorizacionService;
import autonoma.edu.co.Torneos_api.Service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping
    public List<EquipoResponse> listar() {
        return equipoService.listar();
    }

    @GetMapping("/{id}")
    public EquipoResponse consultar(@PathVariable Long id) {
        return equipoService.consultar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoResponse crear(@Valid @RequestBody CrearEquipoRequest request,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return equipoService.crear(request, usuarioId);
    }
}
