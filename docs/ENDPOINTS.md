# Torneos API — Documentación de endpoints

**Integrantes:** Juan Simón Patiño Pachón · Elizabeth Meneses Muñoz
**Repositorio:** https://github.com/ailist-cloud/Torneos_api
**URL base:** `http://localhost:8080`

## Antes de probar

1. Crear la base `torneos_db` en PostgreSQL y ejecutar `docs/00_crear_tablas.sql` y `docs/02_datos_prueba.sql`.
2. Definir la variable de entorno `DB_PASSWORD` con la contraseña del usuario `postgres` y ejecutar `TorneosApiApplication`.
3. Opcional: importar en Postman `docs/Torneos-api.postman_collection.json` (trae todas las peticiones con body y header).

**Autorización:** las peticiones que modifican datos llevan el header `X-Usuario-Id` con el id del usuario que hace la operación. Con los datos de prueba, `1` es Laura (ORGANIZADOR del torneo 1). Sin header → 401; usuario sin permiso → 403. Los `GET` no necesitan header.

**Formato de error (todas las respuestas de error):**
```json
{ "timestamp": "2026-11-12T10:00:00", "status": 409, "error": "Conflict",
  "mensaje": "RN-01: el equipo Águilas FC ya juega el partido 5 de 2026-12-05T10:00 a 2026-12-05T11:30",
  "ruta": "/api/partidos" }
```

**Valores permitidos:**
- `rol`: `ORGANIZADOR`, `REPRESENTANTE`, `PUBLICO`
- `estado` de torneo: `ABIERTO`, `CERRADO`, `EN_CURSO`, `FINALIZADO`
- `estado` de partido: `PROGRAMADO`, `EN_JUEGO`, `FINALIZADO`, `CANCELADO`
- `criteriosDesempate`: `DIFERENCIA_GOLES`, `GOLES_A_FAVOR`, `GOLES_EN_CONTRA`, `PARTIDOS_GANADOS`

---

## Contrato HTTP mínimo del enunciado

| # | Verbo | URL | Responsabilidad |
|---|---|---|---|
| 1 | GET | `localhost:8080/api/torneos/{id}/tabla` | Calcula posiciones |
| 2 | GET | `localhost:8080/api/partidos` | Consulta programación |
| 3 | POST | `localhost:8080/api/partidos` | Programa partido sin cruces |
| 4 | PUT | `localhost:8080/api/torneos/{id}` | Reemplaza configuración antes de iniciar |
| 5 | PATCH | `localhost:8080/api/partidos/{id}/resultado` | Registra o corrige resultado autorizado |
| 6 | DELETE | `localhost:8080/api/partidos/{id}` | Elimina partido futuro sin resultado |

El detalle de cada uno, con body, está abajo junto con los endpoints adicionales del CRUD.

---

## Usuarios

**GET** `localhost:8080/api/usuarios` — Lista los usuarios. → 200

**GET** `localhost:8080/api/usuarios/{id}` — Consulta un usuario. → 200 · 404 si no existe

**POST** `localhost:8080/api/usuarios` — Crea un usuario. → 201 · 400 rol inválido · 409 email repetido
```json
{ "nombre": "Carlos Rep", "email": "carlos@torneos.co", "rol": "REPRESENTANTE" }
```

## Equipos

**GET** `localhost:8080/api/equipos` — Lista los equipos. → 200

**GET** `localhost:8080/api/equipos/{id}` — Consulta un equipo. → 200 · 404

**POST** `localhost:8080/api/equipos` — Crea un equipo (solo ORGANIZADOR). Header `X-Usuario-Id: 1` → 201 · 401 · 403 · 409 nombre repetido · 422 el representante no tiene rol REPRESENTANTE
```json
{ "nombre": "Halcones", "ciudad": "Rionegro", "representanteId": 2 }
```

## Torneos

**GET** `localhost:8080/api/torneos` — Lista los torneos con sus criterios de desempate. → 200

**GET** `localhost:8080/api/torneos/{id}` — Consulta un torneo. → 200 · 404

**POST** `localhost:8080/api/torneos` — Crea un torneo en estado ABIERTO; el organizador es el usuario del header. Header `X-Usuario-Id: 1` → 201 · 400 puntos/fechas/criterios inválidos (RN-03, RN-05) · 401 · 403
```json
{
  "nombre": "Liga Universitaria 2026",
  "deporte": "Fútbol",
  "fechaInicio": "2026-11-15",
  "fechaFin": "2026-12-15",
  "puntosVictoria": 3,
  "puntosEmpate": 1,
  "puntosDerrota": 0,
  "criteriosDesempate": ["DIFERENCIA_GOLES", "GOLES_A_FAVOR"]
}
```

**PUT** `localhost:8080/api/torneos/{id}` — Reemplaza la configuración antes de iniciar (ABIERTO o CERRADO). Header `X-Usuario-Id: 1` → 200 · 403 · 404 · 422 si ya inició
```json
{
  "nombre": "Liga Universitaria 2026 (editada)",
  "deporte": "Fútbol",
  "fechaInicio": "2026-11-15",
  "fechaFin": "2026-12-15",
  "puntosVictoria": 3,
  "puntosEmpate": 1,
  "puntosDerrota": 0,
  "criteriosDesempate": ["PARTIDOS_GANADOS", "DIFERENCIA_GOLES"]
}
```

**PATCH** `localhost:8080/api/torneos/{id}/estado` — Cambia el estado (ABIERTO → CERRADO → EN_CURSO → FINALIZADO). Header `X-Usuario-Id: 1` → 200 · 422 transición no permitida, menos de 2 equipos o partidos pendientes
```json
{ "estado": "CERRADO" }
```

**POST** `localhost:8080/api/torneos/{id}/inscripciones` — Inscribe un equipo. Header `X-Usuario-Id: 1` → 201 · 409 ya inscrito · 422 torneo no ABIERTO (RN-06)
```json
{ "equipoId": 1 }
```

**GET** `localhost:8080/api/torneos/{id}/inscripciones` — Lista los equipos inscritos. → 200

**GET** `localhost:8080/api/torneos/{id}/tabla` — Calcula la tabla de posiciones desde los partidos FINALIZADOS (RN-04), desempatando con los criterios del torneo (RN-05). → 200 · 422 sin criterios

**GET** `localhost:8080/api/torneos/{id}/historial` — Trazabilidad del torneo: creación, cambios de configuración y de estado, inscripciones; con usuario y fecha. → 200

## Partidos

**GET** `localhost:8080/api/partidos` — Programación completa. Filtros opcionales: → 200
- `localhost:8080/api/partidos?torneoId=1`
- `localhost:8080/api/partidos?equipoId=1`

**GET** `localhost:8080/api/partidos/{id}` — Consulta un partido. → 200 · 404

**POST** `localhost:8080/api/partidos` — Programa un partido sin cruces de horario (RN-01). Los dos equipos deben estar inscritos y la fecha dentro del torneo. Header `X-Usuario-Id: 1` → 201 · 400 mismo equipo · 409 cruce de horario · 422 equipo no inscrito o fecha fuera del torneo
```json
{
  "torneoId": 1,
  "equipoLocalId": 3,
  "equipoVisitanteId": 4,
  "fechaHoraInicio": "2026-12-10T15:00:00",
  "duracionMin": 90
}
```

**PATCH** `localhost:8080/api/partidos/{id}/estado` — Cambia el estado: PROGRAMADO → EN_JUEGO → FINALIZADO (exige resultado), o CANCELADO. Header `X-Usuario-Id: 1` → 200 · 422
```json
{ "estado": "EN_JUEGO" }
```

**PATCH** `localhost:8080/api/partidos/{id}/resultado` — Registra o corrige el resultado (RN-02: solo PROGRAMADO o EN_JUEGO; RN-03: goles no negativos). Solo el organizador del torneo. Header `X-Usuario-Id: 1` → 200 · 400 goles negativos · 401 · 403 · 422 partido FINALIZADO o CANCELADO
```json
{ "golesLocal": 2, "golesVisitante": 1 }
```

**DELETE** `localhost:8080/api/partidos/{id}` — Elimina un partido futuro, PROGRAMADO y sin resultado. Header `X-Usuario-Id: 1` → 204 · 422 si ya pasó, tiene resultado o no está PROGRAMADO

**GET** `localhost:8080/api/partidos/{id}/historial` — Trazabilidad del partido (programación, cambios de estado, resultado registrado y corregido con el marcador anterior, eliminación). Se conserva aunque el partido se elimine. → 200

---

## Reglas de negocio y dónde se validan

| Regla | Validación | Respuesta |
|---|---|---|
| RN-01 Un equipo no juega dos partidos simultáneos | `POST /api/partidos` | 409 |
| RN-02 Resultado solo si el partido está PROGRAMADO o EN_JUEGO | `PATCH /api/partidos/{id}/resultado` | 422 |
| RN-03 Goles y puntos no negativos | `PATCH .../resultado`, `POST`/`PUT /api/torneos` | 400 |
| RN-04 La tabla se calcula, no se edita | `GET /api/torneos/{id}/tabla` (no existe endpoint para editarla) | — |
| RN-05 Criterios de desempate definidos | `POST`/`PUT /api/torneos`, `GET .../tabla` | 400 / 422 |
| RN-06 Torneo cerrado no admite equipos | `POST /api/torneos/{id}/inscripciones` | 422 |
