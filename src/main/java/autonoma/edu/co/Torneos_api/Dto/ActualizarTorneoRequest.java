package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * PUT reemplaza toda la configuración, por eso todos los campos son obligatorios.
 *
 * @param nombre Nombre.
 * @param deporte Deporte del torneo.
 * @param fechaInicio Fecha de inicio (yyyy-MM-dd).
 * @param fechaFin Fecha de fin (yyyy-MM-dd).
 * @param puntosVictoria Puntos que da una victoria.
 * @param puntosEmpate Puntos que da un empate.
 * @param puntosDerrota Puntos que da una derrota.
 * @param criteriosDesempate Criterios de desempate en el orden en que se aplican (RN-05).
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record ActualizarTorneoRequest(
        @NotBlank(message = "es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "es obligatorio") @Size(max = 50) String deporte,
        @NotNull(message = "es obligatoria (yyyy-MM-dd)") LocalDate fechaInicio,
        @NotNull(message = "es obligatoria (yyyy-MM-dd)") LocalDate fechaFin,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "no puede ser negativo") Integer puntosVictoria,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "no puede ser negativo") Integer puntosEmpate,
        @NotNull(message = "es obligatorio") @Min(value = 0, message = "no puede ser negativo") Integer puntosDerrota,
        @NotEmpty(message = "RN-05: debe tener al menos un criterio") List<CriterioDesempate> criteriosDesempate
) { }
