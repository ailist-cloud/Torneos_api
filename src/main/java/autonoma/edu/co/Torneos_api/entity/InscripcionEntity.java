package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tabla {@code inscripcion}: relaciona un equipo con un torneo (llave compuesta).
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
@Table(name = "inscripcion")
public class InscripcionEntity {

    /** Identificador único (autoincremental). */
    @EmbeddedId
    private InscripcionIdEntity id;

    // @MapsId: la columna torneo_id de la llave compuesta se llena con este torneo
    /** Torneo al que pertenece. */
    @ManyToOne
    @MapsId("torneoId")
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneo;

    /** Equipo. */
    @ManyToOne
    @MapsId("equipoId")
    @JoinColumn(name = "equipo_id")
    private EquipoEntity equipo;

    /** Fecha en que se inscribió el equipo. */
    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    public InscripcionEntity(TorneoEntity torneo, EquipoEntity equipo, LocalDate fechaInscripcion) {
        this.id = new InscripcionIdEntity(torneo.getId(), equipo.getId());
        this.torneo = torneo;
        this.equipo = equipo;
        this.fechaInscripcion = fechaInscripcion;
    }
}
