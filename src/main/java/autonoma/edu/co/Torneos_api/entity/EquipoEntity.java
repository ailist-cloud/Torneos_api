package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla {@code equipo}.
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
@Table(name = "equipo")
public class EquipoEntity {

    /** Identificador único (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre. */
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /** Ciudad del equipo. */
    @Column(length = 100)
    private String ciudad;

    /** Usuario con rol REPRESENTANTE que representa al equipo. */
    @ManyToOne
    @JoinColumn(name = "representante_id")
    private UsuarioEntity representante;
}
