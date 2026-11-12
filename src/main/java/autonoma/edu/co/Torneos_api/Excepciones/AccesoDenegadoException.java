package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/**
 * 403: se sabe quién es, pero no tiene permiso para esta operación.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public class AccesoDenegadoException extends ApiException {

    public AccesoDenegadoException(String mensaje) {
        super(HttpStatus.FORBIDDEN, mensaje);
    }
}
