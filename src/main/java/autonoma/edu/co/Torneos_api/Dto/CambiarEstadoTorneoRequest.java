package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de {@code PATCH /api/torneos/{id}/estado}: el nuevo estado del torneo.
 *
 * @param (ABIERTO ( a b i e r t o.
 * @param CERRADO  c e r r a d o.
 * @param estado Estado actual.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record CambiarEstadoTorneoRequest(
        @NotNull(message = "es obligatorio (ABIERTO, CERRADO, EN_CURSO o FINALIZADO)") EstadoTorneo estado
) { }
