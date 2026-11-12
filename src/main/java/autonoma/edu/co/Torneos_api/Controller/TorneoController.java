package autonoma.edu.co.Torneos_api.Controller;

import autonoma.edu.co.Torneos_api.Dto.ActualizarTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CambiarEstadoTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.CrearTorneoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscribirEquipoRequest;
import autonoma.edu.co.Torneos_api.Dto.InscripcionResponse;
import autonoma.edu.co.Torneos_api.Dto.TablaPosicionesResponse;
import autonoma.edu.co.Torneos_api.Dto.TorneoResponse;
import autonoma.edu.co.Torneos_api.Service.TablaPosicionesService;
import autonoma.edu.co.Torneos_api.Service.TorneoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final TablaPosicionesService tablaPosicionesService;

    public TorneoController(TorneoService torneoService,
                            TablaPosicionesService tablaPosicionesService) {
        this.torneoService = torneoService;
        this.tablaPosicionesService = tablaPosicionesService;
    }

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
    public TorneoResponse crear(@RequestBody CrearTorneoRequest request) {
        return torneoService.crear(request);
    }

    @PutMapping("/{id}")
    public TorneoResponse actualizar(@PathVariable Long id, @RequestBody ActualizarTorneoRequest request) {
        return torneoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public TorneoResponse cambiarEstado(@PathVariable Long id, @RequestBody CambiarEstadoTorneoRequest request) {
        return torneoService.cambiarEstado(id, request);
    }

    @GetMapping("/{id}/tabla")
    public TablaPosicionesResponse calcularTabla(@PathVariable Long id) {
        return tablaPosicionesService.calcular(id);
    }

    @PostMapping("/{id}/inscripciones")
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse inscribirEquipo(@PathVariable Long id, @RequestBody InscribirEquipoRequest request) {
        return torneoService.inscribirEquipo(id, request);
    }

    @GetMapping("/{id}/inscripciones")
    public List<InscripcionResponse> listarInscripciones(@PathVariable Long id) {
        return torneoService.listarInscripciones(id);
    }
}
