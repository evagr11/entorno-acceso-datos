# Actividad 2.3 · Tablas en HSQLDB

Crea las tablas **DEPARTAMENTOS** y **EMPLEADOS** en HSQLDB e inserta las filas del libro.

## 1. Generar la BD

```bash
cd /workspaces/entorno-acceso-datos
java scripts/CargarSQL.java "jdbc:hsqldb:file:DB/HSQLDB/actividad2_3/actividad2_3" SA "" actividades/2.3-hsqldb/actividad2_3.sql
```

Se crean los ficheros `DB/HSQLDB/actividad2_3/actividad2_3.properties` y `actividad2_3.script`.
El script borra las tablas antes de crearlas, así que se puede repetir.

## 2. Ver las tablas

- **En la terminal:** `ver-bd jdbc:hsqldb:file:DB/HSQLDB/actividad2_3/actividad2_3 SA`
- **El fichero de la BD:** abre `DB/HSQLDB/actividad2_3/actividad2_3.script`. HSQLDB guarda ahí, en texto, los `CREATE TABLE` y los `INSERT`.
- **Con la ventana de HSQLDB (como en el libro):**
  ```bash
  hsqldb-manager --url jdbc:hsqldb:file:/home/vscode/DB/HSQLDB/actividad2_3/actividad2_3 --user SA
  ```
  Se ve en el Escritorio: pestaña PUERTOS → 6080 → Connect → clave `vscode`.
  Ciérrala (File → Exit) antes de volver a generar la BD, porque la deja bloqueada.
