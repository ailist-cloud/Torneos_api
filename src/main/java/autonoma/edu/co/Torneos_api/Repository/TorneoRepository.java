package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorneoRepository extends JpaRepository<TorneoEntity, Long> {
}