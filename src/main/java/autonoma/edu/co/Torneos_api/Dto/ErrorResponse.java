package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDateTime;

/** Formato único de todas las respuestas de error de la API (lo construye {@code ManejadorErrores}). */
public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String mensaje,
        String ruta
) { }
