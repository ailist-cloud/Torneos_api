package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/** Llave primaria compuesta de inscripcion: (torneo_id, equipo_id). Necesita equals y hashCode. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class InscripcionIdEntity implements Serializable {

    @Column(name = "torneo_id")
    private Long torneoId;

    @Column(name = "equipo_id")
    private Long equipoId;
}
