package autonoma.edu.co.Torneos_api.Dto;

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
