package autonoma.edu.co.Torneos_api.Dto;

public record CrearUsuarioRequest(
        String nombre,
        String email,
        String rol
) { }
