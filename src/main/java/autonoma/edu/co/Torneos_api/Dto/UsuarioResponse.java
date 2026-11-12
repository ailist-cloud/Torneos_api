package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;

/** Respuesta con los datos de un usuario. */
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        RolUsuario rol
) {
    public static UsuarioResponse desdeEntity(UsuarioEntity usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
