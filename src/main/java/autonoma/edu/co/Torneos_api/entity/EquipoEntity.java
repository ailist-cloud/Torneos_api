package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "equipo")
public class EquipoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 100)
    private String ciudad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "representante_id")
    private UsuarioEntity representante;

    public EquipoEntity() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public UsuarioEntity getRepresentante() {
        return representante;
    }

    public void setRepresentante(UsuarioEntity representante) {
        this.representante = representante;
    }
}