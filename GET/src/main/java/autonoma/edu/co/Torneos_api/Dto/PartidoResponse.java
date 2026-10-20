package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDateTime;

public record PartidoResponse(
        Long id,
        Long torneoId,
        Long equipoLocalId,
        String equipoLocal,
        Long equipoVisitanteId,
        String equipoVisitante,
        LocalDateTime fechaHoraInicio,
        Integer duracionMin,
        String estado,
        Integer golesLocal,
        Integer golesVisitante) {
}
