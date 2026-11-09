import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;

import net.ucanaccess.jdbc.UcanaccessDriver;
import net.ucanaccess.jdbc.UcanaccessSQLException;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 10
 * Pantalla gráfica para:
 *   - Crear las tablas PRODUCTOS, CLIENTES y VENTAS en la BD Access ejemplo.accdb
 *     (si no existe la BD se crea). Si ya existen se indica.
 *   - Llenar las tablas con los datos de las tablas de MySQL (BD unidad2). Si un registro
 *     ya está insertado se muestra el código y el mensaje de error.
 *   - Informe de ventas e informe de clientes con JasperReports.
 * Los mensajes se muestran en el área de texto.
 *
 * Antes: crea y llena las tablas de MySQL con  java Ejercicio5.java 1  e inserta ventas
 * con Ejercicio6.java (carpeta comprueba-tu-aprendizaje).
 *
 * Ejecución:  ./ejecutar.sh Ejercicio10     (se ve en el Escritorio, puerto 6080)
 */
public class Ejercicio10 extends JFrame {

    static final String BD_ACCESS = "ejemplo.accdb";
    private final JTextArea area = new JTextArea(14, 50);

    public Ejercicio10() {
        super("Ejercicio 10");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Ejercicio 10. Creación de tablas en Access e Informes");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton bCrear = new JButton("Crear Tablas en Access");
        JButton bLlenar = new JButton("Llenar tablas con datos MySQL");
        JButton bVentas = new JButton("Informe Ventas");
        JButton bClientes = new JButton("Informe Clientes");
        JPanel botones = new JPanel(new GridLayout(4, 1, 8, 12));
        botones.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        botones.add(bCrear); botones.add(bLlenar); botones.add(bVentas); botones.add(bClientes);

        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel pMensajes = new JPanel(new BorderLayout());
        pMensajes.add(new JLabel("Mensajes"), BorderLayout.NORTH);
        pMensajes.add(new JScrollPane(area), BorderLayout.CENTER);
        pMensajes.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));

        add(titulo, BorderLayout.NORTH);
        add(botones, BorderLayout.WEST);
        add(pMensajes, BorderLayout.CENTER);

        bCrear.addActionListener(e -> crearTablas());
        bLlenar.addActionListener(e -> llenarTablas());
        bVentas.addActionListener(e -> informe("./plantillas/informe-ventas.jrxml", "InformeVentas",
                "RESUMEN DATOS DE VENTAS."));
        bClientes.addActionListener(e -> informe("./plantillas/informe-clientes.jrxml", "InformeClientes",
                "RESUMEN DATOS DE CLIENTES."));

        pack();
        setLocationRelativeTo(null);
    }

    private void log(String s) {
        area.append(s + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    // ---------------------------------------------------------------- Access
    /** Crea la BD Access si no existe (método del libro, pág. 139) */
    public static boolean crearbd(String nombre) {
        String url = UcanaccessDriver.URL_PREFIX + nombre + ";newDatabaseVersion=V2007";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            Connection conn = DriverManager.getConnection(url);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Base de datos " + nombre + " creada");
        return true;
    }

    private Connection conectarAccess() throws SQLException {
        if (!new File(BD_ACCESS).exists()) {
            if (crearbd(BD_ACCESS)) log("Base de datos " + BD_ACCESS + " creada.");
            else throw new SQLException("No se pudo crear la BD " + BD_ACCESS);
        }
        return DriverManager.getConnection(UcanaccessDriver.URL_PREFIX + BD_ACCESS);
    }

    private void crearTablas() {
        String[][] tablas = {
            {"PRODUCTOS", "CREATE TABLE PRODUCTOS (ID Integer PRIMARY KEY, DESCRIPCION Text(50) NOT NULL, "
                        + "STOCKACTUAL Integer, STOCKMINIMO Integer, PVP Float)"},
            {"CLIENTES", "CREATE TABLE CLIENTES (ID Integer PRIMARY KEY, NOMBRE Text(50) NOT NULL, "
                       + "DIRECCION Text(50), POBLACION Text(50), TELEF Text(20), NIF Text(10))"},
            {"VENTAS", "CREATE TABLE VENTAS (IDVENTA Integer PRIMARY KEY, FECHAVENTA Datetime NOT NULL, "
                     + "IDCLIENTE Integer REFERENCES CLIENTES(ID), "
                     + "IDPRODUCTO Integer REFERENCES PRODUCTOS(ID), CANTIDAD Integer)"},
        };
        try (Connection con = conectarAccess(); Statement st = con.createStatement()) {
            for (String[] t : tablas) {
                try {
                    st.executeUpdate(t[1]);
                    log("Tabla " + t[0] + " creada.");
                } catch (UcanaccessSQLException e) {
                    log("Tabla " + t[0] + " no creada (ya existe). Código: " + e.getErrorCode()
                        + " - " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            log("ERROR: " + e.getMessage());
        }
    }

    private void llenarTablas() {
        try (Connection mysql = DriverManager.getConnection(
                     "jdbc:mysql://localhost/unidad2", "unidad2", "unidad2");
             Connection access = conectarAccess()) {
            copiar(mysql, access, "PRODUCTOS", "ID, DESCRIPCION, STOCKACTUAL, STOCKMINIMO, PVP", 5);
            copiar(mysql, access, "CLIENTES", "ID, NOMBRE, DIRECCION, POBLACION, TELEF, NIF", 6);
            copiar(mysql, access, "VENTAS", "IDVENTA, FECHAVENTA, IDCLIENTE, IDPRODUCTO, CANTIDAD", 5);
        } catch (SQLException e) {
            log("ERROR: " + e.getMessage());
        }
    }

    /** Lee una tabla de MySQL y guarda sus filas en la tabla del mismo nombre de Access */
    private void copiar(Connection origen, Connection destino, String tabla, String columnas, int n)
            throws SQLException {
        String marcas = "?" + ", ?".repeat(n - 1);
        int insertadas = 0;
        try (Statement st = origen.createStatement();
             ResultSet rs = st.executeQuery("SELECT " + columnas + " FROM " + tabla);
             PreparedStatement ps = destino.prepareStatement(
                     "INSERT INTO " + tabla + " (" + columnas + ") VALUES (" + marcas + ")")) {
            while (rs.next()) {
                for (int i = 1; i <= n; i++) ps.setObject(i, rs.getObject(i));
                try {
                    ps.executeUpdate();
                    insertadas++;
                } catch (SQLException e) {
                    log(tabla + " " + rs.getObject(1) + ": código " + e.getErrorCode() + " - " + e.getMessage());
                }
            }
        }
        log("Tabla " + tabla + ": " + insertadas + " registros insertados.");
    }

    private void informe(String plantilla, String salida, String titulo) {
        try (Connection con = conectarAccess()) {
            Informes.generar(plantilla, salida, Informes.parametros(titulo), con, false);
            log("Informe generado en informes/" + salida + ".pdf");
        } catch (Exception e) {
            log("ERROR al generar el informe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ejercicio10().setVisible(true));
    }
}
