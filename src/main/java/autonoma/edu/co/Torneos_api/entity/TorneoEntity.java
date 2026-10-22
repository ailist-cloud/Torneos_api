package autonoma.edu.co.Torneos_api.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "torneo")
public class TorneoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String deporte;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "puntos_victoria", nullable = false)
    private Integer puntosVictoria;

    @Column(name = "puntos_empate", nullable = false)
    private Integer puntosEmpate;

    @Column(name = "puntos_derrota", nullable = false)
    private Integer puntosDerrota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizador_id")
    private UsuarioEntity organizador;

    public TorneoEntity() {
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

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getPuntosVictoria() {
        return puntosVictoria;
    }

    public void setPuntosVictoria(Integer puntosVictoria) {
        this.puntosVictoria = puntosVictoria;
    }

    public Integer getPuntosEmpate() {
        return puntosEmpate;
    }

    public void setPuntosEmpate(Integer puntosEmpate) {
        this.puntosEmpate = puntosEmpate;
    }

    public Integer getPuntosDerrota() {
        return puntosDerrota;
    }

    public void setPuntosDerrota(Integer puntosDerrota) {
        this.puntosDerrota = puntosDerrota;
    }

    public UsuarioEntity getOrganizador() {
        return organizador;
    }

    public void setOrganizador(UsuarioEntity organizador) {
        this.organizador = organizador;
    }
}