import java.awt.*;
import java.sql.*;
import javax.swing.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 8
 * Pantalla gráfica para consultar la tabla departamentos (MySQL) con 4 botones:
 * Primero, Siguiente, Anterior y Último. Se usa un ResultSet desplazable
 * (TYPE_SCROLL_INSENSITIVE) ordenado por número de departamento.
 *
 * Ejecución:  java Ejercicio8.java     (se ve en el Escritorio, puerto 6080)
 */
public class Ejercicio8 extends JFrame {

    private final JTextField txtCodigo = new JTextField(5);
    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtLocalidad = new JTextField(15);
    private Connection conexion;
    private ResultSet rs;

    public Ejercicio8() {
        super("CONSULTA DE DEPARTAMENTOS");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { salir(); }
        });

        JLabel titulo = new JLabel("DATOS DE DEPARTAMENTOS", SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel datos = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.anchor = GridBagConstraints.WEST;
        String[] etiquetas = {"Código Departamento:", "Nombre Departamento:", "Localidad Departamento:"};
        JTextField[] campos = {txtCodigo, txtNombre, txtLocalidad};
        for (int i = 0; i < 3; i++) {
            g.gridx = 0; g.gridy = i; datos.add(new JLabel(etiquetas[i]), g);
            g.gridx = 1; campos[i].setEditable(false); datos.add(campos[i], g);
        }

        JButton bPrimero = new JButton("Primero");
        JButton bSiguiente = new JButton("Siguiente");
        JButton bAnterior = new JButton("Anterior");
        JButton bUltimo = new JButton("Último");
        JPanel botones = new JPanel();
        botones.add(bPrimero); botones.add(bSiguiente); botones.add(bAnterior); botones.add(bUltimo);

        bPrimero.addActionListener(e -> mover("primero"));
        bSiguiente.addActionListener(e -> mover("siguiente"));
        bAnterior.addActionListener(e -> mover("anterior"));
        bUltimo.addActionListener(e -> mover("ultimo"));

        add(titulo, BorderLayout.NORTH);
        add(datos, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);

        conectar();
        mover("primero"); // inicialmente se muestra el primer departamento
    }

    private void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");
            Statement st = conexion.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT dept_no, dnombre, loc FROM departamentos ORDER BY dept_no");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void mover(String accion) {
        try {
            boolean ok;
            switch (accion) {
                case "primero" -> ok = rs.first();
                case "ultimo" -> ok = rs.last();
                case "siguiente" -> {
                    if (rs.isLast()) { aviso("Ya estás en el último departamento."); return; }
                    ok = rs.next();
                }
                default -> {
                    if (rs.isFirst()) { aviso("Ya estás en el primer departamento."); return; }
                    ok = rs.previous();
                }
            }
            if (!ok) { aviso("No hay departamentos en la tabla."); return; }
            txtCodigo.setText(String.valueOf(rs.getInt("dept_no")));
            txtNombre.setText(rs.getString("dnombre"));
            txtLocalidad.setText(rs.getString("loc"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    private void salir() {
        try { if (conexion != null) conexion.close(); } catch (SQLException ignored) { }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ejercicio8().setVisible(true));
    }
}
