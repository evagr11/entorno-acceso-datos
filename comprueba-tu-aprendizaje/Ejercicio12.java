import java.sql.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 12
 * Información sobre la BD ejercicio12.db (SQLite): nombre, driver y URL; y para cada
 * tabla (solo tablas): nombre, clave primaria, claves ajenas (importadas), claves
 * exportadas, columnas (nombre, tipo y si puede ser nula) y número de registros.
 * Cada apartado va en su propio try-catch capturando SQLException.
 *
 * Ejecución:  java Ejercicio12.java [ruta/ejercicio12.db]
 *             (por defecto usa ejercicio12.db de esta misma carpeta)
 *
 * NOTA: la ejercicio12.db de los recursos del capítulo no estaba disponible; la de esta
 * carpeta es una de ejemplo. Si tienes la del libro, pásala como argumento.
 */
public class Ejercicio12 {
    public static void main(String[] args) {
        String fichero = args.length > 0 ? args[0] : "ejercicio12.db";
        if (!new java.io.File(fichero).exists()) {
            System.out.println("No existe el fichero " + fichero);
            return;
        }
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + fichero)) {
            DatabaseMetaData dbmd = con.getMetaData();

            System.out.println("INFORMACIÓN SOBRE LA BASE DE DATOS:");
            System.out.println("===================================");
            try {
                System.out.println("Nombre : " + dbmd.getDatabaseProductName());
                System.out.println("Driver : " + dbmd.getDriverName());
                System.out.println("URL    : " + dbmd.getURL());
            } catch (SQLException e) {
                System.out.println("Error en la información de la BD: " + e.getMessage());
            }

            System.out.println();
            System.out.println("INFORMACIÓN SOBRE LAS TABLAS (Solo las tablas). Para cada tabla visualizar:");
            System.out.println("==========================================================================");
            try (ResultSet tablas = dbmd.getTables(null, null, "%", new String[] {"TABLE"})) {
                while (tablas.next()) {
                    String tabla = tablas.getString("TABLE_NAME");
                    System.out.println("Nombre : " + tabla);
                    primarias(dbmd, tabla);
                    importadas(dbmd, tabla);
                    exportadas(dbmd, tabla);
                    columnas(dbmd, tabla);
                    registros(con, tabla);
                    System.out.println("--------------------------------------------------------------");
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener las tablas: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    static void primarias(DatabaseMetaData dbmd, String tabla) {
        try (ResultSet pk = dbmd.getPrimaryKeys(null, null, tabla)) {
            StringBuilder s = new StringBuilder();
            while (pk.next()) s.append(s.length() > 0 ? " + " : "").append(pk.getString("COLUMN_NAME"));
            System.out.println("Clave(s) Primaria(s): " + (s.length() > 0 ? s : "(ninguna)"));
        } catch (SQLException e) {
            System.out.println("Clave(s) Primaria(s): error - " + e.getMessage());
        }
    }

    static void importadas(DatabaseMetaData dbmd, String tabla) {
        try (ResultSet fk = dbmd.getImportedKeys(null, null, tabla)) {
            StringBuilder s = new StringBuilder();
            while (fk.next())
                s.append(s.length() > 0 ? ", " : "").append(fk.getString("FKCOLUMN_NAME"))
                 .append(" -> ").append(fk.getString("PKTABLE_NAME")).append("(")
                 .append(fk.getString("PKCOLUMN_NAME")).append(")");
            System.out.println("Clave(s) ajena(s) (importadas): " + (s.length() > 0 ? s : "(ninguna)"));
        } catch (SQLException e) {
            System.out.println("Clave(s) ajena(s) (importadas): error - " + e.getMessage());
        }
    }

    static void exportadas(DatabaseMetaData dbmd, String tabla) {
        try (ResultSet fk = dbmd.getExportedKeys(null, null, tabla)) {
            StringBuilder s = new StringBuilder();
            while (fk.next())
                s.append(s.length() > 0 ? ", " : "").append(fk.getString("FKTABLE_NAME")).append("(")
                 .append(fk.getString("FKCOLUMN_NAME")).append(")");
            System.out.println("Clave(s) exportada(s): " + (s.length() > 0 ? s : "(ninguna)"));
        } catch (SQLException e) {
            System.out.println("Clave(s) exportada(s): error - " + e.getMessage());
        }
    }

    static void columnas(DatabaseMetaData dbmd, String tabla) {
        try (ResultSet col = dbmd.getColumns(null, null, tabla, null)) {
            System.out.println("Columnas: nombre, tipo y si puede ser nula.");
            while (col.next())
                System.out.printf("   %s, %s, %s%n", col.getString("COLUMN_NAME"),
                        col.getString("TYPE_NAME"), col.getString("IS_NULLABLE"));
        } catch (SQLException e) {
            System.out.println("Columnas: error - " + e.getMessage());
        }
    }

    static void registros(Connection con, String tabla) {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM \"" + tabla + "\"")) {
            rs.next();
            System.out.println("Número de registros que tiene: " + rs.getInt(1));
        } catch (SQLException e) {
            System.out.println("Número de registros: error - " + e.getMessage());
        }
    }
}
