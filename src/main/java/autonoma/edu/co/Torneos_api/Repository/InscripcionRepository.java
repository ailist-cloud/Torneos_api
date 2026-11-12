package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla {@code inscripcion} (llave compuesta torneo_id + equipo_id).
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public interface InscripcionRepository extends JpaRepository<InscripcionEntity, InscripcionIdEntity> {

    List<InscripcionEntity> findByTorneoIdOrderByFechaInscripcionAsc(Long torneoId);

    boolean existsByTorneoIdAndEquipoId(Long torneoId, Long equipoId);

    long countByTorneoId(Long torneoId);
}
