package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import java.time.LocalDate;
import java.util.List;

public record TorneoResponse(
        Long id,
        String nombre,
        String deporte,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        Integer puntosVictoria,
        Integer puntosEmpate,
        Integer puntosDerrota,
        Long organizadorId,
        String organizadorNombre,
        List<CriterioDesempateResponse> criteriosDesempate
) {
    public static TorneoResponse desdeEntity(TorneoEntity entity, List<CriterioDesempateEntity> criterios) {
        return new TorneoResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getDeporte(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getEstado(),
                entity.getPuntosVictoria(),
                entity.getPuntosEmpate(),
                entity.getPuntosDerrota(),
                entity.getOrganizador() != null ? entity.getOrganizador().getId() : null,
                entity.getOrganizador() != null ? entity.getOrganizador().getNombre() : null,
                criterios.stream().map(CriterioDesempateResponse::desdeEntity).toList()
        );
    }
}
