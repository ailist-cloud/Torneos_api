package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CrearPartidoRequest(
        @NotNull(message = "es obligatorio") Long torneoId,
        @NotNull(message = "es obligatorio") Long equipoLocalId,
        @NotNull(message = "es obligatorio") Long equipoVisitanteId,
        @NotNull(message = "es obligatoria (yyyy-MM-ddTHH:mm:ss)") LocalDateTime fechaHoraInicio,
        @NotNull(message = "es obligatoria") @Positive(message = "debe ser mayor que 0") Integer duracionMin
) { }
