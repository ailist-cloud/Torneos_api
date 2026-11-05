package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;

public record CriterioDesempateResponse(
        Integer orden,
        CriterioDesempate criterio
) {
    public static CriterioDesempateResponse desdeEntity(CriterioDesempateEntity entity) {
        return new CriterioDesempateResponse(entity.getOrden(), entity.getCriterio());
    }
}
