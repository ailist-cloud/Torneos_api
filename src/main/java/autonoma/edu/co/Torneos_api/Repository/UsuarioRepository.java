package autonoma.edu.co.Torneos_api.Repository;

import autonoma.edu.co.Torneos_api.Domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}