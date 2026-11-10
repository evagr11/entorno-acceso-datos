# Actividad 2.5 · Db4o

Crea la BD de objetos **EMPLEDEP.YAP** con empleados y departamentos, y muestra los empleados de un departamento.

> Necesita el jar `db4o-8.0.276.16149-all-java5.jar` en `DB/jars` (arrástralo y abre una terminal nueva).

## 1. Generar la BD

```bash
cd /workspaces/entorno-acceso-datos/actividades/2.5-db4o
java Actividad2_5.java 30
```

La primera vez crea `~/DB/DB4O/EMPLEDEP.YAP` con los datos. El número es el departamento a consultar (10, 20, 30 o 40).

## 2. Ver los datos

Db4o no tiene tablas ni consola SQL: los datos se ven ejecutando el programa con otro departamento
(`java Actividad2_5.java 10`, `java Actividad2_5.java 20`...).

## 3. Volver a generarla desde cero

```bash
rm ~/DB/DB4O/EMPLEDEP.YAP
java Actividad2_5.java 30
```
