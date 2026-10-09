package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "criterio_desempate")
public class CriterioDesempateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "torneo_id")
    private Torneoentity torneoentity;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false, length = 30)
    private String criterio;

    public CriterioDesempateEntity() {
    }
}