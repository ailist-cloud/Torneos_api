package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.EquipoEntity;

public record EquipoResponse(
        Long id,
        String nombre,
        String ciudad,
        Long representanteId,
        String representanteNombre
) {
    public static EquipoResponse desdeEntity(EquipoEntity entity) {
        return new EquipoResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getCiudad(),
                entity.getRepresentante() != null ? entity.getRepresentante().getId() : null,
                entity.getRepresentante() != null ? entity.getRepresentante().getNombre() : null
        );
    }
}
