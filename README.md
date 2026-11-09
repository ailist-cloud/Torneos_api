# Torneos-api

Backend para la gestión de torneos deportivos: equipos, calendario, resultados y tabla de posiciones derivada.

**Integrantes:** Juan Simón Patiño Pachón · Elizabeth Meneses Muñoz

## Problema

La organización manual de torneos genera inconsistencias en los resultados y no detecta cruces de partidos. Este backend aplica reglas del dominio, rechaza operaciones inválidas y responde con contratos HTTP consistentes.

## Tecnologías

- Java 21
- Spring Boot 3.3.5
- Maven
- Spring Data JPA + Hibernate
- PostgreSQL
- Git y GitHub

## Estructura del proyecto

```
autonoma.edu.co.Torneos_api
├── Config         → ManejadorErrores (todas las respuestas de error con el mismo JSON)
├── Controller     → recibe la petición HTTP y llama al servicio
├── Domain         → enums con reglas: EstadoTorneo, EstadoPartido, RolUsuario, CriterioDesempate
├── Dto            → records de entrada (con validaciones @NotNull, @Min...) y de salida
├── Excepciones    → 400 SolicitudInvalida, 404 RecursoNoEncontrado, 409 Conflicto, 422 ReglaNegocio
├── Repository     → Spring Data JPA
├── Service        → lógica y reglas de negocio RN-01 a RN-06
├── entity         → entidades JPA (estados guardados como enum con @Enumerated)
└── TorneosApiApplication.java
```

## Modelo de datos

![Diagrama de base de datos](docs/Modelo%20Relacional.drawio.png)
Entidades: Usuario, Torneo, CriterioDesempate, Equipo, Inscripcion y Partido, más `historial_cambio` para la trazabilidad. La tabla de posiciones no es una tabla: se calcula desde los resultados.

### Avance hasta ahora
- [x] Proyecto Spring Boot 3.3.5 configurado con Maven y Java 21.
- [x] Base de datos `torneos_db` en PostgreSQL con 6 tablas relacionadas.
- [x] Entidades JPA (`@Entity`) con métodos accesores (Getters/Setters) y Repositorios JPA.
- [x] Endpoint de diagnóstico `GET /api/estado`.
- [x] Módulo de Partidos completo: `GET`, `POST`, `PATCH` y `DELETE` en `/api/partidos`.
- [x] Módulo de Torneos: `GET /api/torneos/{id}/tabla` y `PUT /api/torneos/{id}`.
- [x] Validación de cruces de horario (RN-01) y de todas las reglas RN-02 a RN-06.
- [x] Manejo de errores uniforme (400/404/409/422) y validación de DTOs con `@Valid`.
- [x] Colección de pruebas HTTP: `docs/Torneos-api.postman_collection.json`.
- [x] Trazabilidad: tabla `historial_cambio` con quién hizo cada cambio y cuándo.
- [x] Autorización por rol con el header `X-Usuario-Id` (solo el organizador modifica su torneo).
- [x] El resultado se registra y se corrige mientras el partido está en juego; luego se finaliza.

## Cómo ejecutar

1. Crear la base `torneos_db` en PostgreSQL y, en pgAdmin (Query Tool):
   - Base nueva: ejecutar `docs/00_crear_tablas.sql` y luego `docs/02_datos_prueba.sql`.
   - Base que ya tiene tablas: ejecutar `docs/01_arreglar_bd_existente.sql` (vuelve autoincrementales los `id` y pasa los estados a MAYÚSCULAS) y `docs/03_agregar_historial.sql` (tabla de trazabilidad).
2. Definir la contraseña como variable de entorno (no se guarda en el repositorio):

```
$env:DB_PASSWORD="tu_contraseña"
```

3. Ejecutar:

```
.\mvnw.cmd spring-boot:run
```

4. Probar: `http://localhost:8080/api/estado`
5. En Postman: *Import* → `docs/Torneos-api.postman_collection.json`.

## Pruebas unitarias

Están en `src/test/java` y usan **JUnit 5 + Mockito** (vienen en `spring-boot-starter-test`).
Los repositorios se simulan con Mockito, así que **no necesitan base de datos** (excepto `TorneosApiApplicationTests`, que levanta la aplicación completa).

| Clase de prueba | Qué comprueba |
|---|---|
| `EstadoPartidoTest` | RN-02 y transiciones de estado del partido |
| `EstadoTorneoTest` | RN-06 y ciclo de vida del torneo |
| `PartidoEntityTest` | RN-01: cálculo de cruces de horario |
| `PartidoServiceTest` | RN-01, RN-02, registrar/corregir resultado, eliminar, trazabilidad y autorización |
| `TablaPosicionesServiceTest` | RN-04 (tabla calculada) y RN-05 (criterios de desempate) |
| `TorneoServiceTest` | RN-05, RN-06, reglas de puntos, fechas y estados del torneo |
| `AutorizacionServiceTest` | 401 sin usuario, 403 sin permiso |
| `ValidacionDtoTest` | RN-03 (goles y puntos no negativos) y validaciones de los DTO |

Ejecutar: en IntelliJ, clic derecho sobre `src/test/java` → **Run 'All Tests'**, o en consola:

```
.\mvnw.cmd test
```

## Contrato HTTP mínimo

| Verbo | Ruta | Responsabilidad |
|---|---|---|
| GET | /api/torneos/{id}/tabla | Calcula posiciones |
| GET | /api/partidos | Consulta programación |
| POST | /api/partidos | Programa partido sin cruces |
| PUT | /api/torneos/{id} | Reemplaza configuración antes de iniciar |
| PATCH | /api/partidos/{id}/resultado | Registra o corrige resultado |
| DELETE | /api/partidos/{id} | Elimina partido futuro sin resultado |

### Endpoints adicionales

| Verbo | Ruta | Responsabilidad |
|---|---|---|
| GET / POST | /api/usuarios, /api/usuarios/{id} | Usuarios (ORGANIZADOR, REPRESENTANTE, PUBLICO) |
| GET / POST | /api/equipos, /api/equipos/{id} | Equipos (el representante debe tener rol REPRESENTANTE) |
| GET / POST | /api/torneos, /api/torneos/{id} | Torneos con sus criterios de desempate |
| PATCH | /api/torneos/{id}/estado | ABIERTO → CERRADO → EN_CURSO → FINALIZADO |
| GET / POST | /api/torneos/{id}/inscripciones | Inscribir equipos (RN-06) |
| GET | /api/partidos?torneoId=&equipoId= | Programación filtrada (filtros opcionales) |
| GET | /api/partidos/{id} | Un partido |
| PATCH | /api/partidos/{id}/estado | PROGRAMADO → EN_JUEGO → FINALIZADO (o CANCELADO) |
| GET | /api/partidos/{id}/historial | Trazabilidad del partido (se conserva aunque se elimine) |
| GET | /api/torneos/{id}/historial | Trazabilidad del torneo |

### Autorización

No hay login: quien hace la operación se envía en el header `X-Usuario-Id`.

| Operación | Quién puede |
|---|---|
| Todos los `GET` | Cualquiera (público, equipos) |
| `POST /api/usuarios` | Cualquiera |
| `POST /api/equipos`, `POST /api/torneos` | Usuario con rol ORGANIZADOR |
| `PUT`/`PATCH` de un torneo, inscripciones, `POST`/`PATCH`/`DELETE` de partidos | El ORGANIZADOR de ese torneo |

Sin header → 401. Usuario sin permiso → 403.

### Flujo de un resultado

1. `PATCH /api/partidos/{id}/estado` → `EN_JUEGO`
2. `PATCH /api/partidos/{id}/resultado` → registra; se puede repetir para **corregir** (RN-02: solo PROGRAMADO o EN_JUEGO)
3. `PATCH /api/partidos/{id}/estado` → `FINALIZADO` (exige resultado). Solo los finalizados cuentan en la tabla.

### Formato de errores

```json
{ "timestamp": "2026-11-05T10:00:00", "status": 409, "error": "Conflict",
  "mensaje": "RN-01: el equipo Águilas FC ya juega el partido 5 de 2026-12-05T10:00 a 2026-12-05T11:30",
  "ruta": "/api/partidos" }
```

## Reglas de negocio

- RN-01: un equipo no puede jugar dos partidos simultáneos.
- RN-02: un partido solo acepta resultado si está programado o en juego.
- RN-03: los goles no pueden ser negativos.
- RN-04: la tabla se calcula desde resultados, no se edita.
- RN-05: los criterios de desempate deben estar definidos.
- RN-06: un torneo cerrado no admite nuevos equipos.

## Flujo de ramas

Cada incremento va en una rama `feature/...` y se integra a `main` mediante Pull Request, después de la revisión.