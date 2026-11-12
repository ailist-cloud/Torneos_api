package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDateTime;

/**
 * Formato único de todas las respuestas de error de la API (lo construye {@code ManejadorErrores}).
 *
 * @param timestamp Momento del error.
 * @param status Código HTTP.
 * @param error Nombre del código HTTP.
 * @param mensaje Explicación del error.
 * @param ruta Ruta de la petición que falló.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String mensaje,
        String ruta
) { }
