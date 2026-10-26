package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.Dto.CrearEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.EquipoResponse;
import autonoma.edu.co.Torneos_api.Repository.EquipoRepository;
import autonoma.edu.co.Torneos_api.Repository.UsuarioRepository;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    public EquipoService(EquipoRepository equipoRepository,
                         UsuarioRepository usuarioRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // 1. Listar equipos
    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        return equipoRepository.findAll(Sort.by("nombre"))
                .stream()
                .map(EquipoResponse::desdeEntity)
                .toList();
    }

    // 2. Consultar un equipo
    @Transactional(readOnly = true)
    public EquipoResponse consultar(Long id) {
        EquipoEntity equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El equipo " + id + " no existe"));
        return EquipoResponse.desdeEntity(equipo);
    }

    // 3. Crear equipo (nombre único y representante con rol REPRESENTANTE)
    @Transactional
    public EquipoResponse crear(CrearEquipoRequest request) {
        if (request == null || request.nombre() == null || request.nombre().isBlank()
                || request.representanteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre y representanteId son obligatorios");
        }
        String nombre = request.nombre().trim();
        if (equipoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un equipo llamado " + nombre);
        }

        UsuarioEntity representante = usuarioRepository.findById(request.representanteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El usuario " + request.representanteId() + " no existe"));
        if (!RolUsuario.REPRESENTANTE.name().equalsIgnoreCase(representante.getRol())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El usuario " + representante.getNombre() + " no tiene rol REPRESENTANTE");
        }

        EquipoEntity equipo = new EquipoEntity();
        equipo.setNombre(nombre);
        equipo.setCiudad(request.ciudad() != null ? request.ciudad().trim() : null);
        equipo.setRepresentante(representante);

        EquipoEntity guardado = equipoRepository.save(equipo);
        return EquipoResponse.desdeEntity(guardado);
    }
}
