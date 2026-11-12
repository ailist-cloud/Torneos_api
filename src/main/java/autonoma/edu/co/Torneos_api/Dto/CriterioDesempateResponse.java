package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;

/**
 * Un criterio de desempate de un torneo con su orden de aplicación.
 *
 * @param orden Orden en que se aplica el criterio (1 = primero).
 * @param criterio Criterio de desempate.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record CriterioDesempateResponse(
        Integer orden,
        CriterioDesempate criterio
) {
    /**
     * Desde entity.
     *
     * @param entity entity
     * @return el resultado
     */
    public static CriterioDesempateResponse desdeEntity(CriterioDesempateEntity entity) {
        return new CriterioDesempateResponse(entity.getOrden(), entity.getCriterio());
    }
}
