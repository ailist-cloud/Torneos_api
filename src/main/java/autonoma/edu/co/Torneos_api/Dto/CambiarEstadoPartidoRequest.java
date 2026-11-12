package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import jakarta.validation.constraints.NotNull;

/** Cuerpo de {@code PATCH /api/partidos/{id}/estado}: el nuevo estado del partido. */
public record CambiarEstadoPartidoRequest(
        @NotNull(message = "es obligatorio (EN_JUEGO, FINALIZADO o CANCELADO)") EstadoPartido estado
) { }
