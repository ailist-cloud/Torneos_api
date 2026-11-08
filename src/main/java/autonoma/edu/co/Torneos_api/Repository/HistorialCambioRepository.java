package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.HistorialCambioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialCambioRepository extends JpaRepository<HistorialCambioEntity, Long> {

    List<HistorialCambioEntity> findByEntidadAndEntidadIdOrderByFechaHoraAscIdAsc(String entidad, Long entidadId);
}
