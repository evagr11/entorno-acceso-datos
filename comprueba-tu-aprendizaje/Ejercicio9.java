import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 9
 * Gestión de la tabla empleados (MySQL) con pantalla gráfica: consultar, insertar,
 * modificar y eliminar. El departamento y el director se eligen de listas.
 *   - Insertar: todos los campos obligatorios salvo la comisión (si no, 0). La fecha de
 *     alta por defecto es la del sistema. Al insertar, el empleado se añade a la lista
 *     de directores.
 *   - Eliminar: no se puede eliminar un empleado que es director de otros. Al eliminar,
 *     se quita de la lista de directores.
 *   - Se controla el tamaño de los campos: número de empleado 4 dígitos, apellido y
 *     oficio el número de caracteres de la tabla (10).
 *
 * Ejecución:  java Ejercicio9.java     (se ve en el Escritorio, puerto 6080)
 */
public class Ejercicio9 extends JFrame {

    private final JTextField txtNum = new JTextField(6);
    private final JTextField txtApellido = new JTextField(14);
    private final JTextField txtOficio = new JTextField(14);
    private final JTextField txtSalario = new JTextField(8);
    private final JTextField txtComision = new JTextField(8);
    private final JTextField txtFecha = new JTextField(9);
    private final JComboBox<Item> cbDep = new JComboBox<>();
    private final JComboBox<Item> cbDir = new JComboBox<>();
    private Connection con;

    /** Elemento de las listas desplegables: código + texto */
    record Item(int codigo, String texto) {
        @Override public String toString() { return codigo < 0 ? texto : codigo + " / " + texto; }
    }
    private static final Item ELIGE_DEP = new Item(-1, "Elige Departamento");
    private static final Item ELIGE_DIR = new Item(-1, "Elige Director");

    public Ejercicio9() {
        super("GESTIÓN DE EMPLEADOS");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { salir(); }
        });

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("GESTIÓN DE EMPLEADOS");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 4; p.add(titulo, g); g.gridwidth = 1;

        JButton bConsultar = new JButton("CONSULTAR EMPLEADO");
        fila(p, g, 1, "Nº EMPLEADO:", txtNum, bConsultar);
        fila(p, g, 2, "APELLIDO:", txtApellido, null);
        fila(p, g, 3, "OFICIO:", txtOficio, cbDep);
        fila(p, g, 4, "SALARIO:", txtSalario, cbDir);
        JPanel pFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pFecha.add(new JLabel("FECHA ALTA:")); pFecha.add(txtFecha); pFecha.add(new JLabel("(yyyy-MM-dd)"));
        fila(p, g, 5, "COMISIÓN:", txtComision, pFecha);

        JButton bInsertar = new JButton("INSERTAR"), bEliminar = new JButton("ELIMINAR"),
                bModificar = new JButton("MODIFICAR"), bSalir = new JButton("SALIR"),
                bLimpiar = new JButton("LIMPIAR");
        JPanel b1 = new JPanel(); b1.add(bInsertar); b1.add(bEliminar); b1.add(bModificar);
        JPanel b2 = new JPanel(); b2.add(bSalir); b2.add(bLimpiar);
        g.gridx = 0; g.gridy = 6; g.gridwidth = 4; g.anchor = GridBagConstraints.CENTER; p.add(b1, g);
        g.gridy = 7; p.add(b2, g);
        add(p);

        bConsultar.addActionListener(e -> consultar());
        bInsertar.addActionListener(e -> insertar());
        bEliminar.addActionListener(e -> eliminar());
        bModificar.addActionListener(e -> modificar());
        bLimpiar.addActionListener(e -> limpiar());
        bSalir.addActionListener(e -> salir());

        conectar();
        cargarListas();
        limpiar();
        pack();
        setLocationRelativeTo(null);
    }

    private static void fila(JPanel p, GridBagConstraints g, int y, String etiqueta,
                             JComponent campo, JComponent extra) {
        g.gridy = y; g.gridx = 0; p.add(new JLabel(etiqueta), g);
        g.gridx = 1; p.add(campo, g);
        if (extra != null) { g.gridx = 2; g.gridwidth = 2; p.add(extra, g); g.gridwidth = 1; }
    }

    // ------------------------------------------------------------------ BD
    private void conectar() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");
        } catch (SQLException e) {
            error("Error de conexión con la BD: " + e.getMessage());
            System.exit(1);
        }
    }

    private void cargarListas() {
        cbDep.removeAllItems(); cbDep.addItem(ELIGE_DEP);
        cbDir.removeAllItems(); cbDir.addItem(ELIGE_DIR);
        try (Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT dept_no, dnombre FROM departamentos ORDER BY dept_no");
            while (rs.next()) cbDep.addItem(new Item(rs.getInt(1), rs.getString(2)));
            rs = st.executeQuery("SELECT emp_no, apellido FROM empleados ORDER BY emp_no");
            while (rs.next()) cbDir.addItem(new Item(rs.getInt(1), rs.getString(2)));
        } catch (SQLException e) {
            error("Error al cargar las listas: " + e.getMessage());
        }
    }

    private void consultar() {
        String num = txtNum.getText().trim();
        if (num.isEmpty()) return; // si no se introduce número no se muestra nada
        Integer emp = leerNumEmpleado();
        if (emp == null) return;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM empleados WHERE emp_no = ?")) {
            ps.setInt(1, emp);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                mensaje("EL EMPLEADO " + emp + " NO EXISTE");
                return;
            }
            txtApellido.setText(rs.getString("apellido"));
            txtOficio.setText(rs.getString("oficio"));
            txtSalario.setText(String.valueOf(rs.getFloat("salario")));
            txtComision.setText(String.valueOf(rs.getFloat("comision")));
            Date f = rs.getDate("fecha_alt");
            txtFecha.setText(f == null ? "" : f.toString());
            seleccionar(cbDep, rs.getInt("dept_no"));
            int dir = rs.getInt("dir");
            if (rs.wasNull()) cbDir.setSelectedItem(ELIGE_DIR); else seleccionar(cbDir, dir);
        } catch (SQLException e) {
            error("Error al consultar: " + e.getMessage());
        }
    }

    private void insertar() {
        Object[] v = validar();
        if (v == null) return;
        int emp = (Integer) v[0];
        try {
            if (existeEmpleado(emp)) {
                mensaje("EL EMPLEADO " + emp + " YA EXISTE");
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO empleados VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                rellenar(ps, v, false);
                ps.executeUpdate();
            }
            cbDir.addItem(new Item(emp, (String) v[1])); // se añade a la lista de directores
            mensaje("EMPLEADO INSERTADO");
            limpiar();
        } catch (SQLException e) {
            error("Error al insertar: " + e.getMessage());
        }
    }

    private void modificar() {
        Object[] v = validar();
        if (v == null) return;
        int emp = (Integer) v[0];
        try {
            if (!existeEmpleado(emp)) {
                mensaje("EL EMPLEADO " + emp + " NO EXISTE");
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE empleados SET apellido = ?, oficio = ?, dir = ?, fecha_alt = ?, "
                  + "salario = ?, comision = ?, dept_no = ? WHERE emp_no = ?")) {
                rellenar(ps, v, true);
                ps.executeUpdate();
            }
            cargarListas(); // por si ha cambiado el apellido
            mensaje("EMPLEADO MODIFICADO");
            limpiar();
        } catch (SQLException e) {
            error("Error al modificar: " + e.getMessage());
        }
    }

    private void eliminar() {
        Integer emp = leerNumEmpleado();
        if (emp == null) return;
        try {
            if (!existeEmpleado(emp)) {
                mensaje("EL EMPLEADO " + emp + " NO EXISTE");
                return;
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM empleados WHERE dir = ?")) {
                ps.setInt(1, emp);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    mensaje("NO SE PUEDE ELIMINAR: EL EMPLEADO " + emp + " ES DIRECTOR DE OTROS");
                    return;
                }
            }
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar el empleado " + emp + "?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM empleados WHERE emp_no = ?")) {
                ps.setInt(1, emp);
                ps.executeUpdate();
            }
            for (int i = 0; i < cbDir.getItemCount(); i++) // se quita de la lista de directores
                if (cbDir.getItemAt(i).codigo() == emp) { cbDir.removeItemAt(i); break; }
            mensaje("EMPLEADO ELIMINADO");
            limpiar();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    private boolean existeEmpleado(int emp) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT emp_no FROM empleados WHERE emp_no = ?")) {
            ps.setInt(1, emp);
            return ps.executeQuery().next();
        }
    }

    // ------------------------------------------------------------------ Validación
    private Integer leerNumEmpleado() {
        String num = txtNum.getText().trim();
        if (!num.matches("\\d{1,4}")) {
            mensaje("Nº DE EMPLEADO INCORRECTO (numérico de 4 dígitos como máximo)");
            return null;
        }
        return Integer.parseInt(num);
    }

    /** Devuelve {emp, apellido, oficio, dir, fecha, salario, comision, dep} o null si hay error */
    private Object[] validar() {
        Integer emp = leerNumEmpleado();
        if (emp == null) return null;
        String apellido = txtApellido.getText().trim().toUpperCase();
        String oficio = txtOficio.getText().trim().toUpperCase();
        if (apellido.isEmpty()) { mensaje("APELLIDO NULO"); return null; }
        if (apellido.length() > 10) { mensaje("APELLIDO DEMASIADO LARGO (máx. 10)"); return null; }
        if (oficio.isEmpty()) { mensaje("OFICIO NULO"); return null; }
        if (oficio.length() > 10) { mensaje("OFICIO DEMASIADO LARGO (máx. 10)"); return null; }
        float salario, comision;
        try {
            salario = Float.parseFloat(txtSalario.getText().trim().replace(',', '.'));
            if (salario <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { mensaje("SALARIO INCORRECTO"); return null; }
        try {
            String c = txtComision.getText().trim().replace(',', '.');
            comision = c.isEmpty() ? 0 : Float.parseFloat(c);
        } catch (NumberFormatException e) { mensaje("COMISIÓN INCORRECTA"); return null; }
        Date fecha;
        try {
            fecha = Date.valueOf(txtFecha.getText().trim());
        } catch (IllegalArgumentException e) { mensaje("FECHA INCORRECTA (yyyy-MM-dd)"); return null; }
        Item dep = (Item) cbDep.getSelectedItem();
        if (dep == null || dep.codigo() < 0) { mensaje("ELIGE UN DEPARTAMENTO"); return null; }
        Item dir = (Item) cbDir.getSelectedItem();
        if (dir == null || dir.codigo() < 0) { mensaje("ELIGE UN DIRECTOR"); return null; }
        if (dir.codigo() == emp) { mensaje("UN EMPLEADO NO PUEDE SER SU PROPIO DIRECTOR"); return null; }
        return new Object[] {emp, apellido, oficio, dir.codigo(), fecha, salario, comision, dep.codigo()};
    }

    private static void rellenar(PreparedStatement ps, Object[] v, boolean update) throws SQLException {
        int i = 1;
        if (!update) ps.setInt(i++, (Integer) v[0]);
        ps.setString(i++, (String) v[1]);
        ps.setString(i++, (String) v[2]);
        ps.setInt(i++, (Integer) v[3]);
        ps.setDate(i++, (Date) v[4]);
        ps.setFloat(i++, (Float) v[5]);
        ps.setFloat(i++, (Float) v[6]);
        ps.setInt(i++, (Integer) v[7]);
        if (update) ps.setInt(i, (Integer) v[0]);
    }

    // ------------------------------------------------------------------ Utilidades
    private static void seleccionar(JComboBox<Item> cb, int codigo) {
        for (int i = 0; i < cb.getItemCount(); i++)
            if (cb.getItemAt(i).codigo() == codigo) { cb.setSelectedIndex(i); return; }
        cb.setSelectedIndex(0);
    }

    private void limpiar() {
        for (JTextField t : new JTextField[] {txtNum, txtApellido, txtOficio, txtSalario, txtComision})
            t.setText("");
        txtFecha.setText(LocalDate.now().toString()); // por defecto, la fecha del sistema
        cbDep.setSelectedIndex(0);
        cbDir.setSelectedIndex(0);
        txtNum.requestFocus();
    }

    private void mensaje(String m) { JOptionPane.showMessageDialog(this, m, "Mensaje", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void salir() {
        try { if (con != null) con.close(); } catch (SQLException ignored) { }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ejercicio9().setVisible(true));
    }
}
