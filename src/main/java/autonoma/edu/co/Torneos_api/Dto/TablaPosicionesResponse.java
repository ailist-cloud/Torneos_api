package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;

import java.util.List;

public record TablaPosicionesResponse(
        Long torneoId,
        String torneoNombre,
        List<CriterioDesempate> criteriosDesempate,
        List<PosicionResponse> posiciones
) { }
