package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/**
 * 409: la operación choca con datos que ya existen (duplicados, cruces de horario).
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public class ConflictoException extends ApiException {

    public ConflictoException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
