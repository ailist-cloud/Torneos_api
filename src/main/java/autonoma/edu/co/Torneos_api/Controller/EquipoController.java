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

/**
 * Endpoints de equipos: {@code /api/equipos}.
 * Los GET son públicos; crear un equipo exige el header {@code X-Usuario-Id} de un ORGANIZADOR.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    /** GET /api/equipos — lista todos los equipos. */
    @GetMapping
    public List<EquipoResponse> listar() {
        return equipoService.listar();
    }

    /** GET /api/equipos/{id} — consulta un equipo (404 si no existe). */
    @GetMapping("/{id}")
    public EquipoResponse consultar(@PathVariable Long id) {
        return equipoService.consultar(id);
    }

    /** POST /api/equipos — crea un equipo. Requiere header X-Usuario-Id de un ORGANIZADOR. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoResponse crear(@Valid @RequestBody CrearEquipoRequest request,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return equipoService.crear(request, usuarioId);
    }
}
