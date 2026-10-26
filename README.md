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
├── Config
├── Controller
├── Domain
├── Dto
├── Repository
├── Service
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
- [ ] Módulo de Torneos: `GET /api/torneos/{id}/tabla` y `PUT /api/torneos/{id}`.
- [ ] Implementación de validación de cruces de horario (RN-01).
- [ ] Colección de pruebas HTTP exportada desde Postman.

## Cómo ejecutar

1. Crear la base `torneos_db` en PostgreSQL y ejecutar el script de tablas.
2. Definir la contraseña como variable de entorno (no se guarda en el repositorio):

```
$env:DB_PASSWORD="tu_contraseña"
```

3. Ejecutar:

```
.\mvnw.cmd spring-boot:run
```

4. Probar: `http://localhost:8080/api/estado`

## Contrato HTTP mínimo

| Verbo | Ruta | Responsabilidad |
|---|---|---|
| GET | /api/torneos/{id}/tabla | Calcula posiciones |
| GET | /api/partidos | Consulta programación |
| POST | /api/partidos | Programa partido sin cruces |
| PUT | /api/torneos/{id} | Reemplaza configuración antes de iniciar |
| PATCH | /api/partidos/{id}/resultado | Registra o corrige resultado |
| DELETE | /api/partidos/{id} | Elimina partido futuro sin resultado |

## Reglas de negocio

- RN-01: un equipo no puede jugar dos partidos simultáneos.
- RN-02: un partido solo acepta resultado si está programado o en juego.
- RN-03: los goles no pueden ser negativos.
- RN-04: la tabla se calcula desde resultados, no se edita.
- RN-05: los criterios de desempate deben estar definidos.
- RN-06: un torneo cerrado no admite nuevos equipos.

## Flujo de ramas

Cada incremento va en una rama `feature/...` y se integra a `main` mediante Pull Request, después de la revisión.