package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta con la configuración completa de un torneo y sus criterios de desempate.
 *
 * @param id Identificador único (autoincremental).
 * @param nombre Nombre.
 * @param deporte Deporte del torneo.
 * @param fechaInicio Fecha de inicio (yyyy-MM-dd).
 * @param fechaFin Fecha de fin (yyyy-MM-dd).
 * @param estado Estado actual.
 * @param puntosVictoria Puntos que da una victoria.
 * @param puntosEmpate Puntos que da un empate.
 * @param puntosDerrota Puntos que da una derrota.
 * @param organizadorId Id del organizador del torneo.
 * @param organizadorNombre Nombre del organizador.
 * @param criteriosDesempate Criterios de desempate en el orden en que se aplican (RN-05).
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
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
    /**
     * Desde entity.
     *
     * @param torneo torneo sobre el que se opera
     * @param criterios criterios de desempate
     * @return los datos del torneo con sus criterios
     */
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
