package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.Partido;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;

    public PartidoService(PartidoRepository partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    /** Consulta la programación. Todos los filtros son opcionales. */
    @Transactional(readOnly = true)
    public List<PartidoResponse> listar(Long torneoId, Long equipoId, String estado) {
        EstadoPartido estadoFiltro = convertirEstado(estado);

        return partidoRepository.findAll(Sort.by("fechaHoraInicio")).stream()
                .filter(p -> torneoId == null || p.getTorneo().getId().equals(torneoId))
                .filter(p -> equipoId == null
                        || p.getEquipoLocal().getId().equals(equipoId)
                        || p.getEquipoVisitante().getId().equals(equipoId))
                .filter(p -> estadoFiltro == null || p.getEstado() == estadoFiltro)
                .map(this::aRespuesta)
                .toList();
    }

    private EstadoPartido convertirEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        try {
            return EstadoPartido.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de partido inválido: " + estado);
        }
    }

    private PartidoResponse aRespuesta(Partido p) {
        return new PartidoResponse(
                p.getId(),
                p.getTorneo().getId(),
                p.getEquipoLocal().getId(),
                p.getEquipoLocal().getNombre(),
                p.getEquipoVisitante().getId(),
                p.getEquipoVisitante().getNombre(),
                p.getFechaHoraInicio(),
                p.getDuracionMin(),
                p.getEstado().name(),
                p.getGolesLocal(),
                p.getGolesVisitante());
    }
}
