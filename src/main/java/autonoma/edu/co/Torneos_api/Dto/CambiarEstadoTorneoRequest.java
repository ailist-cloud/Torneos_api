package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoTorneoRequest(
        @NotNull(message = "es obligatorio (ABIERTO, CERRADO, EN_CURSO o FINALIZADO)") EstadoTorneo estado
) { }
