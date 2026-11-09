import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import net.sf.jasperreports.engine.JRException;

/**
 * ACTIVIDAD 2.13
 * Nueva plantilla (plantillas/resumen-departamentos.jrxml) para obtener por cada
 * departamento, además de sus datos, el número de empleados, la media de salario y la
 * suma de salario. Si no tiene empleados sale 0 en la media, el contador y la suma.
 * El documento se visualiza en apaisado.
 *
 * Ejecución:  ./ejecutar.sh Actividad2_13
 */
public class Actividad2_13 {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo")) {
            Informes.generar("./plantillas/resumen-departamentos.jrxml", "ResumenDepartamentos",
                    Informes.parametros("RESUMEN DATOS DE DEPARTAMENTOS."), conn, true);
        } catch (CommunicationsException c) {
            System.out.println(" Error de comunicación con la BD. No está arrancada.");
        } catch (SQLException e) {
            System.out.println(" Error al ejecutar sentencia SQL: " + e.getMessage());
        } catch (JRException ex) {
            System.out.println(" Error Jasper.");
            ex.printStackTrace();
        }
    }
}
