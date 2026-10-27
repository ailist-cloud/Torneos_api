package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partido")
public class PartidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "torneo_id")
    private Torneoentity torneoentity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_local_id")
    private Equipoentity equipoentityLocal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_visitante_id")
    private Equipoentity equipoentityVisitante;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    public PartidoEntity() {
    }
}