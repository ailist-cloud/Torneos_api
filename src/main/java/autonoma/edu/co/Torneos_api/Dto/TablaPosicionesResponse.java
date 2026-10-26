package autonoma.edu.co.Torneos_api.Dto;

import java.util.List;

public record TablaPosicionesResponse(
        Long torneoId,
        String torneoNombre,
        List<String> criteriosDesempate,   // los que se aplicaron, en orden (RN-05)
        List<PosicionResponse> posiciones
) { }
