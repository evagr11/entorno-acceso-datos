# Actividad 2.12 · Funciones y procedimientos

Crea desde Java el procedimiento `datos_salario` en MySQL (y la función `salario_medio` en Oracle) y los llama con `CallableStatement`.

## 1. Generar la BD

**No hace falta.** Usa la BD `ejemplo` de MySQL. El propio programa borra y vuelve a crear el procedimiento cada vez.

## 2. Ejecutar

```bash
cd /workspaces/entorno-acceso-datos/actividades/2.12-funciones-procedimientos
java ProcedimientoMySQL.java
```

`FuncionOracle.java` necesita Oracle XE, que no está en el Codespace.

## 3. Ver el procedimiento y las tablas

```bash
mysql -h 127.0.0.1 -u ejemplo -pejemplo ejemplo -e "SHOW CREATE PROCEDURE datos_salario\G"
ver-bd jdbc:mysql://localhost/ejemplo ejemplo ejemplo
```
