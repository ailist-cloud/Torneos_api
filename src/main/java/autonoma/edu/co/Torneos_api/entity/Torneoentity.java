package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "torneo")
public class Torneoentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String deporte;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "puntos_victoria", nullable = false)
    private Integer puntosVictoria;

    @Column(name = "puntos_empate", nullable = false)
    private Integer puntosEmpate;

    @Column(name = "puntos_derrota", nullable = false)
    private Integer puntosDerrota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizador_id")
    private Usuarioentity organizador;

    public Torneoentity() {
    }
}