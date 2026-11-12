package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla {@code torneo}.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public interface TorneoRepository extends JpaRepository<TorneoEntity, Long> {

    List<TorneoEntity> findAllByOrderByFechaInicioAsc();
}
