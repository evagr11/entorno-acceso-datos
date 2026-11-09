# Comprueba tu aprendizaje · Ejercicios 1 a 4

## 1. Librería Java necesaria para cada base de datos

| Base de datos | Librería Java necesaria para la conexión |
|---|---|
| SQLite | `sqlite-jdbc-3.8.11.2.jar` (en este entorno: `sqlite-jdbc.jar`) |
| Apache Derby | `derby.jar` (desde Derby 10.15 también `derbyshared.jar`; para la consola `ij`, `derbytools.jar`) |
| HSQLDB | `hsqldb.jar` |
| H2 | `h2-1.4.191.jar` (en este entorno: `h2.jar`) |
| MySQL | `mysql-connector-java-5.1.38-bin.jar` (actualmente `mysql-connector-j-9.x.jar`) |
| ORACLE | `ojdbc6.jar` (actualmente `ojdbc11.jar`) |
| Db4o | `db4o-8.0.276.16149-all-java5.jar` |

## 2. Driver y URL de conexión

| Base de datos | Driver / URL |
|---|---|
| SQLite | `org.sqlite.JDBC` · `jdbc:sqlite:D:/DB/SQLITE/ejemplo.db` |
| Apache Derby | `org.apache.derby.jdbc.EmbeddedDriver` · `jdbc:derby:D:/DB/DERBY/ejemplo` |
| HSQLDB | `org.hsqldb.jdbcDriver` · `jdbc:hsqldb:file:D:/DB/HSQLDB/ejemplo/ejemplo` |
| H2 | `org.h2.Driver` · `jdbc:h2:D:/DB/H2/ejemplo/ejemplo` (usuario `sa`, clave vacía) |
| MySQL | `com.mysql.jdbc.Driver` (actual: `com.mysql.cj.jdbc.Driver`) · `jdbc:mysql://localhost/ejemplo` |
| ORACLE | `oracle.jdbc.driver.OracleDriver` · `jdbc:oracle:thin:@localhost:1521:XE` |

En Linux solo cambia la ruta: por ejemplo `jdbc:sqlite:/home/usuario/DB/SQLITE/ejemplo.db`.

## 3. Afirmación sobre JDBC que NO es correcta

**La d)**: *"Los tipos de conectores 3 y 4 se usan normalmente cuando el único sistema de acceso
final al gestor de bases de datos es ODBC"*. Es al revés: los que se usan cuando solo hay ODBC
son los **tipos 1 y 2**. Los tipos 3 y 4 son la mejor forma de acceder mediante JDBC.

> La a) también es discutible: JDBC está pensado para bases de datos **relacionales**, no
> orientadas a objetos. Si solo se puede marcar una, la respuesta esperada es la d).

## 4. ¿Es correcto el código?

**No compila.** `Class.forName()` puede lanzar `ClassNotFoundException`, y `getConnection()`,
`createStatement()`, `executeQuery()`, `next()`, `getInt()`, `getString()` y `close()` pueden
lanzar `SQLException`. Las dos son excepciones comprobadas (*checked*), así que hay que
capturarlas con `try-catch` o declararlas en `main` con `throws`.

Versión corregida:

```java
import java.sql.*;

public class Ejercicio4 {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");
            Statement sentencia = conexion.createStatement();
            ResultSet resul = sentencia.executeQuery("SELECT * FROM empleados");
            while (resul.next()) {
                System.out.printf("%d, %s %n",
                        resul.getInt("EMP_NO"), resul.getString("APELLIDO"));
            }
            resul.close();
            sentencia.close();
            conexion.close();
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

Los nombres de columna en mayúsculas (`"EMP_NO"`, `"APELLIDO"`) no son un problema: en MySQL
no distinguen mayúsculas de minúsculas.
