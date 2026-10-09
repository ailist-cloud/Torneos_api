package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "equipo")
public class Equipoentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 100)
    private String ciudad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "representante_id")
    private Usuarioentity representante;

    public Equipoentity() {
    }
}