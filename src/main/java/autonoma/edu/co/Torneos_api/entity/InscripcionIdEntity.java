package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InscripcionIdEntity implements Serializable {

    @Column(name = "torneo_id")
    private Long torneoId;

    @Column(name = "equipo_id")
    private Long equipoId;

    public InscripcionIdEntity() {
    }

    public InscripcionIdEntity(Long torneoId, Long equipoId) {
        this.torneoId = torneoId;
        this.equipoId = equipoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InscripcionIdEntity that)) return false;
        return Objects.equals(torneoId, that.torneoId)
                && Objects.equals(equipoId, that.equipoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(torneoId, equipoId);
    }
}