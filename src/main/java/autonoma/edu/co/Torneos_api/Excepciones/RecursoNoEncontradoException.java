package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/**
 * 404: el registro buscado no existe.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public class RecursoNoEncontradoException extends ApiException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(HttpStatus.NOT_FOUND, "No existe " + recurso + " con id " + id);
    }
}
