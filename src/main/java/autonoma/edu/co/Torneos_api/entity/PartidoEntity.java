package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Tabla {@code partido}. Incluye la lógica para saber si dos horarios se cruzan (RN-01).
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
@Table(name = "partido")
public class PartidoEntity {

    /** Identificador único (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Torneo al que pertenece. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "torneo_id")
    private TorneoEntity torneo;

    /** Equipo local. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_local_id")
    private EquipoEntity equipoLocal;

    /** Equipo visitante. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_visitante_id")
    private EquipoEntity equipoVisitante;

    /** Fecha y hora de inicio (yyyy-MM-ddTHH:mm:ss). */
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    /** Duración del partido en minutos. */
    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;

    /** Estado actual. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPartido estado;

    /** Goles del equipo local (no negativos, RN-03). */
    @Column(name = "goles_local")
    private Integer golesLocal;

    /** Goles del equipo visitante (no negativos, RN-03). */
    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    /**
     * Hora en que termina el partido (inicio + duración). No es columna, se calcula.
     *
     * @return el resultado
     */
    public LocalDateTime calcularFechaHoraFin() {
        return fechaHoraInicio.plusMinutes(duracionMin);
    }

    /**
     * true si ya tiene marcador registrado.
     *
     * @return {@code true} si se cumple la condición
     */
    public boolean tieneResultado() {
        return golesLocal != null && golesVisitante != null;
    }

    /**
     * true si el equipo juega este partido como local o como visitante.
     *
     * @param equipoId id del equipo
     * @return {@code true} si se cumple la condición
     */
    public boolean participa(Long equipoId) {
        return equipoLocal.getId().equals(equipoId) || equipoVisitante.getId().equals(equipoId);
    }

    /**
     * RN-01: dos intervalos [inicio, fin) se cruzan si uno empieza antes de que el otro termine.
     * Ej: 10:00-11:30 y 11:00-12:30 se cruzan; 10:00-11:30 y 11:30-13:00 no.
     *
     * @param otroInicio otro inicio
     * @param otroFin otro fin
     * @return {@code true} si se cumple la condición
     */
    public boolean seCruzaCon(LocalDateTime otroInicio, LocalDateTime otroFin) {
        return fechaHoraInicio.isBefore(otroFin) && otroInicio.isBefore(calcularFechaHoraFin());
    }
}
