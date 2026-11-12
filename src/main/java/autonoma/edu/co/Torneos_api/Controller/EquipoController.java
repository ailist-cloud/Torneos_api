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
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    /**
     * GET /api/equipos — lista todos los equipos.
     *
     * @return lista con los equipos
     */
    @GetMapping
    public List<EquipoResponse> listar() {
        return equipoService.listar();
    }

    /**
     * GET /api/equipos/{id} — consulta un equipo (404 si no existe).
     *
     * @param id id del equipo
     * @return los datos del equipo
     */
    @GetMapping("/{id}")
    public EquipoResponse consultar(@PathVariable Long id) {
        return equipoService.consultar(id);
    }

    /**
     * POST /api/equipos — crea un equipo. Requiere header X-Usuario-Id de un ORGANIZADOR.
     *
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del equipo
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoResponse crear(@Valid @RequestBody CrearEquipoRequest request,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return equipoService.crear(request, usuarioId);
    }
}
