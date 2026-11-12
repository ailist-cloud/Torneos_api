package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Acceso a la tabla {@code criterio_desempate}.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public interface CriterioDesempateRepository extends JpaRepository<CriterioDesempateEntity, Long> {

    List<CriterioDesempateEntity> findByTorneoIdOrderByOrdenAsc(Long torneoId);

    // Borra de una vez con un DELETE (antes de insertar los nuevos criterios del PUT)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from CriterioDesempateEntity c where c.torneo.id = :torneoId")
    void eliminarPorTorneo(@Param("torneoId") Long torneoId);
}
