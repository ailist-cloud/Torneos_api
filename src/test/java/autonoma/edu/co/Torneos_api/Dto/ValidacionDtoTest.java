package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba las anotaciones de validación de los DTO (@Min, @NotNull, @NotEmpty...). */
class ValidacionDtoTest {

    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("RN-03: los goles no pueden ser negativos")
    void golesNegativos() {
        Set<ConstraintViolation<RegistrarResultadoRequest>> errores =
                validator.validate(new RegistrarResultadoRequest(-1, 0));

        assertEquals(1, errores.size());
        assertEquals("golesLocal", errores.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Un resultado con goles en cero es válido")
    void golesEnCeroSonValidos() {
        assertTrue(validator.validate(new RegistrarResultadoRequest(0, 0)).isEmpty());
    }

    @Test
    @DisplayName("RN-03: los puntos del torneo no pueden ser negativos")
    void puntosNegativos() {
        CrearTorneoRequest request = new CrearTorneoRequest("Copa", "Fútbol",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 3, 1, -1,
                List.of(CriterioDesempate.DIFERENCIA_GOLES));

        Set<ConstraintViolation<CrearTorneoRequest>> errores = validator.validate(request);

        assertEquals(1, errores.size());
        assertEquals("puntosDerrota", errores.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("RN-05: el torneo debe tener al menos un criterio de desempate")
    void sinCriterios() {
        CrearTorneoRequest request = new CrearTorneoRequest("Copa", "Fútbol",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 3, 1, 0, List.of());

        Set<ConstraintViolation<CrearTorneoRequest>> errores = validator.validate(request);

        assertEquals(1, errores.size());
        assertEquals("criteriosDesempate", errores.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("La duración de un partido debe ser mayor que cero")
    void duracionCero() {
        CrearPartidoRequest request = new CrearPartidoRequest(1L, 1L, 2L, LocalDateTime.of(2026, 12, 5, 10, 0), 0);

        assertEquals(1, validator.validate(request).size());
    }
}
