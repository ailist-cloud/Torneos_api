package autonoma.edu.co.Torneos_api.Dto;

public record CrearEquipoRequest(
        String nombre,
        String ciudad,
        Long representanteId
) { }
