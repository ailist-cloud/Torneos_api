package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Tabla {@code inscripcion}: relaciona un equipo con un torneo (llave compuesta). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inscripcion")
public class InscripcionEntity {

    @EmbeddedId
    private InscripcionIdEntity id;

    // @MapsId: la columna torneo_id de la llave compuesta se llena con este torneo
    @ManyToOne
    @MapsId("torneoId")
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneo;

    @ManyToOne
    @MapsId("equipoId")
    @JoinColumn(name = "equipo_id")
    private EquipoEntity equipo;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    public InscripcionEntity(TorneoEntity torneo, EquipoEntity equipo, LocalDate fechaInscripcion) {
        this.id = new InscripcionIdEntity(torneo.getId(), equipo.getId());
        this.torneo = torneo;
        this.equipo = equipo;
        this.fechaInscripcion = fechaInscripcion;
    }
}
