package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/** 409: la operación choca con datos que ya existen (duplicados, cruces de horario). */
public class ConflictoException extends ApiException {

    public ConflictoException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
