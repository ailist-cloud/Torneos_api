package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import jakarta.validation.constraints.NotNull;

/** Cuerpo de {@code PATCH /api/torneos/{id}/estado}: el nuevo estado del torneo. */
public record CambiarEstadoTorneoRequest(
        @NotNull(message = "es obligatorio (ABIERTO, CERRADO, EN_CURSO o FINALIZADO)") EstadoTorneo estado
) { }
