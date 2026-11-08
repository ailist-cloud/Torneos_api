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
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "historial_cambio")
public class HistorialCambioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String entidad;      // "PARTIDO" o "TORNEO"

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @Column(nullable = false, length = 30)
    private String accion;       // ej: "RESULTADO_CORREGIDO"

    @Column(length = 255)
    private String detalle;      // ej: "1-1 -> 2-1"

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}
