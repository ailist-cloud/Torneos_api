-- =====================================================================
-- 02_datos_prueba.sql
-- Datos mínimos para probar en Postman. Ejecutar sobre una base SIN datos
-- (los id asumen que las tablas empiezan vacías: usuarios 1-4, equipos 1-4, torneo 1).
-- =====================================================================

INSERT INTO usuario (nombre, email, rol) VALUES
    ('Laura Organizadora', 'laura@torneos.co',  'ORGANIZADOR'),    -- id 1
    ('Pedro Rep',          'pedro@torneos.co',  'REPRESENTANTE'),  -- id 2
    ('Ana Rep',            'ana@torneos.co',    'REPRESENTANTE'),  -- id 3
    ('Juan Público',       'juan@torneos.co',   'PUBLICO');        -- id 4

INSERT INTO equipo (nombre, ciudad, representante_id) VALUES
    ('Águilas FC',     'Manizales', 2),  -- id 1
    ('Búhos United',   'Pereira',   2),  -- id 2
    ('Cóndores',       'Armenia',   3),  -- id 3
    ('Delfines',       'Medellín',  3);  -- id 4

INSERT INTO torneo (nombre, deporte, fecha_inicio, fecha_fin, estado,
                    puntos_victoria, puntos_empate, puntos_derrota, organizador_id)
VALUES ('Copa Eje Cafetero 2026', 'Fútbol', '2026-11-01', '2026-12-20', 'EN_CURSO', 3, 1, 0, 1);  -- id 1

INSERT INTO criterio_desempate (torneo_id, orden, criterio) VALUES
    (1, 1, 'DIFERENCIA_GOLES'),
    (1, 2, 'GOLES_A_FAVOR');

INSERT INTO inscripcion (torneo_id, equipo_id, fecha_inscripcion) VALUES
    (1, 1, '2026-10-20'), (1, 2, '2026-10-20'), (1, 3, '2026-10-21'), (1, 4, '2026-10-21');

INSERT INTO partido (torneo_id, equipo_local_id, equipo_visitante_id, fecha_hora_inicio, duracion_min, estado, goles_local, goles_visitante) VALUES
    (1, 1, 2, '2026-11-02 10:00', 90, 'FINALIZADO', 2, 0),
    (1, 3, 4, '2026-11-02 12:00', 90, 'FINALIZADO', 3, 0),
    (1, 1, 3, '2026-11-03 10:00', 90, 'FINALIZADO', 1, 1),
    (1, 2, 4, '2026-11-03 12:00', 90, 'FINALIZADO', 1, 0),
    (1, 1, 4, '2026-12-05 10:00', 90, 'PROGRAMADO', NULL, NULL),
    (1, 2, 3, '2026-12-05 10:00', 90, 'PROGRAMADO', NULL, NULL);
