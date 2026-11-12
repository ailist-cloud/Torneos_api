package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;

/**
 * Respuesta con los datos de un usuario.
 *
 * @param id Identificador único (autoincremental).
 * @param nombre Nombre.
 * @param email Correo electrónico (único).
 * @param rol Rol del usuario: ORGANIZADOR, REPRESENTANTE o PUBLICO.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        RolUsuario rol
) {
    /**
     * Desde entity.
     *
     * @param usuario usuario que hace el cambio
     * @return los datos del usuario
     */
    public static UsuarioResponse desdeEntity(UsuarioEntity usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
