package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;

import java.time.LocalDate;

public record InscripcionResponse(
        Long torneoId,
        String torneoNombre,
        Long equipoId,
        String equipoNombre,
        LocalDate fechaInscripcion
) {
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
