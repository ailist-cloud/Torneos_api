package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * RN-03: los goles no pueden ser negativos (lo valida @Min antes de llegar al servicio).
 *
 * @param golesLocal Goles del equipo local (no negativos, RN-03).
 * @param golesVisitante Goles del equipo visitante (no negativos, RN-03).
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record RegistrarResultadoRequest(
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "RN-03: no puede ser negativo") Integer golesLocal,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "RN-03: no puede ser negativo") Integer golesVisitante
) { }
