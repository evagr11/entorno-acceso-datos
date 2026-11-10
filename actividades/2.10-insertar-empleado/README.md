# Actividad 2.10 · Insertar un empleado

Inserta un empleado en la tabla empleados comprobando que el departamento y el director existen,
que el número no está repetido y que el salario es mayor que 0.

## 1. Generar la BD

**No hace falta.** Inserta en la BD `ejemplo` de MySQL, que se crea sola al abrir el Codespace.

## 2. Ejecutar

```bash
cd /workspaces/entorno-acceso-datos/actividades/2.10-insertar-empleado
java InsertarEmpleado.java 8000 PEREZ VENDEDOR 7698 1800 100 30
```

Orden de los datos: EMP_NO APELLIDO OFICIO DIR SALARIO COMISION DEPT_NO.
Si lo ejecutas dos veces con el mismo número, la segunda dice *"número de empleado duplicado"* (es una de las comprobaciones).

## 3. Ver las tablas

- **En la terminal:** `ver-bd jdbc:mysql://localhost/ejemplo ejemplo ejemplo`
- **Solo el empleado nuevo:** `mysql -h 127.0.0.1 -u ejemplo -pejemplo ejemplo -e "SELECT * FROM empleados WHERE emp_no = 8000"`

## 4. Dejar la tabla como al principio

```bash
mysql -h 127.0.0.1 -u root -proot < .devcontainer/mysql-init/01-ejemplo.sql
```
(desde `/workspaces/entorno-acceso-datos`)
