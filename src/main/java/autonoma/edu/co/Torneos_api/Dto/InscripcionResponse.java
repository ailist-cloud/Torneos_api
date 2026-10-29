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
    public static InscripcionResponse desdeEntity(InscripcionEntity entity) {
        return new InscripcionResponse(
                entity.getTorneoentity().getId(),
                entity.getTorneoentity().getNombre(),
                entity.getEquipoentity().getId(),
                entity.getEquipoentity().getNombre(),
                entity.getFechaInscripcion()
        );
    }
}
