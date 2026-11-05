package autonoma.edu.co.Torneos_api.Config;

import autonoma.edu.co.Torneos_api.Dto.ErrorResponse;
import autonoma.edu.co.Torneos_api.Excepciones.ApiException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Convierte cualquier error en un JSON con el mismo formato (ErrorResponse),
 * para que Postman nunca reciba un 500 sin explicación.
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    /** Nuestras excepciones: 400, 404, 409 y 422. */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> manejarApiException(ApiException ex, HttpServletRequest request) {
        return construir(ex.getStatus(), ex.getMessage(), request);
    }

    /** Falla una anotación del DTO (@NotNull, @NotBlank, @Min...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder mensaje = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if (mensaje.length() > 0) {
                mensaje.append("; ");
            }
            mensaje.append(error.getField()).append(": ").append(error.getDefaultMessage());
        }
        return construir(HttpStatus.BAD_REQUEST, mensaje.toString(), request);
    }

    /** JSON mal escrito, fecha con formato incorrecto o valor de enum que no existe. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String mensaje = "El cuerpo no es un JSON válido. Fechas: yyyy-MM-dd, fechas con hora: yyyy-MM-ddTHH:mm:ss";

        if (ex.getCause() instanceof InvalidFormatException formato && formato.getTargetType().isEnum()) {
            String campo = formato.getPath().isEmpty() ? "?" : formato.getPath().get(formato.getPath().size() - 1).getFieldName();
            mensaje = "Valor '" + formato.getValue() + "' inválido para '" + campo + "'. Valores permitidos: "
                    + Arrays.toString(formato.getTargetType().getEnumConstants());
        }
        return construir(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    /** Ej: GET /api/torneos/abc (se esperaba un número). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarParametroInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue(), request);
    }

    /** La URL no existe (ruta mal escrita en Postman). */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRutaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "La ruta " + request.getRequestURI() + " no existe", request);
    }

    /** La URL existe pero con otro verbo (ej: PUT donde solo hay GET). */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarVerboInvalido(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED,
                "El verbo " + ex.getMethod() + " no está permitido en esta ruta. Permitidos: " + ex.getSupportedHttpMethods(),
                request);
    }

    /** La base de datos rechazó la operación (FK, UNIQUE, NOT NULL, CHECK). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarRestriccionBd(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Restricción de BD violada", ex);
        return construir(HttpStatus.CONFLICT,
                "La base de datos rechazó la operación: " + ex.getMostSpecificCause().getMessage(), request);
    }

    /** Cualquier otro error inesperado: se registra en consola para poder depurarlo. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperado(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en " + request.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje, HttpServletRequest request) {
        ErrorResponse cuerpo = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI());
        return ResponseEntity.status(status).body(cuerpo);
    }
}
