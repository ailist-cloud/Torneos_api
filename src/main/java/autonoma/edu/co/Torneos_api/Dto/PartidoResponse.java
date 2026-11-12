package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;

import java.time.LocalDateTime;

/**
 * Respuesta con los datos de un partido, incluida la hora de fin calculada (inicio + duración).
 *
 * @param id Identificador único (autoincremental).
 * @param torneoId Id del torneo.
 * @param torneoNombre Nombre del torneo.
 * @param equipoLocalId Id del equipo local.
 * @param equipoLocal Equipo local.
 * @param equipoVisitanteId Id del equipo visitante.
 * @param equipoVisitante Equipo visitante.
 * @param fechaHoraInicio Fecha y hora de inicio (yyyy-MM-ddTHH:mm:ss).
 * @param fechaHoraFin Fecha y hora de fin (inicio + duración).
 * @param duracionMin Duración del partido en minutos.
 * @param estado Estado actual.
 * @param golesLocal Goles del equipo local (no negativos, RN-03).
 * @param golesVisitante Goles del equipo visitante (no negativos, RN-03).
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record PartidoResponse(
        Long id,
        Long torneoId,
        String torneoNombre,
        Long equipoLocalId,
        String equipoLocal,
        Long equipoVisitanteId,
        String equipoVisitante,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        Integer duracionMin,
        EstadoPartido estado,
        Integer golesLocal,
        Integer golesVisitante
) {
    /**
     * Desde entity.
     *
     * @param partido partido
     * @return los datos del partido
     */
    public static PartidoResponse desdeEntity(PartidoEntity partido) {
        return new PartidoResponse(
                partido.getId(),
                partido.getTorneo().getId(),
                partido.getTorneo().getNombre(),
                partido.getEquipoLocal().getId(),
                partido.getEquipoLocal().getNombre(),
                partido.getEquipoVisitante().getId(),
                partido.getEquipoVisitante().getNombre(),
                partido.getFechaHoraInicio(),
                partido.calcularFechaHoraFin(),
                partido.getDuracionMin(),
                partido.getEstado(),
                partido.getGolesLocal(),
                partido.getGolesVisitante()
        );
    }
}
