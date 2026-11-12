package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Acceso a la tabla {@code partido}, con consultas por torneo, por estado y por equipo.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
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
