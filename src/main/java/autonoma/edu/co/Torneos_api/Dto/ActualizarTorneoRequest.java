package autonoma.edu.co.Torneos_api.Dto;

import java.time.LocalDate;
import java.util.List;

public record ActualizarTorneoRequest(
        String nombre,
        String deporte,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer puntosVictoria,
        Integer puntosEmpate,
        Integer puntosDerrota,
        List<String> criteriosDesempate
) { }
