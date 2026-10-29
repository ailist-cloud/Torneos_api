package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String mensaje,
        String ruta
) { }
