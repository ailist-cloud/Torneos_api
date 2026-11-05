package autonoma.edu.co.Torneos_api.Excepciones;

import org.springframework.http.HttpStatus;

/** 400: los datos enviados están mal formados o son incoherentes. */
public class SolicitudInvalidaException extends ApiException {

    public SolicitudInvalidaException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, mensaje);
    }
}
