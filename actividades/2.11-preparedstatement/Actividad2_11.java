import java.sql.*;
import java.text.DecimalFormat;

/**
 * ACTIVIDAD 2.11
 * Utiliza PreparedStatement para visualizar el APELLIDO, SALARIO y OFICIO de los
 * empleados de un departamento cuyo valor se recibe desde main(). Visualiza también
 * el nombre del departamento. Al final, el salario medio y el número de empleados.
 * Si el departamento no existe, se indica. Se usa DecimalFormat para el salario.
 *
 * Ejecución:  java Actividad2_11.java 30
 */
public class Actividad2_11 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Actividad2_11.java NUM_DEPARTAMENTO");
            return;
        }
        int dep;
        try {
            dep = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("El número de departamento debe ser numérico.");
            return;
        }
        DecimalFormat formato = new DecimalFormat("##,##0.00");

        try (Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo")) {

            // Nombre del departamento
            PreparedStatement psDep = conexion.prepareStatement(
                    "SELECT dnombre FROM departamentos WHERE dept_no = ?");
            psDep.setInt(1, dep);
            ResultSet rsDep = psDep.executeQuery();
            if (!rsDep.next()) {
                System.out.println("El departamento " + dep + " no existe.");
                return;
            }
            System.out.println("Departamento: " + dep + " - " + rsDep.getString(1));
            System.out.println("=================================");
            rsDep.close();
            psDep.close();

            // Empleados del departamento
            String sql = "SELECT apellido, salario, oficio FROM empleados WHERE dept_no = ? ORDER BY 1";
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, dep);
            ResultSet rs = sentencia.executeQuery();
            while (rs.next())
                System.out.printf("%-10s %12s  %s%n", rs.getString("apellido"),
                        formato.format(rs.getFloat("salario")), rs.getString("oficio"));
            rs.close();
            sentencia.close();

            // Salario medio y número de empleados
            PreparedStatement psTot = conexion.prepareStatement(
                    "SELECT COUNT(*), IFNULL(AVG(salario), 0) FROM empleados WHERE dept_no = ?");
            psTot.setInt(1, dep);
            ResultSet rsTot = psTot.executeQuery();
            rsTot.next();
            System.out.println("=================================");
            System.out.println("Número de empleados: " + rsTot.getInt(1));
            System.out.println("Salario medio:       " + formato.format(rsTot.getFloat(2)));
            rsTot.close();
            psTot.close();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
