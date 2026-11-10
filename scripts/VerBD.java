import java.sql.*;
import java.util.Set;

/**
 * Muestra todas las tablas de una base de datos JDBC (SQLite, Derby, HSQLDB, H2, MySQL)
 * con sus columnas y sus filas.
 *
 * Uso: ver-bd <url> [usuario] [clave]
 *      (o java scripts/VerBD.java <url> [usuario] [clave])
 */
public class VerBD {

    // Esquemas internos de los SGBD que no interesa mostrar
    static final Set<String> ESQUEMAS_SISTEMA = Set.of(
            "INFORMATION_SCHEMA", "SYSTEM_LOBS", "SYS", "SYSIBM", "SYSCAT", "SYSCS_DIAG",
            "SYSCS_UTIL", "SYSFUN", "SYSPROC", "SYSSTAT", "SQLJ", "NULLID");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: ver-bd <url> [usuario] [clave]");
            System.out.println("Ej.: ver-bd jdbc:hsqldb:file:DB/HSQLDB/actividad2_3/actividad2_3 SA");
            return;
        }
        String url = args[0];
        String usuario = args.length > 1 ? args[1] : "";
        String clave = args.length > 2 ? args[2] : "";

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
            DatabaseMetaData dbmd = conexion.getMetaData();
            System.out.println("SGBD: " + dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion());
            System.out.println("URL:  " + url);

            // En MySQL hay que indicar la BD (catálogo); si no, salen las tablas de todas
            String catalogo = url.startsWith("jdbc:mysql") ? conexion.getCatalog() : null;

            int numTablas = 0;
            try (ResultSet tablas = dbmd.getTables(catalogo, null, "%", null)) {
                while (tablas.next()) {
                    String tipo = tablas.getString("TABLE_TYPE");
                    String esquema = tablas.getString("TABLE_SCHEM");
                    String nombre = tablas.getString("TABLE_NAME");
                    if (tipo == null || !tipo.toUpperCase().contains("TABLE") || tipo.toUpperCase().contains("SYSTEM")) continue;
                    if (esquema != null && ESQUEMAS_SISTEMA.contains(esquema.toUpperCase())) continue;
                    if (nombre.toLowerCase().startsWith("sqlite_")) continue;

                    mostrarTabla(conexion, esquema != null ? esquema + "." + nombre : nombre);
                    numTablas++;
                }
            }
            if (numTablas == 0) System.out.println("\nLa base de datos no tiene tablas (¿la URL es la correcta?).");

            // HSQLDB en modo fichero: SHUTDOWN para cerrarla bien y liberar el bloqueo
            if (url.startsWith("jdbc:hsqldb:file:")) {
                try (Statement s = conexion.createStatement()) { s.execute("SHUTDOWN"); }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (url.startsWith("jdbc:derby:")) {
            try { DriverManager.getConnection("jdbc:derby:;shutdown=true"); } catch (SQLException ignorada) { }
            new java.io.File("derby.log").delete();
        }
    }

    static void mostrarTabla(Connection conexion, String tabla) throws SQLException {
        try (Statement sentencia = conexion.createStatement();
             ResultSet rs = sentencia.executeQuery("SELECT * FROM " + tabla)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnas = rsmd.getColumnCount();

            System.out.println("\n=== " + tabla + " ===");
            StringBuilder cabecera = new StringBuilder();
            for (int i = 1; i <= columnas; i++) {
                cabecera.append(String.format("%-14s ", rsmd.getColumnName(i)));
            }
            System.out.println(cabecera);
            System.out.println("-".repeat(cabecera.length()));

            int filas = 0;
            while (rs.next()) {
                StringBuilder fila = new StringBuilder();
                for (int i = 1; i <= columnas; i++) {
                    fila.append(String.format("%-14s ", rs.getString(i)));
                }
                System.out.println(fila);
                filas++;
            }
            System.out.println("(" + filas + " filas)");
        }
    }
}
