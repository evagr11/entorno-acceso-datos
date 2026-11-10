# Actividad 2.9 · ResultSetMetaData

Muestra la información de las columnas de la tabla empleados.

## 1. Generar la BD

**No hace falta.** Usa la BD `ejemplo` de MySQL, que se crea sola al abrir el Codespace
con las tablas departamentos y empleados del libro.

## 2. Ejecutar

```bash
cd /workspaces/entorno-acceso-datos/actividades/2.9-resultsetmetadata
java Actividad2_9.java
```

## 3. Ver las tablas

- **En la terminal:** `ver-bd jdbc:mysql://localhost/ejemplo ejemplo ejemplo`
- **Con el cliente de MySQL:** `mysql -h 127.0.0.1 -u ejemplo -pejemplo ejemplo` y dentro `SHOW TABLES;`, `SELECT * FROM empleados;`, `exit`
