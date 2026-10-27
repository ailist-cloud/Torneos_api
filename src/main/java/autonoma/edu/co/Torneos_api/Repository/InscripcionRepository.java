package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<InscripcionEntity, InscripcionIdEntity> {

    List<InscripcionEntity> findByTorneoentity_Id(Long torneoId);

    boolean existsByTorneoentity_IdAndEquipoentity_Id(Long torneoId, Long equipoId);
}
