package autonoma.edu.co.Torneos_api.Domain;

/**
 * Criterios para ordenar equipos empatados en puntos en la tabla de posiciones (RN-05).
 * Cada torneo define cuáles usa y en qué orden.
 *
 * @author Juan Simón Patiño Pachón
 * @author Elizabeth Meneses Muñoz
 * @version 1.0
 * @since 2026-11
 */
public enum CriterioDesempate {
    DIFERENCIA_GOLES,
    GOLES_A_FAVOR,
    GOLES_EN_CONTRA,
    PARTIDOS_GANADOS
}
