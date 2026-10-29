package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Dto.PosicionResponse;
import autonoma.edu.co.Torneos_api.Dto.TablaPosicionesResponse;
import autonoma.edu.co.Torneos_api.Repository.CriterioDesempateRepository;
import autonoma.edu.co.Torneos_api.Repository.InscripcionRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.Repository.TorneoRepository;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TablaPosicionesService {

    private final TorneoRepository torneoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PartidoRepository partidoRepository;
    private final CriterioDesempateRepository criterioDesempateRepository;

    public TablaPosicionesService(TorneoRepository torneoRepository,
                                  InscripcionRepository inscripcionRepository,
                                  PartidoRepository partidoRepository,
                                  CriterioDesempateRepository criterioDesempateRepository) {
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.partidoRepository = partidoRepository;
        this.criterioDesempateRepository = criterioDesempateRepository;
    }

    @Transactional(readOnly = true)
    public TablaPosicionesResponse calcular(Long torneoId) {
        TorneoEntity torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El torneo " + torneoId + " no existe"));


        List<CriterioDesempate> criterios = new ArrayList<>();
        for (CriterioDesempateEntity entidad : criterioDesempateRepository.findByTorneoentity_IdOrderByOrdenAsc(torneoId)) {
            criterios.add(convertirCriterioGuardado(entidad));
        }
        if (criterios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "RN-05: el torneo " + torneo.getNombre() + " no tiene criterios de desempate definidos");
        }


        Map<Long, Fila> filas = new LinkedHashMap<>();
        for (InscripcionEntity inscripcion : inscripcionRepository.findByTorneoentity_Id(torneoId)) {
            EquipoEntity equipo = inscripcion.getEquipoentity();
            filas.put(equipo.getId(), new Fila(equipo));
        }


        for (PartidoEntity partido : partidoRepository.findByTorneoentity_IdAndEstado(torneoId, EstadoPartido.FINALIZADO.name())) {
            if (partido.getGolesLocal() == null || partido.getGolesVisitante() == null) {
                continue;
            }
            Fila local = filas.computeIfAbsent(partido.getEquipoEntityLocal().getId(),
                    k -> new Fila(partido.getEquipoEntityLocal()));
            Fila visitante = filas.computeIfAbsent(partido.getEquipoEntityVisitante().getId(),
                    k -> new Fila(partido.getEquipoEntityVisitante()));
            local.registrar(partido.getGolesLocal(), partido.getGolesVisitante(), torneo);
            visitante.registrar(partido.getGolesVisitante(), partido.getGolesLocal(), torneo);
        }


        Comparator<Fila> orden = Comparator.comparingInt((Fila f) -> f.puntos).reversed();
        for (CriterioDesempate criterio : criterios) {
            orden = orden.thenComparing(comparadorDe(criterio));
        }
        orden = orden.thenComparing(f -> f.equipo.getNombre(), String.CASE_INSENSITIVE_ORDER);

        List<Fila> ordenadas = filas.values().stream().sorted(orden).toList();
        List<PosicionResponse> posiciones = new ArrayList<>();
        for (int i = 0; i < ordenadas.size(); i++) {
            posiciones.add(ordenadas.get(i).aRespuesta(i + 1));
        }

        return new TablaPosicionesResponse(
                torneo.getId(),
                torneo.getNombre(),
                criterios.stream().map(Enum::name).toList(),
                posiciones);
    }



    private Comparator<Fila> comparadorDe(CriterioDesempate criterio) {
        return switch (criterio) {
            case DIFERENCIA_GOLES -> Comparator.comparingInt((Fila f) -> f.golesAFavor - f.golesEnContra).reversed();
            case GOLES_A_FAVOR -> Comparator.comparingInt((Fila f) -> f.golesAFavor).reversed();
            case GOLES_EN_CONTRA -> Comparator.comparingInt((Fila f) -> f.golesEnContra);
            case PARTIDOS_GANADOS -> Comparator.comparingInt((Fila f) -> f.ganados).reversed();
        };
    }

    private CriterioDesempate convertirCriterioGuardado(CriterioDesempateEntity entidad) {
        try {
            return CriterioDesempate.valueOf(entidad.getCriterio().trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "RN-05: criterio de desempate desconocido en la BD: " + entidad.getCriterio());
        }
    }


    private static final class Fila {
        private final EquipoEntity equipo;
        private int jugados;
        private int ganados;
        private int empatados;
        private int perdidos;
        private int golesAFavor;
        private int golesEnContra;
        private int puntos;

        private Fila(EquipoEntity equipo) {
            this.equipo = equipo;
        }

        private void registrar(int aFavor, int enContra, TorneoEntity torneo) {
            jugados++;
            golesAFavor += aFavor;
            golesEnContra += enContra;
            if (aFavor > enContra) {
                ganados++;
                puntos += torneo.getPuntosVictoria();
            } else if (aFavor == enContra) {
                empatados++;
                puntos += torneo.getPuntosEmpate();
            } else {
                perdidos++;
                puntos += torneo.getPuntosDerrota();
            }
        }

        private PosicionResponse aRespuesta(int posicion) {
            return new PosicionResponse(
                    posicion,
                    equipo.getId(),
                    equipo.getNombre(),
                    jugados,
                    ganados,
                    empatados,
                    perdidos,
                    golesAFavor,
                    golesEnContra,
                    golesAFavor - golesEnContra,
                    puntos);
        }
    }
}