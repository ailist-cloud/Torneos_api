package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Dto.CrearPartidoRequest;
import autonoma.edu.co.Torneos_api.Dto.PartidoResponse;
import autonoma.edu.co.Torneos_api.Dto.RegistrarResultadoRequest;
import autonoma.edu.co.Torneos_api.Repository.EquipoRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;

    public PartidoService(PartidoRepository partidoRepository,
                          TorneoRepository torneoRepository,
                          EquipoRepository equipoRepository) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
    }


    public List<PartidoResponse> consultarProgramacion() {
        return partidoRepository.findAll()
                .stream()
                .map(PartidoResponse::desdeEntity)
                .toList();
    }

    public PartidoResponse programarPartido(CrearPartidoRequest request) {
        TorneoEntity torneo = torneoRepository.findById(request.torneoId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        EquipoEntity local = equipoRepository.findById(request.equipoLocalId())
                .orElseThrow(() -> new RuntimeException("Equipo local no encontrado"));
        EquipoEntity visitante = equipoRepository.findById(request.equipoVisitanteId())
                .orElseThrow(() -> new RuntimeException("Equipo visitante no encontrado"));

        PartidoEntity partido = new PartidoEntity();
        partido.setTorneoentity(torneo);
        partido.setEquipoEntityLocal(local);
        partido.setEquipoEntityVisitante(visitante);
        partido.setFechaHoraInicio(request.fechaHoraInicio());
        partido.setDuracionMin(request.duracionMin());
        partido.setEstado("PROGRAMADO");

        PartidoEntity guardado = partidoRepository.save(partido);
        return PartidoResponse.desdeEntity(guardado);
    }


    public PartidoResponse registrarResultado(Long id, RegistrarResultadoRequest request) {
        PartidoEntity partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        partido.setGolesLocal(request.golesLocal());
        partido.setGolesVisitante(request.golesVisitante());
        partido.setEstado("FINALIZADO");

        PartidoEntity actualizado = partidoRepository.save(partido);
        return PartidoResponse.desdeEntity(actualizado);
    }


    public void eliminarPartido(Long id) {
        if (!partidoRepository.existsById(id)) {
            throw new RuntimeException("El partido con ID " + id + " no existe");
        }
        partidoRepository.deleteById(id);
    }
}