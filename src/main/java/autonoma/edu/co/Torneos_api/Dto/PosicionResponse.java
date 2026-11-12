package autonoma.edu.co.Torneos_api.Dto;

/**
 * Una fila de la tabla de posiciones: partidos jugados, ganados, empatados, perdidos, goles y puntos.
 *
 * @param posicion Posición en la tabla (1 = primero).
 * @param equipoId Id del equipo.
 * @param equipo Equipo.
 * @param partidosJugados Partidos jugados.
 * @param ganados Partidos ganados.
 * @param empatados Partidos empatados.
 * @param perdidos Partidos perdidos.
 * @param golesAFavor Goles a favor.
 * @param golesEnContra Goles en contra.
 * @param diferenciaGoles Goles a favor menos goles en contra.
 * @param puntos Puntos acumulados.
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public record PosicionResponse(
        Integer posicion,
        Long equipoId,
        String equipo,
        Integer partidosJugados,
        Integer ganados,
        Integer empatados,
        Integer perdidos,
        Integer golesAFavor,
        Integer golesEnContra,
        Integer diferenciaGoles,
        Integer puntos
) { }
