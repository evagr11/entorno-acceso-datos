import java.sql.*;

/**
 * ACTIVIDAD 2.7: Realiza la Actividad 2.6 utilizando el puente JDBC-ODBC.
 *
 * IMPORTANTE: el puente JDBC-ODBC (sun.jdbc.odbc.JdbcOdbcDriver) solo existe hasta Java 7.
 * Con Java 8 o superior este programa compila pero falla al cargar el driver.
 * Para hacerlo con un JDK actual mira actividad2_7.c (misma actividad con ODBC desde C).
 *
 * Actividad 2.6:
 * 1) Obtén el APELLIDO, OFICIO y SALARIO de los empleados del departamento 10.
 * 2) Visualiza el APELLIDO del empleado con máximo salario, su SALARIO y el nombre
 *    de su departamento.
 *
 * Ejecución:  java Actividad2_7.java   (solo con Java 7)
 */
public class Actividad2_7 {
    public static void main(String[] args) {
        try {
            // Cargar el driver
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            // Establecemos la conexión con la BD
            Connection conexion = DriverManager.getConnection(
                    "jdbc:odbc:Mysql-odbc");
            Statement sentencia = conexion.createStatement();

            // ---- Parte 1: empleados del departamento 10 ----
            System.out.println("EMPLEADOS DEL DEPARTAMENTO 10");
            System.out.println("=============================");
            ResultSet resul = sentencia.executeQuery(
                    "SELECT apellido, oficio, salario FROM empleados WHERE dept_no = 10");
            while (resul.next()) {
                System.out.printf("%s, %s, %.2f %n",
                        resul.getString("apellido"),
                        resul.getString("oficio"),
                        resul.getFloat("salario"));
            }
            resul.close();

            // ---- Parte 2: empleado con máximo salario ----
            System.out.println();
            System.out.println("EMPLEADO CON MÁXIMO SALARIO");
            System.out.println("===========================");
            String sql = "SELECT e.apellido, e.salario, d.dnombre "
                       + "FROM empleados e JOIN departamentos d ON e.dept_no = d.dept_no "
                       + "WHERE e.salario = (SELECT MAX(salario) FROM empleados)";
            resul = sentencia.executeQuery(sql);
            while (resul.next()) {
                System.out.printf("Apellido: %s, Salario: %.2f, Departamento: %s %n",
                        resul.getString(1), resul.getFloat(2), resul.getString(3));
            }

            resul.close();      // Cerrar ResultSet
            sentencia.close();  // Cerrar Statement
            conexion.close();   // Cerrar conexión
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
