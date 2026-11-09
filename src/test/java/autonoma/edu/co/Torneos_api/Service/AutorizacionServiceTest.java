package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.DatosPrueba;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.Excepciones.AccesoDenegadoException;
import autonoma.edu.co.Torneos_api.Excepciones.NoAutenticadoException;
import autonoma.edu.co.Torneos_api.Repository.UsuarioRepository;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/** Autorización con el header X-Usuario-Id: 401 si no se sabe quién es, 403 si no tiene permiso. */
@ExtendWith(MockitoExtension.class)
class AutorizacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutorizacionService autorizacionService;

    @Test
    @DisplayName("401: sin header (usuarioId null)")
    void sinHeader() {
        assertThrows(NoAutenticadoException.class, () -> autorizacionService.identificar(null));
    }

    @Test
    @DisplayName("401: el usuario del header no existe")
    void usuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoAutenticadoException.class, () -> autorizacionService.identificar(99L));
    }

    @Test
    @DisplayName("403: un REPRESENTANTE no puede hacer operaciones de organizador")
    void representanteNoEsOrganizador() {
        UsuarioEntity pedro = DatosPrueba.usuario(2L, "Pedro", RolUsuario.REPRESENTANTE);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(pedro));

        assertThrows(AccesoDenegadoException.class, () -> autorizacionService.exigirOrganizador(2L));
    }

    @Test
    @DisplayName("403: un ORGANIZADOR no puede modificar el torneo de otro organizador")
    void organizadorDeOtroTorneo() {
        UsuarioEntity otro = DatosPrueba.usuario(9L, "Otro Organizador", RolUsuario.ORGANIZADOR);
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(otro));
        TorneoEntity torneoDeLaura = DatosPrueba.torneo(EstadoTorneo.ABIERTO);

        assertThrows(AccesoDenegadoException.class,
                () -> autorizacionService.exigirOrganizadorDelTorneo(9L, torneoDeLaura));
    }

    @Test
    @DisplayName("El organizador dueño del torneo sí tiene permiso")
    void organizadorDelTorneoTienePermiso() {
        UsuarioEntity laura = DatosPrueba.organizadora();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(laura));
        TorneoEntity torneo = DatosPrueba.torneo(EstadoTorneo.ABIERTO);

        UsuarioEntity autorizado = autorizacionService.exigirOrganizadorDelTorneo(1L, torneo);

        assertEquals("Laura", autorizado.getNombre());
    }
}
