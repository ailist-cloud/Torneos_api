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
Entidades: Usuario, Torneo, CriterioDesempate, Equipo, Inscripcion y Partido. La tabla de posiciones no es una tabla: se calcula desde los resultados.

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

## Cómo ejecutar

1. Crear la base `torneos_db` en PostgreSQL y, en pgAdmin (Query Tool):
   - Base nueva: ejecutar `docs/00_crear_tablas.sql` y luego `docs/02_datos_prueba.sql`.
   - Base que ya tiene tablas: ejecutar `docs/01_arreglar_bd_existente.sql` (vuelve autoincrementales los `id` y pasa los estados a MAYÚSCULAS).
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
| PATCH | /api/partidos/{id}/estado | PROGRAMADO → EN_JUEGO / CANCELADO |

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