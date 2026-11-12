package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Llave primaria compuesta de inscripcion: (torneo_id, equipo_id). Necesita equals y hashCode.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class InscripcionIdEntity implements Serializable {

    /** Id del torneo. */
    @Column(name = "torneo_id")
    private Long torneoId;

    /** Id del equipo. */
    @Column(name = "equipo_id")
    private Long equipoId;
}
