package autonoma.edu.co.Torneos_api.Config;

import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import org.springframework.boot.CommandLineRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final TorneoRepository torneoRepository;
    private final PartidoRepository partidoRepository;

    @Override
    public void run(String... args) {
        System.out.println("Torneos: " + torneoRepository.count());
        System.out.println("Partidos: " + partidoRepository.count());
    }
}