package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/** 422: los datos son válidos, pero una regla del dominio (RN-xx) no permite la operación. */
public class ReglaNegocioException extends ApiException {

    public ReglaNegocioException(String mensaje) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);
    }
}
