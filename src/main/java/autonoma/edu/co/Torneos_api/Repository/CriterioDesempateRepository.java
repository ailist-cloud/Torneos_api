package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CriterioDesempateRepository extends JpaRepository<CriterioDesempateEntity, Long> {

    List<CriterioDesempateEntity> findByTorneoentity_IdOrderByOrdenAsc(Long torneoId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from CriterioDesempateEntity c where c.torneoentity.id = :torneoId")
    void eliminarPorTorneo(@Param("torneoId") Long torneoId);
}
