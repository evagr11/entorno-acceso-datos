# Actividad 2.8 · DatabaseMetaData

Muestra la información de una base de datos (SGBD, driver, usuario, tablas...) con `DatabaseMetaData`.

## 1. Generar la BD

**No hace falta.** Usa las BD `ejemplo` que se crean solas al abrir el Codespace
(`~/DB/SQLITE/ejemplo.db`, `~/DB/DERBY/ejemplo`, `~/DB/HSQLDB/ejemplo`, `~/DB/H2/ejemplo` y MySQL `ejemplo`).
Si las has estropeado: `recrear-ejemplos.sh`.

## 2. Ejecutar

```bash
cd /workspaces/entorno-acceso-datos/actividades/2.8-metadatos
java Actividad2_8.java sqlite
```

Cambia `sqlite` por `mysql`, `derby`, `hsqldb` o `h2` (`oracle` solo en un equipo con Oracle XE).

## 3. Ver las tablas

```bash
ver-bd jdbc:sqlite:/home/vscode/DB/SQLITE/ejemplo.db
ver-bd jdbc:derby:/home/vscode/DB/DERBY/ejemplo
ver-bd jdbc:hsqldb:file:/home/vscode/DB/HSQLDB/ejemplo SA
ver-bd jdbc:h2:/home/vscode/DB/H2/ejemplo/ejemplo sa
ver-bd jdbc:mysql://localhost/ejemplo ejemplo ejemplo
```
