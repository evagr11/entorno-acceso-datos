import java.sql.*;

/**
 * ACTIVIDAD 2.8
 * Prueba el programa EjemploDatabaseMetadata (pág. 98) para visualizar información de
 * las bases de datos Oracle y SQLite con las que estás trabajando en este tema.
 *
 * Ejecución:  java Actividad2_8.java <sqlite | oracle | mysql | derby | hsqldb | h2>
 *             (sin argumento usa sqlite)
 */
public class Actividad2_8 {

    static final String HOME = System.getProperty("user.home");

    public static void main(String[] args) {
        String bd = args.length > 0 ? args[0].toLowerCase() : "sqlite";
        String url, usuario = "", clave = "", catalogo = null, esquema = null;

        switch (bd) {
            case "sqlite" -> url = "jdbc:sqlite:" + HOME + "/DB/SQLITE/ejemplo.db";
            case "oracle" -> { url = "jdbc:oracle:thin:@localhost:1521:XE";
                               usuario = "ejemplo"; clave = "ejemplo";
                               esquema = "EJEMPLO"; } // en Oracle el esquema es el usuario (en mayúsculas)
            case "mysql"  -> { url = "jdbc:mysql://localhost/ejemplo";
                               usuario = "ejemplo"; clave = "ejemplo"; catalogo = "ejemplo"; }
            case "derby"  -> { url = "jdbc:derby:" + HOME + "/DB/DERBY/ejemplo"; esquema = "APP"; }
            case "hsqldb" -> { url = "jdbc:hsqldb:file:" + HOME + "/DB/HSQLDB/ejemplo";
                               usuario = "SA"; esquema = "PUBLIC"; }
            case "h2"     -> { url = "jdbc:h2:" + HOME + "/DB/H2/ejemplo/ejemplo";
                               usuario = "sa"; esquema = "PUBLIC"; }
            default -> { System.out.println("Base de datos no válida: " + bd); return; }
        }

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
            DatabaseMetaData dbmd = conexion.getMetaData();

            String nombre = dbmd.getDatabaseProductName();
            String driver = dbmd.getDriverName();
            String urlBD = dbmd.getURL();
            String user = dbmd.getUserName();

            System.out.println("INFORMACIÓN SOBRE LA BASE DE DATOS:");
            System.out.println("===================================");
            System.out.printf("Nombre : %s %n", nombre);
            System.out.printf("Driver : %s %n", driver);
            System.out.printf("URL    : %s %n", urlBD);
            System.out.printf("Usuario: %s %n", user);

            // Obtener información de las tablas y vistas que hay
            ResultSet resul = dbmd.getTables(catalogo, esquema, null, new String[] {"TABLE", "VIEW"});
            while (resul.next()) {
                String cat = resul.getString(1);   // columna 1
                String esq = resul.getString(2);   // columna 2
                String tabla = resul.getString(3); // columna 3
                String tipo = resul.getString(4);  // columna 4
                System.out.printf("%s - Catalogo: %s, Esquema: %s, Nombre: %s %n",
                        tipo, cat, esq, tabla);
            }
            resul.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
