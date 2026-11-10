# Actividad 2.7 · Puente JDBC-ODBC

La actividad 2.6 usando el puente JDBC-ODBC.

## 1. Generar la BD

**No hace falta.** Usa la BD `ejemplo` de MySQL a través del origen de datos ODBC `Mysql-odbc`, que se prepara solo al abrir el Codespace.

## 2. Ejecutar

`Actividad2_7.java` solo funciona con Java 7 (el puente se eliminó en Java 8, como avisa el libro en la pág. 95).
La versión que funciona en el Codespace es la de C con ODBC:

```bash
cd /workspaces/entorno-acceso-datos/actividades/2.7-jdbc-odbc
gcc actividad2_7.c -lodbc -std=gnu99 -o actividad2_7 && ./actividad2_7
```

## 3. Ver las tablas

- **Por ODBC:** `isql Mysql-odbc` y dentro `SELECT * FROM empleados;` (sal con `quit`)
- **En la terminal:** `ver-bd jdbc:mysql://localhost/ejemplo ejemplo ejemplo`
