package autonoma.edu.co.Torneos_api.Config;

import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import org.springframework.boot.CommandLineRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Al arrancar la aplicación imprime cuántos torneos y partidos hay en la base de datos.
 * Sirve para comprobar rápidamente que la conexión con PostgreSQL funciona.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@RequiredArgsConstructor
@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final TorneoRepository torneoRepository;
    private final PartidoRepository partidoRepository;

    /**
     * Run.
     *
     * @param args argumentos de la línea de comandos
     */
    @Override
    public void run(String... args) {
        System.out.println("Torneos: " + torneoRepository.count());
        System.out.println("Partidos: " + partidoRepository.count());
    }
}