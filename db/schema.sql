-- ============================================================
--  Sistema Integral de Complejo de Cine — Esquema Ciclo 1
--  PostgreSQL 17.x
--  Ejecutar sobre una base de datos llamada "cine".
-- ============================================================

DROP TABLE IF EXISTS Reservas;
DROP TABLE IF EXISTS VentaBoletos;
DROP TABLE IF EXISTS Funciones;
DROP TABLE IF EXISTS Empleados;
DROP TABLE IF EXISTS Salas;
DROP TABLE IF EXISTS Peliculas;

CREATE TABLE Peliculas (
    idPelicula     SERIAL PRIMARY KEY,
    titulo         VARCHAR(150) NOT NULL,
    genero         VARCHAR(60)  NOT NULL,
    duracion       INT          NOT NULL,
    clasificacion  VARCHAR(20)  NOT NULL,
    estado         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE Salas (
    idSala       SERIAL PRIMARY KEY,
    numeroSala   INT          NOT NULL,
    capacidad    INT          NOT NULL,
    tipoSala     VARCHAR(40)  NOT NULL,
    estado       BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Tabla creada para satisfacer la FK de VentaBoletos.
-- El módulo de Empleados (F3) NO se implementa en el Ciclo 1.
CREATE TABLE Empleados (
    idEmpleado     SERIAL PRIMARY KEY,
    nombre         VARCHAR(120) NOT NULL,
    identificacion VARCHAR(20)  NOT NULL,
    cargo          VARCHAR(40)  NOT NULL,
    correo         VARCHAR(120) NOT NULL,
    estado         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE Funciones (
    idFuncion   SERIAL PRIMARY KEY,
    idPelicula  INT         NOT NULL REFERENCES Peliculas(idPelicula),
    idSala      INT         NOT NULL REFERENCES Salas(idSala),
    fecha       VARCHAR(20) NOT NULL,
    horario     VARCHAR(20) NOT NULL,
    estado      BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE VentaBoletos (
    idVenta     SERIAL PRIMARY KEY,
    idFuncion   INT          NOT NULL REFERENCES Funciones(idFuncion),
    idEmpleado  INT          NOT NULL REFERENCES Empleados(idEmpleado),
    asientos    VARCHAR(255) NOT NULL,
    fechaVenta  VARCHAR(20)  NOT NULL,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE Reservas (
    idReserva     SERIAL PRIMARY KEY,
    idFuncion     INT          NOT NULL REFERENCES Funciones(idFuncion),
    asientos      VARCHAR(255) NOT NULL,
    fechaReserva  VARCHAR(20)  NOT NULL,
    estado        BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ============================================================
--  DATOS DE EJEMPLO
-- ============================================================

-- Operador del sistema por defecto (idEmpleado = 1) + empleados de ejemplo.
INSERT INTO Empleados (nombre, identificacion, cargo, correo, estado) VALUES
 ('Operador del Sistema', '0000000000', 'Taquillero',  'operador@cine.local',   TRUE),
 ('María López',          '1102345678', 'Taquillera',  'maria.lopez@cine.local', TRUE),
 ('Carlos Ruiz',          '1709876543', 'Supervisor',  'carlos.ruiz@cine.local', TRUE);

INSERT INTO Peliculas (titulo, genero, duracion, clasificacion, estado) VALUES
 ('Dune: Parte Dos',        'Ciencia Ficción', 166, 'PG-13', TRUE),
 ('Intensamente 2',         'Animación',        96, 'A',     TRUE),
 ('Deadpool y Wolverine',   'Acción',          127, 'R',     TRUE),
 ('Mi Villano Favorito 4',  'Animación',        94, 'A',     TRUE),
 ('Guasón: Folie à Deux',   'Drama',           138, 'R',     TRUE),
 ('Twisters',               'Acción',          122, 'PG-13', TRUE);

INSERT INTO Salas (numeroSala, capacidad, tipoSala, estado) VALUES
 (1,  60, '2D',   TRUE),
 (2,  80, '3D',   TRUE),
 (3,  40, 'VIP',  TRUE),
 (4, 100, 'IMAX', TRUE);

INSERT INTO Funciones (idPelicula, idSala, fecha, horario, estado) VALUES
 (1, 4, '2026-06-20', '19:00', TRUE),
 (2, 1, '2026-06-20', '16:00', TRUE),
 (3, 2, '2026-06-20', '21:00', TRUE),
 (4, 1, '2026-06-21', '15:00', TRUE),
 (5, 3, '2026-06-21', '20:00', TRUE),
 (6, 4, '2026-06-22', '18:00', TRUE);

INSERT INTO VentaBoletos (idFuncion, idEmpleado, asientos, fechaVenta, estado) VALUES
 (1, 1, 'A1,A2',    '2026-06-15', TRUE),
 (1, 1, 'B5',       '2026-06-15', TRUE),
 (3, 2, 'C1,C2,C3', '2026-06-15', TRUE),
 (2, 1, 'A10',      '2026-06-15', TRUE);

INSERT INTO Reservas (idFuncion, asientos, fechaReserva, estado) VALUES
 (5, 'D1,D2', '2026-06-15', TRUE);
