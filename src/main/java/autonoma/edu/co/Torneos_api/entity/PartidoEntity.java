package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "partido")
public class PartidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_local_id")
    private EquipoEntity equipoLocal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_visitante_id")
    private EquipoEntity equipoVisitante;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPartido estado;

    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    /** Hora en que termina el partido (inicio + duración). No es columna, se calcula. */
    public LocalDateTime calcularFechaHoraFin() {
        return fechaHoraInicio.plusMinutes(duracionMin);
    }

    /** true si el equipo juega este partido como local o como visitante. */
    public boolean participa(Long equipoId) {
        return equipoLocal.getId().equals(equipoId) || equipoVisitante.getId().equals(equipoId);
    }

    /**
     * RN-01: dos intervalos [inicio, fin) se cruzan si uno empieza antes de que el otro termine.
     * Ej: 10:00-11:30 y 11:00-12:30 se cruzan; 10:00-11:30 y 11:30-13:00 no.
     */
    public boolean seCruzaCon(LocalDateTime otroInicio, LocalDateTime otroFin) {
        return fechaHoraInicio.isBefore(otroFin) && otroInicio.isBefore(calcularFechaHoraFin());
    }
}
