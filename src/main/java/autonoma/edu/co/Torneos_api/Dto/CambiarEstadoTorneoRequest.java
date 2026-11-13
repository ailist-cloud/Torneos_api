package autonoma.edu.co.Torneos_api.Dto;

public record CambiarEstadoTorneoRequest(
        String estado       // ABIERTO | CERRADO | EN_CURSO | FINALIZADO
) { }
