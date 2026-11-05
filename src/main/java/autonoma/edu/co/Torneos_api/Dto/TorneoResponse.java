package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record TorneoResponse(
        Long id,
        String nombre,
        String deporte,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoTorneo estado,
        Integer puntosVictoria,
        Integer puntosEmpate,
        Integer puntosDerrota,
        Long organizadorId,
        String organizadorNombre,
        List<CriterioDesempateResponse> criteriosDesempate
) {
    public static TorneoResponse desdeEntity(TorneoEntity torneo, List<CriterioDesempateEntity> criterios) {
        List<CriterioDesempateResponse> criteriosResponse = new ArrayList<>();
        for (CriterioDesempateEntity criterio : criterios) {
            criteriosResponse.add(CriterioDesempateResponse.desdeEntity(criterio));
        }
        return new TorneoResponse(
                torneo.getId(),
                torneo.getNombre(),
                torneo.getDeporte(),
                torneo.getFechaInicio(),
                torneo.getFechaFin(),
                torneo.getEstado(),
                torneo.getPuntosVictoria(),
                torneo.getPuntosEmpate(),
                torneo.getPuntosDerrota(),
                torneo.getOrganizador().getId(),
                torneo.getOrganizador().getNombre(),
                criteriosResponse
        );
    }
}
