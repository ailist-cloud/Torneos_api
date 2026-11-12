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
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** GET /api/usuarios — lista todos los usuarios. */
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    /** GET /api/usuarios/{id} — consulta un usuario (404 si no existe). */
    @GetMapping("/{id}")
    public UsuarioResponse consultar(@PathVariable Long id) {
        return usuarioService.consultar(id);
    }

    /** POST /api/usuarios — registra un usuario (409 si el email ya existe). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return usuarioService.crear(request);
    }
}
