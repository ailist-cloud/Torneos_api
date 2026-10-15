package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscripcion")
public class InscripcionEntity {

    @EmbeddedId
    private InscripcionIdEntity id;

    @ManyToOne
    @MapsId("torneoId")
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneoentity;

    @ManyToOne
    @MapsId("equipoId")
    @JoinColumn(name = "equipo_id")
    private EquipoEntity equipoentity;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    public InscripcionEntity() {
    }
}