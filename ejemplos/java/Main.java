import java.sql.*;

/** Ejemplo del libro (pág. 89): muestra la tabla departamentos de MySQL por JDBC. */
public class Main {
    public static void main(String[] args) {
        try {
            // Cargar el driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecemos la conexion con la BD
            Connection conexion = DriverManager.getConnection
                ("jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");

            // Preparamos la consulta
            Statement sentencia = conexion.createStatement();
            String sql = "SELECT * FROM departamentos";
            ResultSet resul = sentencia.executeQuery(sql);

            // Recorremos el resultado para visualizar cada fila
            while (resul.next()) {
                System.out.printf("%d, %s, %s %n",
                    resul.getInt(1), resul.getString(2), resul.getString(3));
            }

            resul.close();      // Cerrar ResultSet
            sentencia.close();  // Cerrar Statement
            conexion.close();   // Cerrar conexion
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
