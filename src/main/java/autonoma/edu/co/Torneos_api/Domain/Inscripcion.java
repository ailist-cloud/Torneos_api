package autonoma.edu.co.Torneos_api.Domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    @EmbeddedId
    private InscripcionId id;

    @ManyToOne
    @MapsId("torneoId")
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    @ManyToOne
    @MapsId("equipoId")
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    public Inscripcion() {
    }
}