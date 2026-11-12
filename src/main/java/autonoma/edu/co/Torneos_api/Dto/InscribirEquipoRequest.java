package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de {@code POST /api/torneos/{id}/inscripciones}: el equipo a inscribir.
 *
 * @param equipoId Id del equipo.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record InscribirEquipoRequest(
        @NotNull(message = "es obligatorio") Long equipoId
) { }
