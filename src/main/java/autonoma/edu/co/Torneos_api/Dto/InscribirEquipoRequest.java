package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.NotNull;

public record InscribirEquipoRequest(
        @NotNull(message = "es obligatorio") Long equipoId
) { }
