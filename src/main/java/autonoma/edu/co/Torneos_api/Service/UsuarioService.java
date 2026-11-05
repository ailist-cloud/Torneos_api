package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Dto.CrearUsuarioRequest;
import autonoma.edu.co.Torneos_api.Dto.UsuarioResponse;
import autonoma.edu.co.Torneos_api.Excepciones.ConflictoException;
import autonoma.edu.co.Torneos_api.Excepciones.RecursoNoEncontradoException;
import autonoma.edu.co.Torneos_api.Repository.UsuarioRepository;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        List<UsuarioResponse> respuesta = new ArrayList<>();
        for (UsuarioEntity usuario : usuarioRepository.findAllByOrderByIdAsc()) {
            respuesta.add(UsuarioResponse.desdeEntity(usuario));
        }
        return respuesta;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse consultar(Long id) {
        return UsuarioResponse.desdeEntity(buscarEntidad(id));
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictoException("Ya existe un usuario con el email " + email);
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setRol(request.rol());

        return UsuarioResponse.desdeEntity(usuarioRepository.save(usuario));
    }

    /** Lo usan otros servicios (Equipo, Torneo) para obtener el usuario o responder 404. */
    public UsuarioEntity buscarEntidad(Long id) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new RecursoNoEncontradoException("usuario", id);
        }
        return usuario.get();
    }
}
