import java.sql.*;

/**
 * ACTIVIDAD 2.12 (parte Oracle)
 * Crea una función en Oracle que reciba un número de departamento y devuelva el salario
 * medio de sus empleados y, como parámetro de salida, el número de empleados.
 *   - Si el departamento no existe: salario medio -1 y número de empleados 0.
 *   - Si existe y no tiene empleados: 0 y 0.
 * El programa crea la función, recorre la tabla departamentos y muestra los datos de
 * cada departamento con su número de empleados y su salario medio.
 *
 * Necesita un Oracle con el usuario ejemplo/ejemplo y las tablas departamentos y empleados.
 * Ejecución:  java FuncionOracle.java
 */
public class FuncionOracle {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conexion = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:XE", "ejemplo", "ejemplo");

            // 1) Crear la función desde Java
            String funcion =
                "CREATE OR REPLACE FUNCTION salario_medio (d NUMBER, nemple OUT NUMBER) " +
                "RETURN NUMBER AS " +
                "  media NUMBER; " +
                "  existe NUMBER; " +
                "BEGIN " +
                "  SELECT COUNT(*) INTO existe FROM departamentos WHERE dept_no = d; " +
                "  IF existe = 0 THEN " +
                "    nemple := 0; " +
                "    RETURN -1; " +
                "  END IF; " +
                "  SELECT COUNT(*), NVL(AVG(salario), 0) INTO nemple, media " +
                "    FROM empleados WHERE dept_no = d; " +
                "  RETURN media; " +
                "END;";
            Statement st = conexion.createStatement();
            st.executeUpdate(funcion);
            st.close();
            System.out.println("Función salario_medio creada.");

            // 2) Preparar la llamada: { ? = call funcion(?, ?) }
            CallableStatement llamada = conexion.prepareCall("{ ? = call salario_medio (?, ?) }");

            // 3) Recorrer departamentos y llamar a la función para cada uno
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(
                    "SELECT dept_no, dnombre, loc FROM departamentos ORDER BY dept_no");
            System.out.printf("%-5s %-15s %-12s %10s %12s%n",
                    "DEP", "NOMBRE", "LOCALIDAD", "Nº EMPLE", "SAL. MEDIO");
            while (rs.next()) {
                llamada.registerOutParameter(1, Types.FLOAT);   // valor devuelto
                llamada.setInt(2, rs.getInt("dept_no"));        // parámetro de entrada
                llamada.registerOutParameter(3, Types.INTEGER); // parámetro OUT
                llamada.executeUpdate();
                System.out.printf("%-5d %-15s %-12s %10d %12.2f%n",
                        rs.getInt("dept_no"), rs.getString("dnombre"), rs.getString("loc"),
                        llamada.getInt(3), llamada.getFloat(1));
            }

            // Prueba con un departamento que no existe
            llamada.registerOutParameter(1, Types.FLOAT);
            llamada.setInt(2, 99);
            llamada.registerOutParameter(3, Types.INTEGER);
            llamada.executeUpdate();
            System.out.printf("Departamento 99 (no existe): empleados %d, media %.2f%n",
                    llamada.getInt(3), llamada.getFloat(1));

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
