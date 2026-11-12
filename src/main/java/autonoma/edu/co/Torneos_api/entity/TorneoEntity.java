package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Tabla {@code torneo}: datos, puntos por resultado, estado y organizador. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "torneo")
public class TorneoEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTorneo estado;

    @Column(name = "puntos_victoria", nullable = false)
    private Integer puntosVictoria;

    @Column(name = "puntos_empate", nullable = false)
    private Integer puntosEmpate;

    @Column(name = "puntos_derrota", nullable = false)
    private Integer puntosDerrota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizador_id")
    private UsuarioEntity organizador;
}
