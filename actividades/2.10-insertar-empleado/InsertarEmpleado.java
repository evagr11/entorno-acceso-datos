import java.sql.*;

/**
 * ACTIVIDAD 2.10
 * Inserta un empleado en la tabla empleados. Los valores se reciben desde main():
 *   EMP_NO APELLIDO OFICIO DIR SALARIO COMISION DEPT_NO
 * Comprobaciones antes de insertar:
 *   - el departamento existe en la tabla departamentos
 *   - el número de empleado no existe
 *   - el salario es > 0
 *   - el director (DIR) existe en la tabla empleados
 *   - el APELLIDO y el OFICIO no son nulos
 *   - la fecha de alta es la fecha actual
 *
 * Ejemplo:  java InsertarEmpleado.java 8000 PEREZ VENDEDOR 7698 1800 100 30
 *           (la comisión puede ser "null")
 */
public class InsertarEmpleado {

    public static void main(String[] args) {
        if (args.length != 7) {
            System.out.println("Uso: java InsertarEmpleado.java EMP_NO APELLIDO OFICIO DIR SALARIO COMISION DEPT_NO");
            return;
        }

        int empNo, dir, deptNo;
        float salario;
        Float comision;
        String apellido = args[1].trim(), oficio = args[2].trim();
        try {
            empNo = Integer.parseInt(args[0]);
            dir = Integer.parseInt(args[3]);
            salario = Float.parseFloat(args[4]);
            comision = args[5].equalsIgnoreCase("null") ? null : Float.valueOf(args[5]);
            deptNo = Integer.parseInt(args[6]);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: EMP_NO, DIR, SALARIO, COMISION y DEPT_NO deben ser numéricos.");
            return;
        }

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo")) {

            // Comprobaciones (se muestran todos los motivos por los que no se inserta)
            boolean correcto = true;
            if (!existe(conexion, "SELECT dept_no FROM departamentos WHERE dept_no = ?", deptNo)) {
                System.out.println("NO SE INSERTA: departamento " + deptNo + " inexistente.");
                correcto = false;
            }
            if (existe(conexion, "SELECT emp_no FROM empleados WHERE emp_no = ?", empNo)) {
                System.out.println("NO SE INSERTA: número de empleado " + empNo + " duplicado.");
                correcto = false;
            }
            if (salario <= 0) {
                System.out.println("NO SE INSERTA: el salario debe ser mayor que 0.");
                correcto = false;
            }
            if (!existe(conexion, "SELECT emp_no FROM empleados WHERE emp_no = ?", dir)) {
                System.out.println("NO SE INSERTA: director " + dir + " inexistente.");
                correcto = false;
            }
            if (apellido.isEmpty() || apellido.equalsIgnoreCase("null")) {
                System.out.println("NO SE INSERTA: el APELLIDO no puede ser nulo.");
                correcto = false;
            }
            if (oficio.isEmpty() || oficio.equalsIgnoreCase("null")) {
                System.out.println("NO SE INSERTA: el OFICIO no puede ser nulo.");
                correcto = false;
            }
            if (!correcto) return;

            // Inserción (la fecha de alta es la fecha actual: CURRENT_DATE)
            String sql = "INSERT INTO empleados VALUES (?, ?, ?, ?, CURRENT_DATE, ?, ?, ?)";
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setInt(1, empNo);
                sentencia.setString(2, apellido.toUpperCase());
                sentencia.setString(3, oficio.toUpperCase());
                sentencia.setInt(4, dir);
                sentencia.setFloat(5, salario);
                if (comision == null) sentencia.setNull(6, Types.FLOAT);
                else sentencia.setFloat(6, comision);
                sentencia.setInt(7, deptNo);
                int filas = sentencia.executeUpdate();
                System.out.printf("Empleado %d insertado. Filas afectadas: %d%n", empNo, filas);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /** Devuelve true si la consulta (con un parámetro entero) devuelve alguna fila */
    static boolean existe(Connection c, String sql, int valor) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
