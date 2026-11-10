# Actividad 2.1 · Tablas en SQLite

Crea las tablas **DEPARTAMENTOS** y **EMPLEADOS** en SQLite e inserta las filas del libro.

## 1. Generar la BD

```bash
cd /workspaces/entorno-acceso-datos
sqlite3 actividades/2.1-sqlite/actividad2_1.db < actividades/2.1-sqlite/actividad2_1.sql
```

Se crea (o se sobrescribe) el fichero `actividades/2.1-sqlite/actividad2_1.db`.
El script borra las tablas antes de crearlas, así que se puede repetir cuantas veces quieras.

## 2. Ver las tablas

- **Con el ratón:** haz clic en `actividad2_1.db` en el explorador de archivos (extensión SQLite Viewer).
- **En la terminal:** `ver-bd jdbc:sqlite:actividades/2.1-sqlite/actividad2_1.db`
- **Con sqlite3:** `sqlite3 actividades/2.1-sqlite/actividad2_1.db` y dentro `.tables`, `SELECT * FROM empleados;`, `.quit`
