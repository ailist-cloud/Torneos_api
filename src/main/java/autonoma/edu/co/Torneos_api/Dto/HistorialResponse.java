package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.entity.HistorialCambioEntity;

import java.time.LocalDateTime;

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
