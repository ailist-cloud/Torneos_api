package autonoma.edu.co.Torneos_api;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import autonoma.edu.co.Torneos_api.entity.UsuarioEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Fábrica de objetos de prueba para no repetir el mismo código en cada test. */
public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static UsuarioEntity usuario(Long id, String nombre, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(nombre.toLowerCase().replace(" ", "") + "@torneos.co");
        usuario.setRol(rol);
        return usuario;
    }

    /** Laura: organizadora del torneo de prueba. */
    public static UsuarioEntity organizadora() {
        return usuario(1L, "Laura", RolUsuario.ORGANIZADOR);
    }

    /** Torneo 3/1/0 que va de 2026 a 2030, para que cualquier fecha de prueba quede dentro. */
    public static TorneoEntity torneo(EstadoTorneo estado) {
        TorneoEntity torneo = new TorneoEntity();
        torneo.setId(1L);
        torneo.setNombre("Copa Prueba");
        torneo.setDeporte("Fútbol");
        torneo.setFechaInicio(LocalDate.of(2026, 1, 1));
        torneo.setFechaFin(LocalDate.of(2030, 12, 31));
        torneo.setEstado(estado);
        torneo.setPuntosVictoria(3);
        torneo.setPuntosEmpate(1);
        torneo.setPuntosDerrota(0);
        torneo.setOrganizador(organizadora());
        return torneo;
    }

    public static EquipoEntity equipo(Long id, String nombre) {
        EquipoEntity equipo = new EquipoEntity();
        equipo.setId(id);
        equipo.setNombre(nombre);
        equipo.setCiudad("Manizales");
        return equipo;
    }

    public static PartidoEntity partido(Long id, TorneoEntity torneo, EquipoEntity local, EquipoEntity visitante,
                                        LocalDateTime inicio, EstadoPartido estado) {
        PartidoEntity partido = new PartidoEntity();
        partido.setId(id);
        partido.setTorneo(torneo);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setFechaHoraInicio(inicio);
        partido.setDuracionMin(90);
        partido.setEstado(estado);
        return partido;
    }

    /** Partido FINALIZADO con marcador, para la tabla de posiciones. */
    public static PartidoEntity finalizado(TorneoEntity torneo, EquipoEntity local, EquipoEntity visitante,
                                           int golesLocal, int golesVisitante) {
        PartidoEntity partido = partido(null, torneo, local, visitante,
                LocalDateTime.of(2026, 11, 2, 10, 0), EstadoPartido.FINALIZADO);
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        return partido;
    }
}
