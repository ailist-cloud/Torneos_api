package autonoma.edu.co.Torneos_api.Dto;

import autonoma.edu.co.Torneos_api.Domain.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cuerpo de {@code POST /api/usuarios}. */
public record CrearUsuarioRequest(
        @NotBlank(message = "es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "es obligatorio") @Email(message = "no es un email válido") @Size(max = 150) String email,
        @NotNull(message = "es obligatorio (ORGANIZADOR, REPRESENTANTE o PUBLICO)") RolUsuario rol
) { }
