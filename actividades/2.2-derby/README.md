# Actividad 2.2 · Tablas en Apache Derby

Crea las tablas **DEPARTAMENTOS** y **EMPLEADOS** en Derby e inserta las filas del libro.

## 1. Generar la BD

```bash
cd /workspaces/entorno-acceso-datos
ij actividades/2.2-derby/actividad2_2.sql
```

La BD se crea en la carpeta `DB/DERBY/actividad2_2` (el `;create=true` del script la crea si no existe).
Al terminar, el propio script muestra las dos tablas.

## 2. Ver las tablas

- **En la terminal:** `ver-bd jdbc:derby:DB/DERBY/actividad2_2`
- **Con ij:** escribe `ij` y dentro:
  ```sql
  connect 'jdbc:derby:DB/DERBY/actividad2_2';
  SELECT * FROM empleados;
  exit;
  ```

## 3. Volver a generarla desde cero

El script no borra las tablas, así que la segunda vez da *"Table already exists"*. Borra antes la BD:

```bash
rm -rf DB/DERBY/actividad2_2 derby.log
ij actividades/2.2-derby/actividad2_2.sql
```
