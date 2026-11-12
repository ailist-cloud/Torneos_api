package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.CrearUsuarioRequest;
import autonoma.edu.co.Torneos_api.Dto.UsuarioResponse;
import autonoma.edu.co.Torneos_api.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de usuarios: {@code /api/usuarios}.
 * Los usuarios tienen rol ORGANIZADOR, REPRESENTANTE o PUBLICO.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * GET /api/usuarios — lista todos los usuarios.
     *
     * @return lista con los usuarios
     */
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    /**
     * GET /api/usuarios/{id} — consulta un usuario (404 si no existe).
     *
     * @param id id del usuario
     * @return los datos del usuario
     */
    @GetMapping("/{id}")
    public UsuarioResponse consultar(@PathVariable Long id) {
        return usuarioService.consultar(id);
    }

    /**
     * POST /api/usuarios — registra un usuario (409 si el email ya existe).
     *
     * @param request datos enviados en el cuerpo de la petición
     * @return los datos del usuario
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return usuarioService.crear(request);
    }
}
