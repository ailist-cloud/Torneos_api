package autonoma.edu.co.Torneos_api.Service;

import autonoma.edu.co.Torneos_api.Domain.CriterioDesempate;
import autonoma.edu.co.Torneos_api.Domain.EstadoPartido;
import autonoma.edu.co.Torneos_api.Dto.PosicionResponse;
import autonoma.edu.co.Torneos_api.Dto.TablaPosicionesResponse;
import autonoma.edu.co.Torneos_api.Excepciones.ReglaNegocioException;
import autonoma.edu.co.Torneos_api.Repository.CriterioDesempateRepository;
import autonoma.edu.co.Torneos_api.Repository.InscripcionRepository;
import autonoma.edu.co.Torneos_api.Repository.PartidoRepository;
import autonoma.edu.co.Torneos_api.entity.CriterioDesempateEntity;
import autonoma.edu.co.Torneos_api.entity.EquipoEntity;
import autonoma.edu.co.Torneos_api.entity.InscripcionEntity;
import autonoma.edu.co.Torneos_api.entity.PartidoEntity;
import autonoma.edu.co.Torneos_api.entity.TorneoEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RN-04: la tabla NO es una tabla de la BD. Se calcula cada vez a partir de los partidos FINALIZADOS.
 *
 * Pasos:
 *  1. Se crea una fila en cero por cada equipo inscrito.
 *  2. Se recorre cada partido finalizado y se suman PJ, PG, PE, PP, GF, GC y puntos.
 *  3. Se ordena: primero por puntos y luego por los criterios de desempate del torneo, en su orden.
 */
@Service
public class TablaPosicionesService {

    private final TorneoService torneoService;
    private final InscripcionRepository inscripcionRepository;
    private final PartidoRepository partidoRepository;
    private final CriterioDesempateRepository criterioRepository;

    public TablaPosicionesService(TorneoService torneoService,
                                  InscripcionRepository inscripcionRepository,
                                  PartidoRepository partidoRepository,
                                  CriterioDesempateRepository criterioRepository) {
        this.torneoService = torneoService;
        this.inscripcionRepository = inscripcionRepository;
        this.partidoRepository = partidoRepository;
        this.criterioRepository = criterioRepository;
    }

    @Transactional(readOnly = true)
    public TablaPosicionesResponse calcular(Long torneoId) {
        TorneoEntity torneo = torneoService.buscarEntidad(torneoId);

        // RN-05: sin criterios no se puede desempatar
        List<CriterioDesempate> criterios = new ArrayList<>();
        for (CriterioDesempateEntity entidad : criterioRepository.findByTorneoIdOrderByOrdenAsc(torneoId)) {
            criterios.add(entidad.getCriterio());
        }
        if (criterios.isEmpty()) {
            throw new ReglaNegocioException("RN-05: el torneo " + torneo.getNombre()
                    + " no tiene criterios de desempate. Defínalos con PUT /api/torneos/" + torneoId);
        }

        // 1. Una fila por equipo inscrito (la llave del mapa es el id del equipo)
        Map<Long, FilaTabla> filas = new HashMap<>();
        for (InscripcionEntity inscripcion : inscripcionRepository.findByTorneoIdOrderByFechaInscripcionAsc(torneoId)) {
            EquipoEntity equipo = inscripcion.getEquipo();
            filas.put(equipo.getId(), new FilaTabla(equipo));
        }

        // 2. Sumar cada partido finalizado
        for (PartidoEntity partido : partidoRepository.findByTorneoIdAndEstado(torneoId, EstadoPartido.FINALIZADO)) {
            if (partido.getGolesLocal() == null || partido.getGolesVisitante() == null) {
                continue;
            }
            FilaTabla local = obtenerFila(filas, partido.getEquipoLocal());
            FilaTabla visitante = obtenerFila(filas, partido.getEquipoVisitante());
            local.sumarPartido(partido.getGolesLocal(), partido.getGolesVisitante(), torneo);
            visitante.sumarPartido(partido.getGolesVisitante(), partido.getGolesLocal(), torneo);
        }

        // 3. Ordenar
        List<FilaTabla> ordenadas = new ArrayList<>(filas.values());
        ordenadas.sort(new ComparadorPosiciones(criterios));

        List<PosicionResponse> posiciones = new ArrayList<>();
        int posicion = 1;
        for (FilaTabla fila : ordenadas) {
            posiciones.add(fila.aRespuesta(posicion));
            posicion++;
        }

        return new TablaPosicionesResponse(torneo.getId(), torneo.getNombre(), criterios, posiciones);
    }

    /** Si un equipo jugó pero no aparece inscrito, igual se le crea su fila. */
    private FilaTabla obtenerFila(Map<Long, FilaTabla> filas, EquipoEntity equipo) {
        FilaTabla fila = filas.get(equipo.getId());
        if (fila == null) {
            fila = new FilaTabla(equipo);
            filas.put(equipo.getId(), fila);
        }
        return fila;
    }

    // =====================================================================================

    /** Acumulado de un equipo en la tabla. */
    private static class FilaTabla {
        private final EquipoEntity equipo;
        private int jugados;
        private int ganados;
        private int empatados;
        private int perdidos;
        private int golesAFavor;
        private int golesEnContra;
        private int puntos;

        FilaTabla(EquipoEntity equipo) {
            this.equipo = equipo;
        }

        void sumarPartido(int golesPropios, int golesRival, TorneoEntity torneo) {
            jugados++;
            golesAFavor += golesPropios;
            golesEnContra += golesRival;

            if (golesPropios > golesRival) {
                ganados++;
                puntos += torneo.getPuntosVictoria();
            } else if (golesPropios == golesRival) {
                empatados++;
                puntos += torneo.getPuntosEmpate();
            } else {
                perdidos++;
                puntos += torneo.getPuntosDerrota();
            }
        }

        int diferenciaGoles() {
            return golesAFavor - golesEnContra;
        }

        PosicionResponse aRespuesta(int posicion) {
            return new PosicionResponse(posicion, equipo.getId(), equipo.getNombre(),
                    jugados, ganados, empatados, perdidos,
                    golesAFavor, golesEnContra, diferenciaGoles(), puntos);
        }
    }

    /**
     * Decide quién va primero entre dos filas.
     * compare devuelve un número negativo si "a" va antes que "b", positivo si va después y 0 si empatan.
     * Integer.compare(b, a) ordena de MAYOR a menor; Integer.compare(a, b) de menor a mayor.
     */
    private static class ComparadorPosiciones implements Comparator<FilaTabla> {

        private final List<CriterioDesempate> criterios;

        ComparadorPosiciones(List<CriterioDesempate> criterios) {
            this.criterios = criterios;
        }

        @Override
        public int compare(FilaTabla a, FilaTabla b) {
            // Primero los puntos (más puntos va primero)
            int resultado = Integer.compare(b.puntos, a.puntos);
            if (resultado != 0) {
                return resultado;
            }

            // Empate en puntos: se aplican los criterios en el orden del torneo
            for (CriterioDesempate criterio : criterios) {
                resultado = compararPorCriterio(a, b, criterio);
                if (resultado != 0) {
                    return resultado;
                }
            }

            // Si siguen empatados, orden alfabético para que la tabla siempre salga igual
            return a.equipo.getNombre().compareToIgnoreCase(b.equipo.getNombre());
        }

        private int compararPorCriterio(FilaTabla a, FilaTabla b, CriterioDesempate criterio) {
            switch (criterio) {
                case DIFERENCIA_GOLES:
                    return Integer.compare(b.diferenciaGoles(), a.diferenciaGoles());
                case GOLES_A_FAVOR:
                    return Integer.compare(b.golesAFavor, a.golesAFavor);
                case GOLES_EN_CONTRA:
                    return Integer.compare(a.golesEnContra, b.golesEnContra); // menos goles en contra va primero
                case PARTIDOS_GANADOS:
                    return Integer.compare(b.ganados, a.ganados);
                default:
                    return 0;
            }
        }
    }
}
