package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
}