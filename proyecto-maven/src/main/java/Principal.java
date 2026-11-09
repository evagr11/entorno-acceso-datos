import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import net.sf.jasperreports.engine.JRException;

/**
 * Ejemplo del libro (págs. 120-122): informe de la tabla departamentos de MySQL con la
 * plantilla plantillas/plantilla.jrxml. Genera HTML, PDF y XML en informes/ y lo
 * muestra en el visor.
 *
 * Ejecución:  ./ejecutar.sh Principal
 */
public class Principal {
    public static void main(String[] args) {
        Map<String, Object> params = Informes.parametros("LISTADO DE DEPARTAMENTOS.");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo")) {
            Informes.generar("./plantillas/plantilla.jrxml", "Informe", params, conn, true);
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
