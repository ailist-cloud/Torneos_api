package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.Inscripcion;
import autonoma.edu.co.Torneos_api.Domain.InscripcionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, InscripcionId> {
}