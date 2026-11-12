package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;

/**
 * Respuesta con los datos de un equipo y su representante.
 *
 * @param id Identificador único (autoincremental).
 * @param nombre Nombre.
 * @param ciudad Ciudad del equipo.
 * @param representanteId Id del usuario representante del equipo.
 * @param representanteNombre Nombre del representante.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record EquipoResponse(
        Long id,
        String nombre,
        String ciudad,
        Long representanteId,
        String representanteNombre
) {
    /**
     * Desde entity.
     *
     * @param equipo equipo
     * @return los datos del equipo
     */
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
