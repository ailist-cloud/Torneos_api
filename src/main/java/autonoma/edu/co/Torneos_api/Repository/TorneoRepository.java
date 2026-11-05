package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TorneoRepository extends JpaRepository<TorneoEntity, Long> {

    List<TorneoEntity> findAllByOrderByFechaInicioAsc();
}
