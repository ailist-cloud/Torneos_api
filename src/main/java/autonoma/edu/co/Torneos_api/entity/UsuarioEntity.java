package autonoma.edu.co.Torneos_api.entity;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla {@code usuario}. El rol se guarda como texto (por ejemplo {@code "ORGANIZADOR"}).
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
@Table(name = "usuario")
public class UsuarioEntity {

    /** Identificador único (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Correo electrónico (único). */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Rol del usuario: ORGANIZADOR, REPRESENTANTE o PUBLICO. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;
}
