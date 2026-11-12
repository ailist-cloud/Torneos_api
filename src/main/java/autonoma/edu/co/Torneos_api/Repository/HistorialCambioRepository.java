package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.HistorialCambioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a la tabla {@code historial_cambio} (trazabilidad). */
public interface HistorialCambioRepository extends JpaRepository<HistorialCambioEntity, Long> {

    List<HistorialCambioEntity> findByEntidadAndEntidadIdOrderByFechaHoraAscIdAsc(String entidad, Long entidadId);
}
