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
    private TorneoEntity torneoentity;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false, length = 30)
    private String criterio;

    public CriterioDesempateEntity() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TorneoEntity getTorneoentity() {
        return torneoentity;
    }

    public void setTorneoentity(TorneoEntity torneoentity) {
        this.torneoentity = torneoentity;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }
}