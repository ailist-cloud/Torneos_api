package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearEquipoRequest(
        @NotBlank(message = "es obligatorio") @Size(max = 100) String nombre,
        @Size(max = 100) String ciudad,
        @NotNull(message = "es obligatorio") Long representanteId
) { }
