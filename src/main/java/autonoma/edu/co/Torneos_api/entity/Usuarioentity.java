package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuarioentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String rol;

    public Usuarioentity() {
    }
}