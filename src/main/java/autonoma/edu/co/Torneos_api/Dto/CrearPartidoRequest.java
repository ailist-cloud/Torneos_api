package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDateTime;

public record CrearPartidoRequest(
        Long torneoId,
        Long equipoLocalId,
        Long equipoVisitanteId,
        LocalDateTime fechaHoraInicio,
        Integer duracionMin
) { }