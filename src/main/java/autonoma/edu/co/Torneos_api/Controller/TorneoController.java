package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.ActualizarTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.HistorialResponse;
import autonoma.edu.co.Torneos_api.Dto.InscribirEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscripcionResponse;
import autonoma.edu.co.Torneos_api.Dto.TablaPosicionesResponse;
import autonoma.edu.co.Torneos_api.Dto.TorneoResponse;
import autonoma.edu.co.Torneos_api.Service.AutorizacionService;
import autonoma.edu.co.Torneos_api.Service.TablaPosicionesService;
import autonoma.edu.co.Torneos_api.Service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final TablaPosicionesService tablaPosicionesService;

    @GetMapping
    public List<TorneoResponse> listar() {
        return torneoService.listar();
    }

    @GetMapping("/{id}")
    public TorneoResponse consultar(@PathVariable Long id) {
        return torneoService.consultar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TorneoResponse crear(@Valid @RequestBody CrearTorneoRequest request,
                                @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.crear(request, usuarioId);
    }

    @PutMapping("/{id}")
    public TorneoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarTorneoRequest request,
                                     @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.actualizar(id, request, usuarioId);
    }

    @PatchMapping("/{id}/estado")
    public TorneoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoTorneoRequest request,
                                        @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.cambiarEstado(id, request, usuarioId);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialResponse> consultarHistorial(@PathVariable Long id) {
        return torneoService.consultarHistorial(id);
    }

    @GetMapping("/{id}/tabla")
    public TablaPosicionesResponse calcularTabla(@PathVariable Long id) {
        return tablaPosicionesService.calcular(id);
    }

    @PostMapping("/{id}/inscripciones")
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse inscribirEquipo(@PathVariable Long id, @Valid @RequestBody InscribirEquipoRequest request,
                                               @RequestHeader(value = AutorizacionService.CABECERA_USUARIO, required = false) Long usuarioId) {
        return torneoService.inscribirEquipo(id, request, usuarioId);
    }

    @GetMapping("/{id}/inscripciones")
    public List<InscripcionResponse> listarInscripciones(@PathVariable Long id) {
        return torneoService.listarInscripciones(id);
    }
}
