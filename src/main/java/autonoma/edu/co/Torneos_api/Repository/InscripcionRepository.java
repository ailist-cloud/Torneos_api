package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.Inscripcionentity;
import autonoma.edu.co.Torneos_api.entity.InscripcionIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcionentity, InscripcionIdEntity> {
}