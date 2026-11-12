package autonoma.edu.co.Torneos_api.Domain;

/**
 * Roles de los usuarios del sistema (punto 3 del enunciado):
 * ORGANIZADOR crea torneos, equipos y calendario; REPRESENTANTE representa a un equipo; PUBLICO solo consulta.
 */
public enum RolUsuario {
    ORGANIZADOR,
    REPRESENTANTE,
    PUBLICO
}
