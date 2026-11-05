package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;

public record EquipoResponse(
        Long id,
        String nombre,
        String ciudad,
        Long representanteId,
        String representanteNombre
) {
    public static EquipoResponse desdeEntity(EquipoEntity equipo) {
        UsuarioEntity representante = equipo.getRepresentante();
        Long representanteId = null;
        String representanteNombre = null;
        if (representante != null) {
            representanteId = representante.getId();
            representanteNombre = representante.getNombre();
        }
        return new EquipoResponse(equipo.getId(), equipo.getNombre(), equipo.getCiudad(),
                representanteId, representanteNombre);
    }
}
