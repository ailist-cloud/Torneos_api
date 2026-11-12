package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla {@code criterio_desempate}: los criterios de un torneo con su orden.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "criterio_desempate")
public class CriterioDesempateEntity {

    /** Identificador único (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Torneo al que pertenece. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneo;

    /** Orden en que se aplica el criterio (1 = primero). */
    @Column(nullable = false)
    private Integer orden;

    /** Criterio de desempate. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CriterioDesempate criterio;

    public CriterioDesempateEntity(TorneoEntity torneo, Integer orden, CriterioDesempate criterio) {
        this.torneo = torneo;
        this.orden = orden;
        this.criterio = criterio;
    }
}
