import java.sql.*;

/**
 * ACTIVIDAD 2.12 (parte MySQL)
 * Procedimiento en MySQL similar a la función de Oracle: recibe un número de departamento
 * y, como parámetros de salida, devuelve el número de empleados y el salario medio.
 *   - Si el departamento no existe: salario medio -1 y número de empleados 0.
 *   - Si existe y no tiene empleados: 0 y 0.
 * El procedimiento se crea desde este programa, que después recorre la tabla
 * departamentos y muestra sus datos con el número de empleados y el salario medio.
 *
 * Ejecución:  java ProcedimientoMySQL.java
 */
public class ProcedimientoMySQL {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");

            // 1) Crear el procedimiento desde Java (desde JDBC no se usa DELIMITER)
            Statement st = conexion.createStatement();
            st.executeUpdate("DROP PROCEDURE IF EXISTS datos_salario");
            st.executeUpdate(
                "CREATE PROCEDURE datos_salario (IN d INT, OUT nemple INT, OUT media FLOAT) " +
                "BEGIN " +
                "  DECLARE existe INT; " +
                "  SELECT COUNT(*) INTO existe FROM departamentos WHERE dept_no = d; " +
                "  IF existe = 0 THEN " +
                "    SET nemple = 0; " +
                "    SET media = -1; " +
                "  ELSE " +
                "    SELECT COUNT(*), IFNULL(AVG(salario), 0) INTO nemple, media " +
                "      FROM empleados WHERE dept_no = d; " +
                "  END IF; " +
                "END");
            st.close();
            System.out.println("Procedimiento datos_salario creado.");

            // 2) Preparar la llamada: { call procedimiento(?, ?, ?) }
            CallableStatement llamada = conexion.prepareCall("{ call datos_salario (?, ?, ?) }");

            // 3) Recorrer departamentos y llamar al procedimiento para cada uno
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(
                    "SELECT dept_no, dnombre, loc FROM departamentos ORDER BY dept_no");
            System.out.printf("%-5s %-15s %-12s %10s %12s%n",
                    "DEP", "NOMBRE", "LOCALIDAD", "Nº EMPLE", "SAL. MEDIO");
            while (rs.next()) {
                llamada.setInt(1, rs.getInt("dept_no"));         // parámetro IN
                llamada.registerOutParameter(2, Types.INTEGER);  // parámetro OUT
                llamada.registerOutParameter(3, Types.FLOAT);    // parámetro OUT
                llamada.executeUpdate();
                System.out.printf("%-5d %-15s %-12s %10d %12.2f%n",
                        rs.getInt("dept_no"), rs.getString("dnombre"), rs.getString("loc"),
                        llamada.getInt(2), llamada.getFloat(3));
            }

            // Prueba con un departamento que no existe
            llamada.setInt(1, 99);
            llamada.registerOutParameter(2, Types.INTEGER);
            llamada.registerOutParameter(3, Types.FLOAT);
            llamada.executeUpdate();
            System.out.printf("Departamento 99 (no existe): empleados %d, media %.2f%n",
                    llamada.getInt(2), llamada.getFloat(3));

            rs.close();
            sentencia.close();
            llamada.close();
            conexion.close();
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
