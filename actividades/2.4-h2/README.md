# Actividad 2.4 · Tablas en H2

Crea las tablas **DEPARTAMENTOS** y **EMPLEADOS** en H2 e inserta las filas del libro.

## 1. Generar la BD

```bash
cd /workspaces/entorno-acceso-datos
java org.h2.tools.RunScript -url jdbc:h2:./DB/H2/actividad2_4/actividad2_4 -user sa -script actividades/2.4-h2/actividad2_4.sql
```

Se crea el fichero `DB/H2/actividad2_4/actividad2_4.mv.db`.
El script borra las tablas antes de crearlas, así que se puede repetir.

## 2. Ver las tablas

- **En la terminal:** `ver-bd jdbc:h2:./DB/H2/actividad2_4/actividad2_4 sa`
- **Con la consola web de H2 (como en el libro):** ejecuta `h2-console`, se abre en el navegador (puerto 8082) y pon:
  - JDBC URL: `jdbc:h2:~/DB/H2/actividad2_4/actividad2_4`
  - Usuario: `sa`, sin contraseña

  Si escribes mal la URL, la consola crea una BD vacía nueva (por eso no verías tablas).
  Para la consola con `Ctrl+C` antes de volver a generar la BD.
