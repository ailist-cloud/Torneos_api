package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.Excepciones.AccesoDenegadoException;
import autonoma.edu.co.Torneos_api.Excepciones.NoAutenticadoException;
import autonoma.edu.co.Torneos_api.Repository.UsuarioRepository;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Autorización sencilla por header (sin login):
 *   En Postman se envía el header  X-Usuario-Id: 1  en las peticiones que modifican datos.
 *   - Los GET son públicos (Público y Equipos consultan programación, resultados y tabla).
 *   - Crear equipos y torneos: solo usuarios con rol ORGANIZADOR.
 *   - Modificar un torneo o sus partidos: solo el ORGANIZADOR dueño de ese torneo.
 */
@RequiredArgsConstructor
@Service
public class AutorizacionService {

    public static final String CABECERA_USUARIO = "X-Usuario-Id";

    private final UsuarioRepository usuarioRepository;

    /** 401 si no llega el header o el usuario no existe. */
    public UsuarioEntity identificar(Long usuarioId) {
        if (usuarioId == null) {
            throw new NoAutenticadoException("Falta el header " + CABECERA_USUARIO + " con el id del usuario que hace la operación");
        }
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            throw new NoAutenticadoException("El usuario " + usuarioId + " del header " + CABECERA_USUARIO + " no existe");
        }
        return usuario.get();
    }

    /** 403 si el usuario no tiene rol ORGANIZADOR. */
    public UsuarioEntity exigirOrganizador(Long usuarioId) {
        UsuarioEntity usuario = identificar(usuarioId);
        if (usuario.getRol() != RolUsuario.ORGANIZADOR) {
            throw new AccesoDenegadoException("El usuario " + usuario.getNombre() + " tiene rol " + usuario.getRol()
                    + "; esta operación solo la puede hacer un ORGANIZADOR");
        }
        return usuario;
    }

    /** 403 si el usuario no es el organizador de ESE torneo. */
    public UsuarioEntity exigirOrganizadorDelTorneo(Long usuarioId, TorneoEntity torneo) {
        UsuarioEntity usuario = exigirOrganizador(usuarioId);
        if (!torneo.getOrganizador().getId().equals(usuario.getId())) {
            throw new AccesoDenegadoException("El usuario " + usuario.getNombre()
                    + " no es el organizador del torneo " + torneo.getNombre());
        }
        return usuario;
    }
}
