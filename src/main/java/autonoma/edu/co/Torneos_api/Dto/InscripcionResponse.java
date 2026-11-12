package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;

import java.time.LocalDate;

/**
 * Respuesta con una inscripción: qué equipo está en qué torneo y desde cuándo.
 *
 * @param torneoId Id del torneo.
 * @param torneoNombre Nombre del torneo.
 * @param equipoId Id del equipo.
 * @param equipoNombre Nombre del equipo.
 * @param fechaInscripcion Fecha en que se inscribió el equipo.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record InscripcionResponse(
        Long torneoId,
        String torneoNombre,
        Long equipoId,
        String equipoNombre,
        LocalDate fechaInscripcion
) {
    /**
     * Desde entity.
     *
     * @param inscripcion inscripcion
     * @return los datos de la inscripción
     */
    public static InscripcionResponse desdeEntity(InscripcionEntity inscripcion) {
        return new InscripcionResponse(
                inscripcion.getTorneo().getId(),
                inscripcion.getTorneo().getNombre(),
                inscripcion.getEquipo().getId(),
                inscripcion.getEquipo().getNombre(),
                inscripcion.getFechaInscripcion()
        );
    }
}
