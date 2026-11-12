package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.EstadoTorneo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tabla {@code torneo}: datos, puntos por resultado, estado y organizador.
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
@Table(name = "torneo")
public class TorneoEntity {

    /** Identificador único (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Deporte del torneo. */
    @Column(nullable = false, length = 50)
    private String deporte;

    /** Fecha de inicio (yyyy-MM-dd). */
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    /** Fecha de fin (yyyy-MM-dd). */
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    /** Estado actual. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTorneo estado;

    /** Puntos que da una victoria. */
    @Column(name = "puntos_victoria", nullable = false)
    private Integer puntosVictoria;

    /** Puntos que da un empate. */
    @Column(name = "puntos_empate", nullable = false)
    private Integer puntosEmpate;

    /** Puntos que da una derrota. */
    @Column(name = "puntos_derrota", nullable = false)
    private Integer puntosDerrota;

    /** Usuario ORGANIZADOR dueño del torneo. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "organizador_id")
    private UsuarioEntity organizador;
}
