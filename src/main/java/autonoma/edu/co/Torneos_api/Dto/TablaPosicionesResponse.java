package autonoma.edu.co.Torneos_api.Dto;

import java.util.List;

public record TablaPosicionesResponse(
        Long torneoId,
        String torneoNombre,
        List<String> criteriosDesempate,
        List<PosicionResponse> posiciones
) { }
