package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.Dto.CrearEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.EquipoResponse;
import autonoma.edu.co.Torneos_api.Excepciones.ConflictoException;
import autonoma.edu.co.Torneos_api.Excepciones.RecursoNoEncontradoException;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Repository.EquipoRepository;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioService usuarioService;
    private final AutorizacionService autorizacionService;

    public EquipoService(EquipoRepository equipoRepository,
                         UsuarioService usuarioService,
                         AutorizacionService autorizacionService) {
        this.equipoRepository = equipoRepository;
        this.usuarioService = usuarioService;
        this.autorizacionService = autorizacionService;
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        List<EquipoResponse> respuesta = new ArrayList<>();
        for (EquipoEntity equipo : equipoRepository.findAllByOrderByNombreAsc()) {
            respuesta.add(EquipoResponse.desdeEntity(equipo));
        }
        return respuesta;
    }

    @Transactional(readOnly = true)
    public EquipoResponse consultar(Long id) {
        return EquipoResponse.desdeEntity(buscarEntidad(id));
    }

    @Transactional
    public EquipoResponse crear(CrearEquipoRequest request, Long usuarioId) {
        autorizacionService.exigirOrganizador(usuarioId); // el organizador crea los equipos
        String nombre = request.nombre().trim();
        if (equipoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un equipo llamado " + nombre);
        }

        UsuarioEntity representante = usuarioService.buscarEntidad(request.representanteId());
        if (representante.getRol() != RolUsuario.REPRESENTANTE) {
            throw new ReglaNegocioException("El usuario " + representante.getNombre()
                    + " tiene rol " + representante.getRol() + "; se necesita rol REPRESENTANTE");
        }

        EquipoEntity equipo = new EquipoEntity();
        equipo.setNombre(nombre);
        equipo.setCiudad(request.ciudad() == null ? null : request.ciudad().trim());
        equipo.setRepresentante(representante);

        return EquipoResponse.desdeEntity(equipoRepository.save(equipo));
    }

    public EquipoEntity buscarEntidad(Long id) {
        Optional<EquipoEntity> equipo = equipoRepository.findById(id);
        if (equipo.isEmpty()) {
            throw new RecursoNoEncontradoException("equipo", id);
        }
        return equipo.get();
    }
}
