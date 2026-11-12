package autonoma.edu.co.Torneos_api.Domain;

/**
 * Roles de los usuarios del sistema (punto 3 del enunciado):
 * ORGANIZADOR crea torneos, equipos y calendario; REPRESENTANTE representa a un equipo; PUBLICO solo consulta.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public enum RolUsuario {
    ORGANIZADOR,
    REPRESENTANTE,
    PUBLICO
}
