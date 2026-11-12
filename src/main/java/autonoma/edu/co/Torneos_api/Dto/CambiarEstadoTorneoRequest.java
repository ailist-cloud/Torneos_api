package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de {@code PATCH /api/torneos/{id}/estado}: el nuevo estado del torneo.
 *
 * @param estado nuevo estado del torneo: ABIERTO, CERRADO, EN_CURSO o FINALIZADO
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record CambiarEstadoTorneoRequest(
        @NotNull(message = "es obligatorio (ABIERTO, CERRADO, EN_CURSO o FINALIZADO)") EstadoTorneo estado
) { }
