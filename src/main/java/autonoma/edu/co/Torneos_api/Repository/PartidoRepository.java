package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartidoRepository extends JpaRepository<PartidoEntity, Long> {

    List<PartidoEntity> findAllByOrderByFechaHoraInicioAsc();

    List<PartidoEntity> findByTorneoIdOrderByFechaHoraInicioAsc(Long torneoId);

    List<PartidoEntity> findByTorneoIdAndEstado(Long torneoId, EstadoPartido estado);

    boolean existsByTorneoIdAndEstadoIn(Long torneoId, List<EstadoPartido> estados);

    /** Todos los partidos donde el equipo juega, como local o como visitante. */
    @Query("""
            select p from PartidoEntity p
            where p.equipoLocal.id = :equipoId or p.equipoVisitante.id = :equipoId
            order by p.fechaHoraInicio
            """)
    List<PartidoEntity> buscarPorEquipo(@Param("equipoId") Long equipoId);
}
