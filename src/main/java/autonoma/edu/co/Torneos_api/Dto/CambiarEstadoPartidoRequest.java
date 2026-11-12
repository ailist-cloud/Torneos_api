package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de {@code PATCH /api/partidos/{id}/estado}: el nuevo estado del partido.
 *
 * @param estado nuevo estado del partido: EN_JUEGO, FINALIZADO o CANCELADO
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record CambiarEstadoPartidoRequest(
        @NotNull(message = "es obligatorio (EN_JUEGO, FINALIZADO o CANCELADO)") EstadoPartido estado
) { }
