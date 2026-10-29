package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class InscripcionIdEntity implements Serializable {

    @Column(name = "torneo_id")
    private Long torneoId;

    @Column(name = "equipo_id")
    private Long equipoId;
}