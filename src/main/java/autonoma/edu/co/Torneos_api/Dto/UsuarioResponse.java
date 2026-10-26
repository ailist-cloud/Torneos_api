package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String rol
) {
    public static UsuarioResponse desdeEntity(UsuarioEntity entity) {
        return new UsuarioResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getRol()
        );
    }
}
