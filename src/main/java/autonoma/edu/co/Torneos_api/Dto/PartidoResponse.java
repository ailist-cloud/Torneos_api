package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import java.time.LocalDateTime;

public record PartidoResponse(
        Long id,
        Long torneoId,
        String torneoNombre,
        String equipoLocal,
        String equipoVisitante,
        LocalDateTime fechaHoraInicio,
        Integer duracionMin,
        String estado,
        Integer golesLocal,
        Integer golesVisitante
) {

    public static PartidoResponse desdeEntity(PartidoEntity entity) {
        return new PartidoResponse(
                entity.getId(),
                entity.getTorneoentity() != null ? entity.getTorneoentity().getId() : null,
                entity.getTorneoentity() != null ? entity.getTorneoentity().getNombre() : null,
                entity.getEquipoEntityLocal() != null ? entity.getEquipoEntityLocal().getNombre() : null,
                entity.getEquipoEntityVisitante() != null ? entity.getEquipoEntityVisitante().getNombre() : null,
                entity.getFechaHoraInicio(),
                entity.getDuracionMin(),
                entity.getEstado(),
                entity.getGolesLocal(),
                entity.getGolesVisitante()
        );
    }
}