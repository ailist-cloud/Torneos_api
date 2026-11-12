package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.HistorialCambioEntity;

import java.time.LocalDateTime;

/**
 * Una entrada de la trazabilidad: qué acción se hizo, sobre qué partido o torneo, quién y cuándo.
 *
 * @param id Identificador único (autoincremental).
 * @param entidad Tipo de registro afectado: PARTIDO o TORNEO.
 * @param entidadId Id del partido o torneo afectado.
 * @param accion Acción realizada (por ejemplo RESULTADO_CORREGIDO).
 * @param detalle Detalle del cambio (por ejemplo "1-1 -> 2-1").
 * @param usuarioId Id del usuario que hizo el cambio.
 * @param usuarioNombre Nombre del usuario que hizo el cambio.
 * @param fechaHora Fecha y hora del cambio.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record HistorialResponse(
        Long id,
        String entidad,
        Long entidadId,
        String accion,
        String detalle,
        Long usuarioId,
        String usuarioNombre,
        LocalDateTime fechaHora
) {
    /**
     * Desde entity.
     *
     * @param h h
     * @return el historial
     */
    public static HistorialResponse desdeEntity(HistorialCambioEntity h) {
        Long usuarioId = null;
        String usuarioNombre = null;
        if (h.getUsuario() != null) {
            usuarioId = h.getUsuario().getId();
            usuarioNombre = h.getUsuario().getNombre();
        }
        return new HistorialResponse(h.getId(), h.getEntidad(), h.getEntidadId(), h.getAccion(),
                h.getDetalle(), usuarioId, usuarioNombre, h.getFechaHora());
    }
}
