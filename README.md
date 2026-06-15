# Sistema Integral de Complejo de Cine — Ciclo 1

Aplicación de escritorio en **Java + Swing** con base de datos **PostgreSQL**, construida sobre una arquitectura desacoplada de tres capas (GUI → DP → MD). Implementa los módulos del **Ciclo 1**: Películas (F1), Salas (F2), Cartelera (F4) y Venta de Boletos (F5). Los módulos Empleados (F3) y Reservas (F6) aparecen en el menú pero están deshabilitados (Ciclo 2).

## Requisitos
- Java 21 o superior (JDK).
- PostgreSQL 16 o 17.
- Driver JDBC incluido en `lib/postgresql-42.7.2.jar`.

## 1. Preparar la base de datos
```bash
createdb -U postgres cine
psql -U postgres -d cine -f db/schema.sql
```
Ajusta las credenciales en `db.properties` si difieren:
```properties
db.url=jdbc:postgresql://localhost:5432/cine
db.user=postgres
db.password=postgres
```

## 2. Compilar y ejecutar
**Linux / macOS**
```bash
./run.sh
```
**Windows**
```bat
run.bat
```
Manualmente:
```bash
javac -d out $(find src -name "*.java")
java -cp "out:lib/postgresql-42.7.2.jar" cine.main.Main
```
> `db.properties` debe estar en el directorio desde el que se ejecuta la aplicación.

## Estructura
```
src/cine/main   -> Main (arranque)
src/cine/gui    -> MenuPrincipal + ventanas (Películas, Salas, Cartelera, Venta)
src/cine/dp     -> Lógica de negocio / modelo (Pelicula, Sala, Funcion, VentaBoleto)
src/cine/md     -> Acceso a datos JDBC (PeliculaMD, SalaMD, FuncionMD, VentaBoletoMD)
src/cine/util   -> Conexion (PostgreSQL) y UI (estilos)
db/schema.sql   -> Esquema de 6 tablas + datos de ejemplo
lib/            -> Driver JDBC de PostgreSQL
```

## Datos de ejemplo incluidos
- 6 películas, 4 salas (2D, 3D, VIP, IMAX), 6 funciones, 4 ventas y 1 reserva.
- Empleado operador por defecto (idEmpleado = 1), usado por la venta de boletos en el Ciclo 1.

## Funcionalidad
- **Películas / Salas:** alta, edición, eliminación (con verificación de dependencias) y consultas (general y por parámetro).
- **Cartelera:** programación de funciones con verificación de cruces de sala/horario; cancelación con control de ventas/reservas.
- **Venta de Boletos:** mapa de asientos interactivo (libre / seleccionado / ocupado), registro de venta y anulación que libera los asientos.

## Notas de coherencia con el diseño
- Orden de operación: el usuario presiona **Guardar**, el sistema **valida**, **verifica** (duplicado/disponibilidad) y **solo entonces persiste**.
- Toda la lógica de negocio vive en la capa DP; todo el SQL vive en la capa MD; la GUI no contiene SQL.
- La venta usa un operador por defecto porque el módulo de Empleados pertenece al Ciclo 2.
