package autonoma.edu.co.Torneos_api.Dto;

/** Una fila de la tabla de posiciones: partidos jugados, ganados, empatados, perdidos, goles y puntos. */
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
