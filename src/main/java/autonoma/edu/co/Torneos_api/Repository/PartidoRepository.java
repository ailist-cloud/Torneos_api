package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
}