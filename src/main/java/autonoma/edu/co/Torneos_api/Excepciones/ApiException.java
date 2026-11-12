package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/**
 * Excepción base de la API. Cada subclase define qué código HTTP se responde.
 * El ManejadorErrores la atrapa y la convierte en un ErrorResponse.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    /**
     * Get status.
     *
     * @return el resultado
     */
    public HttpStatus getStatus() {
        return status;
    }
}
