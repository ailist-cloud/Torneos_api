package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;

import java.util.List;

/**
 * Respuesta de {@code GET /api/torneos/{id}/tabla}: criterios usados y posiciones ordenadas.
 *
 * @param torneoId Id del torneo.
 * @param torneoNombre Nombre del torneo.
 * @param criteriosDesempate Criterios de desempate en el orden en que se aplican (RN-05).
 * @param posiciones Filas de la tabla, ya ordenadas.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record TablaPosicionesResponse(
        Long torneoId,
        String torneoNombre,
        List<CriterioDesempate> criteriosDesempate,
        List<PosicionResponse> posiciones
) { }
