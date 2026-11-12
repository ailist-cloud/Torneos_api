package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabla {@code criterio_desempate}: los criterios de un torneo con su orden. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "criterio_desempate")
public class CriterioDesempateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneo;

    @Column(nullable = false)
    private Integer orden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CriterioDesempate criterio;

    public CriterioDesempateEntity(TorneoEntity torneo, Integer orden, CriterioDesempate criterio) {
        this.torneo = torneo;
        this.orden = orden;
        this.criterio = criterio;
    }
}
