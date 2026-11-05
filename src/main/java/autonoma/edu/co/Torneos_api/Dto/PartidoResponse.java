package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;

import java.time.LocalDateTime;

public record PartidoResponse(
        Long id,
        Long torneoId,
        String torneoNombre,
        Long equipoLocalId,
        String equipoLocal,
        Long equipoVisitanteId,
        String equipoVisitante,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        Integer duracionMin,
        EstadoPartido estado,
        Integer golesLocal,
        Integer golesVisitante
) {
    public static PartidoResponse desdeEntity(PartidoEntity partido) {
        return new PartidoResponse(
                partido.getId(),
                partido.getTorneo().getId(),
                partido.getTorneo().getNombre(),
                partido.getEquipoLocal().getId(),
                partido.getEquipoLocal().getNombre(),
                partido.getEquipoVisitante().getId(),
                partido.getEquipoVisitante().getNombre(),
                partido.getFechaHoraInicio(),
                partido.calcularFechaHoraFin(),
                partido.getDuracionMin(),
                partido.getEstado(),
                partido.getGolesLocal(),
                partido.getGolesVisitante()
        );
    }
}
