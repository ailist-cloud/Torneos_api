package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartidoRepository extends JpaRepository<PartidoEntity, Long> {

    List<PartidoEntity> findByTorneoentity_IdAndEstado(Long torneoId, String estado);

    @Query("""
            select p from PartidoEntity p
            where (p.equipoEntityLocal.id = :equipoId or p.equipoEntityVisitante.id = :equipoId)
              and p.estado <> 'CANCELADO'
            """)
    List<PartidoEntity> findActivosDelEquipo(@Param("equipoId") Long equipoId);
}
