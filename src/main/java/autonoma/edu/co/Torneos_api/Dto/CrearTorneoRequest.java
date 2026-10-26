package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDate;
import java.util.List;

public record CrearTorneoRequest(
        String nombre,
        String deporte,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer puntosVictoria,
        Integer puntosEmpate,
        Integer puntosDerrota,
        Long organizadorId,
        List<String> criteriosDesempate
) { }
