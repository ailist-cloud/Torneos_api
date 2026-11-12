package autonoma.edu.co.Torneos_api.Dto;

import jakarta.validation.constraints.NotNull;

/** Cuerpo de {@code POST /api/torneos/{id}/inscripciones}: el equipo a inscribir. */
public record InscribirEquipoRequest(
        @NotNull(message = "es obligatorio") Long equipoId
) { }
