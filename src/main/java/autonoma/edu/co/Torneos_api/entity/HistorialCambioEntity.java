package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Trazabilidad: cada cambio importante (programar, cambiar estado, registrar o corregir resultado,
 * eliminar, reemplazar configuración) deja una fila aquí con quién lo hizo y cuándo.
 * entidad_id NO es llave foránea a propósito: así el historial se conserva aunque el partido se elimine.
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
@Table(name = "historial_cambio")
public class HistorialCambioEntity {

    /** Identificador único (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tipo de registro afectado: PARTIDO o TORNEO. */
    @Column(nullable = false, length = 20)
    private String entidad;      // "PARTIDO" o "TORNEO"

    /** Id del partido o torneo afectado. */
    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    /** Acción realizada (por ejemplo RESULTADO_CORREGIDO). */
    @Column(nullable = false, length = 30)
    private String accion;       // ej: "RESULTADO_CORREGIDO"

    /** Detalle del cambio (por ejemplo "1-1 -> 2-1"). */
    @Column(length = 255)
    private String detalle;      // ej: "1-1 -> 2-1"

    /** Usuario que hizo el cambio. */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    /** Fecha y hora del cambio. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}
