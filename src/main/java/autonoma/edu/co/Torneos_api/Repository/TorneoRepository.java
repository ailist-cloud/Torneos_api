package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a la tabla {@code torneo}. */
public interface TorneoRepository extends JpaRepository<TorneoEntity, Long> {

    List<TorneoEntity> findAllByOrderByFechaInicioAsc();
}
