import java.sql.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 5
 * Crea las tablas PRODUCTOS, CLIENTES y VENTAS en MySQL, Oracle y SQLite:
 *   - MySQL:  base de datos UNIDAD2, usuario y clave unidad2.
 *   - Oracle: usuario de base de datos UNIDAD2 con clave unidad2.
 *   - SQLite: fichero UNIDAD2.DB (en ~/DB/SQLITE).
 * Después llena las tablas PRODUCTOS y CLIENTES con los datos definidos en el programa.
 * Recibe un argumento: 1 = MySQL, 2 = Oracle, 3 = SQLite. Al final visualiza los datos
 * insertados y el número de filas insertadas en cada tabla.
 *
 * Ejecución:  java Ejercicio5.java 1      (o 2 o 3)
 */
public class Ejercicio5 {

    // ------------------------- Datos a insertar -------------------------
    static final Object[][] PRODUCTOS = {
        // ID, DESCRIPCION, STOCKACTUAL, STOCKMINIMO, PVP
        {1, "Portátil Acer 15",       10, 3, 549.99},
        {2, "Ratón inalámbrico",      50, 10, 15.50},
        {3, "Teclado mecánico",       25, 5, 69.90},
        {4, "Diccionario María Moliner", 40, 5, 45.00},
        {5, "Impresora HP DeskJet",   12, 4, 89.00},
        {6, "Pen Drive 64 GB",        80, 20, 9.95},
        {7, "Monitor 24 pulgadas",    15, 3, 139.00},
    };
    static final Object[][] CLIENTES = {
        // ID, NOMBRE, DIRECCION, POBLACION, TELEF, NIF
        {1, "MARÍA SERRANO", "C/ Mayor 12",      "Guadalajara", "949111222", "11111111A"},
        {2, "PEDRO BRAVO",   "Avda. España 5",   "Madrid",      "911222333", "22222222B"},
        {3, "MANUEL SERRA",  "C/ Luna 3",        "Guadalajara", "949333444", "33333333C"},
        {4, "ALICIA PÉREZ",  "Plaza del Sol 1",  "Talavera",    "925444555", "44444444D"},
        {5, "LUIS GARCÍA",   "C/ Río Tajo 20",   "Toledo",      "925555666", "55555555E"},
    };

    // ------------------------- Conexión según la BD -------------------------
    static Connection conectar(int bd) throws SQLException {
        switch (bd) {
            case 1: return DriverManager.getConnection("jdbc:mysql://localhost/unidad2", "unidad2", "unidad2");
            case 2: return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "unidad2", "unidad2");
            case 3: return DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.home")
                                                       + "/DB/SQLITE/UNIDAD2.DB");
            default: throw new IllegalArgumentException("Base de datos no válida");
        }
    }
    static String nombreBD(int bd) {
        return bd == 1 ? "MySQL" : bd == 2 ? "ORACLE" : "SQLite";
    }

    public static void main(String[] args) {
        int bd;
        try {
            bd = Integer.parseInt(args[0]);
            if (bd < 1 || bd > 3) throw new NumberFormatException();
        } catch (Exception e) {
            System.out.println("Uso: java Ejercicio5.java <1=MySQL | 2=Oracle | 3=SQLite>");
            return;
        }

        try (Connection con = conectar(bd)) {
            System.out.println("Conectado a " + nombreBD(bd));
            crearTablas(con);

            int filasProd = 0, filasCli = 0;
            String sqlProd = "INSERT INTO PRODUCTOS (ID, DESCRIPCION, STOCKACTUAL, STOCKMINIMO, PVP) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlProd)) {
                for (Object[] p : PRODUCTOS) {
                    ps.setInt(1, (Integer) p[0]);
                    ps.setString(2, (String) p[1]);
                    ps.setInt(3, (Integer) p[2]);
                    ps.setInt(4, (Integer) p[3]);
                    ps.setDouble(5, (Double) p[4]);
                    filasProd += insertar(ps, "PRODUCTOS", p[0]);
                }
            }
            String sqlCli = "INSERT INTO CLIENTES (ID, NOMBRE, DIRECCION, POBLACION, TELEF, NIF) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlCli)) {
                for (Object[] c : CLIENTES) {
                    ps.setInt(1, (Integer) c[0]);
                    for (int i = 1; i <= 5; i++) ps.setString(i + 1, (String) c[i]);
                    filasCli += insertar(ps, "CLIENTES", c[0]);
                }
            }

            mostrar(con, "PRODUCTOS");
            mostrar(con, "CLIENTES");
            System.out.println("Filas insertadas en PRODUCTOS: " + filasProd);
            System.out.println("Filas insertadas en CLIENTES:  " + filasCli);
        } catch (SQLException e) {
            System.out.println("ERROR (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }

    /** Crea las tres tablas; si ya existen lo indica y continúa */
    static void crearTablas(Connection con) {
        String[] ddl = {
            "CREATE TABLE PRODUCTOS (" +
            "  ID INTEGER PRIMARY KEY," +
            "  DESCRIPCION VARCHAR(50) NOT NULL," +
            "  STOCKACTUAL INTEGER," +
            "  STOCKMINIMO INTEGER," +
            "  PVP FLOAT)",
            "CREATE TABLE CLIENTES (" +
            "  ID INTEGER PRIMARY KEY," +
            "  NOMBRE VARCHAR(50) NOT NULL," +
            "  DIRECCION VARCHAR(50)," +
            "  POBLACION VARCHAR(50)," +
            "  TELEF VARCHAR(20)," +
            "  NIF VARCHAR(10))",
            "CREATE TABLE VENTAS (" +
            "  IDVENTA INTEGER PRIMARY KEY," +
            "  FECHAVENTA DATE NOT NULL," +
            "  IDCLIENTE INTEGER," +
            "  IDPRODUCTO INTEGER," +
            "  CANTIDAD INTEGER," +
            "  CONSTRAINT FK_VENTAS_CLI FOREIGN KEY (IDCLIENTE) REFERENCES CLIENTES(ID)," +
            "  CONSTRAINT FK_VENTAS_PRO FOREIGN KEY (IDPRODUCTO) REFERENCES PRODUCTOS(ID))"
        };
        String[] nombres = {"PRODUCTOS", "CLIENTES", "VENTAS"};
        for (int i = 0; i < ddl.length; i++) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate(ddl[i]);
                System.out.println("Tabla " + nombres[i] + " creada.");
            } catch (SQLException e) {
                System.out.println("Tabla " + nombres[i] + " no creada: " + e.getMessage().trim());
            }
        }
    }

    /** Ejecuta un INSERT; si la fila ya existía lo indica y devuelve 0 */
    static int insertar(PreparedStatement ps, String tabla, Object id) {
        try {
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("No se inserta en " + tabla + " el ID " + id + ": " + e.getMessage().trim());
            return 0;
        }
    }

    /** Visualiza todas las filas de una tabla */
    static void mostrar(Connection con, String tabla) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tabla + " ORDER BY ID")) {
            ResultSetMetaData md = rs.getMetaData();
            System.out.println("\n---- " + tabla + " ----");
            while (rs.next()) {
                StringBuilder fila = new StringBuilder();
                for (int i = 1; i <= md.getColumnCount(); i++)
                    fila.append(i > 1 ? ", " : "").append(rs.getString(i));
                System.out.println(fila);
            }
        }
    }
}
