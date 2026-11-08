package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/** El organizador NO va en el body: es el usuario del header X-Usuario-Id. */
public record CrearTorneoRequest(
        @NotBlank(message = "es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "es obligatorio") @Size(max = 50) String deporte,
        @NotNull(message = "es obligatoria (yyyy-MM-dd)") LocalDate fechaInicio,
        @NotNull(message = "es obligatoria (yyyy-MM-dd)") LocalDate fechaFin,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "no puede ser negativo") Integer puntosVictoria,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "no puede ser negativo") Integer puntosEmpate,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "no puede ser negativo") Integer puntosDerrota,
        @NotEmpty(message = "RN-05: debe tener al menos un criterio") List<CriterioDesempate> criteriosDesempate
) { }
