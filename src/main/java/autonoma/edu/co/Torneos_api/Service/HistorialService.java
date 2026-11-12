package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Dto.HistorialResponse;
import autonoma.edu.co.Torneos_api.Repository.HistorialCambioRepository;
import autonoma.edu.co.Torneos_api.entity.HistorialCambioEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import java.util.List;

/**
 * Guarda y consulta la trazabilidad de partidos y torneos.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@Service
public class HistorialService {

    public static final String PARTIDO = "PARTIDO";
    public static final String TORNEO = "TORNEO";

    private final HistorialCambioRepository historialRepository;

    /**
     * Se llama dentro de la misma transacción del cambio: si el cambio falla, tampoco queda el registro.
     *
     * @param entidad tipo de registro: PARTIDO o TORNEO
     * @param entidadId id del partido o torneo
     * @param accion acción realizada
     * @param detalle detalle del cambio
     * @param usuario usuario que hace el cambio
     */
    @Transactional
    public void registrar(String entidad, Long entidadId, String accion, String detalle, UsuarioEntity usuario) {
        HistorialCambioEntity h = new HistorialCambioEntity();
        h.setEntidad(entidad);
        h.setEntidadId(entidadId);
        h.setAccion(accion);
        h.setDetalle(detalle);
        h.setUsuario(usuario);
        h.setFechaHora(LocalDateTime.now());
        historialRepository.save(h);
    }

    /**
     * Consultar.
     *
     * @param entidad tipo de registro: PARTIDO o TORNEO
     * @param entidadId id del partido o torneo
     * @return lista con los cambios registrados, del más antiguo al más reciente
     */
    @Transactional(readOnly = true)
    public List<HistorialResponse> consultar(String entidad, Long entidadId) {
        List<HistorialResponse> respuesta = new ArrayList<>();
        for (HistorialCambioEntity h : historialRepository.findByEntidadAndEntidadIdOrderByFechaHoraAscIdAsc(entidad, entidadId)) {
            respuesta.add(HistorialResponse.desdeEntity(h));
        }
        return respuesta;
    }
}
