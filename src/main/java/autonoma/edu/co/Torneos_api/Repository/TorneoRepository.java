package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {
}