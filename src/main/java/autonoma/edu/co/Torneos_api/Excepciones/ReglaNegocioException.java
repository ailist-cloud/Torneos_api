package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/**
 * 422: los datos son válidos, pero una regla del dominio (RN-xx) no permite la operación.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public class ReglaNegocioException extends ApiException {

    public ReglaNegocioException(String mensaje) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);
    }
}
