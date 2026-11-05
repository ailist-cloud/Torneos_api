package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** RN-03: los goles no pueden ser negativos (lo valida @Min antes de llegar al servicio). */
public record RegistrarResultadoRequest(
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "RN-03: no puede ser negativo") Integer golesLocal,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "RN-03: no puede ser negativo") Integer golesVisitante
) { }
