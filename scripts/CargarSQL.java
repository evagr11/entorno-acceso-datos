import java.nio.file.*;
import java.sql.*;

/** Uso: java CargarSQL.java <url> <usuario> <clave> <fichero.sql> */
public class CargarSQL {
    public static void main(String[] args) throws Exception {
        String texto = Files.readString(Path.of(args[3]));
        try (Connection c = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement st = c.createStatement()) {
            for (String sql : texto.split(";")) {
                sql = sql.strip();
                if (sql.isEmpty()) continue;
                try {
                    st.execute(sql);
                } catch (SQLException e) {
                    if (!sql.toUpperCase().startsWith("DROP")) throw e; // la tabla aún no existía
                }
            }
            if (args[0].startsWith("jdbc:hsqldb")) st.execute("SHUTDOWN");
        }
        if (args[0].startsWith("jdbc:derby")) {
            try { DriverManager.getConnection("jdbc:derby:;shutdown=true"); } catch (SQLException ignorada) { }
        }
        System.out.println("Cargado en " + args[0]);
    }
}
