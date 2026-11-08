package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/** 401: no se sabe quién hace la petición (falta el header X-Usuario-Id o el usuario no existe). */
public class NoAutenticadoException extends ApiException {

    public NoAutenticadoException(String mensaje) {
        super(HttpStatus.UNAUTHORIZED, mensaje);
    }
}
