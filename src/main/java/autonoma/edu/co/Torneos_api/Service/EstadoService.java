package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Dto.EstadoResponse;
import org.springframework.stereotype.Service;

@Service
public class EstadoService {

    public EstadoResponse consultarEstado() {
        return new EstadoResponse(
                "torneos-api",
                "disponible"
        );
    }
}