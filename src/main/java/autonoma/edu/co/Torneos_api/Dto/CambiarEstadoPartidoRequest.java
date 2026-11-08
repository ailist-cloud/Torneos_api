package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoPartidoRequest(
        @NotNull(message = "es obligatorio (EN_JUEGO, FINALIZADO o CANCELADO)") EstadoPartido estado
) { }
