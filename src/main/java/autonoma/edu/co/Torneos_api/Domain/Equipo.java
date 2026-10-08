package autonoma.edu.co.Torneos_api.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 100)
    private String ciudad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "representante_id")
    private Usuario representante;

    public Equipo() {
    }
}