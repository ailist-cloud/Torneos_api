package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.Dto.CrearUsuarioRequest;
import autonoma.edu.co.Torneos_api.Dto.UsuarioResponse;
import autonoma.edu.co.Torneos_api.Repository.UsuarioRepository;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .map(UsuarioResponse::desdeEntity)
                .toList();
    }



    @Transactional(readOnly = true)
    public UsuarioResponse consultar(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El usuario " + id + " no existe"));
        return UsuarioResponse.desdeEntity(usuario);
    }


    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        if (request == null || vacio(request.nombre()) || vacio(request.email()) || vacio(request.rol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre, email y rol son obligatorios");
        }
        String email = request.email().trim();
        if (!email.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email " + email + " no es válido");
        }
        RolUsuario rol = convertirRol(request.rol());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el email " + email);
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setRol(rol.name());

        UsuarioEntity guardado = usuarioRepository.save(usuario);
        return UsuarioResponse.desdeEntity(guardado);
    }


    private RolUsuario convertirRol(String rol) {
        try {
            return RolUsuario.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rol inválido: " + rol + ". Valores permitidos: " + Arrays.toString(RolUsuario.values()));
        }
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
