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
import lombok.RequiredArgsConstructor;
import java.util.Optional;

/**
 * Lógica de equipos: consulta y creación. Solo un ORGANIZADOR crea equipos
 * y el representante debe tener rol REPRESENTANTE.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioService usuarioService;
    private final AutorizacionService autorizacionService;

    /**
     * Lista todos los equipos ordenados por nombre.
     *
     * @return lista con los equipos
     */
    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        List<EquipoResponse> respuesta = new ArrayList<>();
        for (EquipoEntity equipo : equipoRepository.findAllByOrderByNombreAsc()) {
            respuesta.add(EquipoResponse.desdeEntity(equipo));
        }
        return respuesta;
    }

    /**
     * Consulta un equipo; lanza 404 si no existe.
     *
     * @param id id del equipo
     * @return los datos del equipo
     */
    @Transactional(readOnly = true)
    public EquipoResponse consultar(Long id) {
        return EquipoResponse.desdeEntity(buscarEntidad(id));
    }

    /**
     * Crea un equipo. Exige que quien lo crea sea ORGANIZADOR (401/403),
     * que el nombre no exista (409) y que el representante tenga rol REPRESENTANTE (422).
     *
     * @param request datos enviados en el cuerpo de la petición
     * @param usuarioId id del usuario que hace la operación (header X-Usuario-Id)
     * @return los datos del equipo
     */
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

    /**
     * Lo usan otros servicios para obtener el equipo o responder 404.
     *
     * @param id id del equipo
     * @return el equipo encontrado
     */
    public EquipoEntity buscarEntidad(Long id) {
        Optional<EquipoEntity> equipo = equipoRepository.findById(id);
        if (equipo.isEmpty()) {
            throw new RecursoNoEncontradoException("equipo", id);
        }
        return equipo.get();
    }
}
