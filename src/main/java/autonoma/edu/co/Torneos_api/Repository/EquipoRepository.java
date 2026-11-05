package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipoRepository extends JpaRepository<EquipoEntity, Long> {

    List<EquipoEntity> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);
}
