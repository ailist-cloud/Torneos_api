package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de {@code POST /api/equipos}. El representante debe ser un usuario con rol REPRESENTANTE.
 *
 * @param nombre Nombre.
 * @param ciudad Ciudad del equipo.
 * @param representanteId Id del usuario representante del equipo.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record CrearEquipoRequest(
        @NotBlank(message = "es obligatorio") @Size(max = 100) String nombre,
        @Size(max = 100) String ciudad,
        @NotNull(message = "es obligatorio") Long representanteId
) { }
