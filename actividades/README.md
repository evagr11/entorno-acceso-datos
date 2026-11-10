# Actividades del Tema 2 · Manejo de conectores

Todo se ejecuta desde la terminal del Codespace. Los programas Java de una sola clase se
lanzan directamente con `java Fichero.java`, porque los conectores ya están en el `CLASSPATH`.

> Abre la terminal en la carpeta de cada actividad (`cd actividades/2.6-jdbc`, por ejemplo) o
> pon la ruta completa del fichero.

**Cada carpeta tiene su propio `README.md`** con los pasos completos: cómo generar la BD,
dónde se guarda, cómo ver las tablas y cómo volver a empezar desde cero.

Para ver las tablas de cualquier BD desde la terminal: `ver-bd <url> [usuario] [clave]`
(por ejemplo `ver-bd jdbc:hsqldb:file:DB/HSQLDB/actividad2_3/actividad2_3 SA`).

| Actividad | Qué pide | Cómo se ejecuta |
|---|---|---|
| [**2.1**](2.1-sqlite/README.md) SQLite | Crear las tablas en SQLite | Ya hecha: `2.1-sqlite/actividad2_1.db` (script `actividad2_1.sql`) |
| [**2.2**](2.2-derby/README.md) Derby | Crear EMPLEADOS y DEPARTAMENTOS en Derby e insertar filas | Desde la raíz del repo: `ij actividades/2.2-derby/actividad2_2.sql` |
| [**2.3**](2.3-hsqldb/README.md) HSQLDB | Lo mismo en HSQLDB | `java scripts/CargarSQL.java "jdbc:hsqldb:file:DB/HSQLDB/actividad2_3/actividad2_3" SA "" actividades/2.3-hsqldb/actividad2_3.sql` (o pega el script en `hsqldb-manager`) |
| [**2.4**](2.4-h2/README.md) H2 | Lo mismo en H2 | `java org.h2.tools.RunScript -url jdbc:h2:./DB/H2/actividad2_4/actividad2_4 -user sa -script actividades/2.4-h2/actividad2_4.sql` (o pégalo en `h2-console`) |
| [**2.5**](2.5-db4o/README.md) Db4o | BD EMPLEDEP.YAP con empleados y departamentos, y consultar los de un departamento | `java Actividad2_5.java 30` (necesita el jar de Db4o en `DB/jars`) |
| [**2.6**](2.6-jdbc/README.md) JDBC | Empleados del dept. 10 y el empleado con el salario máximo | `java Actividad2_6.java` |
| [**2.7**](2.7-jdbc-odbc/README.md) JDBC-ODBC | La 2.6 con el puente JDBC-ODBC | `Actividad2_7.java` solo funciona con Java 7. Versión que funciona, con ODBC desde C: `gcc actividad2_7.c -lodbc -std=gnu99 -o actividad2_7 && ./actividad2_7` |
| [**2.8**](2.8-metadatos/README.md) Metadatos | Programa DatabaseMetaData con Oracle y SQLite | `java Actividad2_8.java sqlite` (también `oracle`, `mysql`, `derby`, `hsqldb`, `h2`) |
| [**2.9**](2.9-resultsetmetadata/README.md) ResultSetMetaData | Columnas de la tabla empleados | `java Actividad2_9.java` |
| [**2.10**](2.10-insertar-empleado/README.md) Insertar | Insertar un empleado con comprobaciones | `java InsertarEmpleado.java 8000 PEREZ VENDEDOR 7698 1800 100 30` |
| [**2.11**](2.11-preparedstatement/README.md) PreparedStatement | Empleados de un departamento con media y número de empleados | `java Actividad2_11.java 30` |
| [**2.12**](2.12-funciones-procedimientos/README.md) Funciones y procedimientos | Función en Oracle y procedimiento en MySQL, creados y llamados desde Java | `java ProcedimientoMySQL.java` · `java FuncionOracle.java` (este necesita Oracle) |
| **2.13** JasperReports | Informe apaisado con número de empleados, media y suma por departamento | `cd proyecto-maven && ./ejecutar.sh Actividad2_13` |

## Comprueba tu aprendizaje (carpeta `comprueba-tu-aprendizaje`)

| Ejercicio | Cómo se ejecuta |
|---|---|
| 1 a 4 (teoría) | Respuestas en `respuestas-1-4.md` |
| 5 Tablas PRODUCTOS, CLIENTES y VENTAS | `java Ejercicio5.java 1` (1 = MySQL, 2 = Oracle, 3 = SQLite). Script SQL: `unidad2-tablas.sql` |
| 6 Insertar ventas | `java Ejercicio6.java 1 1 1 4 3` (BD, idventa, idcliente, idproducto, cantidad) |
| 7 Listado de ventas de un cliente | `java Ejercicio7.java 1 1` (BD, idcliente) |
| 8 Ventana de departamentos | `java Ejercicio8.java` (se ve en el Escritorio, puerto 6080) |
| 9 Gestión de empleados | `java Ejercicio9.java` (Escritorio, puerto 6080) |
| 10 Access + informes | `cd proyecto-maven && ./ejecutar.sh Ejercicio10`. Antes ejecuta el 5 y el 6 con MySQL |
| 11 XML con JAXB | `cd proyecto-maven && ./ejecutar.sh Ejercicio11` |
| 12 Metadatos de ejercicio12.db | `java Ejercicio12.java` |
| 13 XSD + JAXB | `cd proyecto-maven && ./ejecutar.sh Ejercicio13` (las clases se generan solas a partir de `src/main/xsd/empleydepar.xsd`) |

## Actividades de ampliación (carpeta `ampliacion`)

| Actividad | Cómo se ejecuta |
|---|---|
| 1 Modificar departamentos desde un fichero de texto | `java ModificarDepartamentos.java` (usa `departamentos.txt`, con los ejemplos del libro y algunos casos de error) |

Cuidado: este programa cambia los datos de la BD `ejemplo` de MySQL. Para volver a los
datos iniciales, ejecuta `mysql -h 127.0.0.1 -u root -proot < .devcontainer/mysql-init/01-ejemplo.sql`.

## Notas

- **Ventanas (ejercicios 8, 9 y 10, y los visores de Jasper):** se ven en el Escritorio.
  Ve a la pestaña PUERTOS, abre el puerto 6080, pulsa Connect y escribe la clave `vscode`.
- **Oracle** (2.8 con `oracle`, 2.12 `FuncionOracle` y la opción 2 de los ejercicios 5 a 7):
  el código está hecho, pero el Codespace no trae Oracle. Ejecútalo en un equipo con Oracle XE,
  con el usuario `ejemplo/ejemplo` (y `unidad2/unidad2` para los ejercicios). El conector `ojdbc11.jar` ya está en `DB/jars`.
- **Ejercicio 12:** el `ejercicio12.db` de los recursos del capítulo no estaba disponible.
  El de la carpeta es uno de ejemplo, con 5 tablas y claves ajenas. Si tienes el del libro,
  ejecuta `java Ejercicio12.java ruta/ejercicio12.db`.
- **Db4o (2.5):** cuando tengas el jar `db4o-8.0.276.16149-all-java5.jar`, arrástralo a `DB/jars`
  y abre una terminal nueva.
- **Proyecto Maven:** la primera vez que ejecutes `./ejecutar.sh` tardará un par de minutos
  en descargar JasperReports, UCanAccess y JAXB.
