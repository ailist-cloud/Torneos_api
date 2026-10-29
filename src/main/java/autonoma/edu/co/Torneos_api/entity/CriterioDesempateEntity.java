package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}