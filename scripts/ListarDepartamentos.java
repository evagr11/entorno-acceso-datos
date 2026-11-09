import java.sql.*;

/** Uso: java ListarDepartamentos.java <url> [usuario] [clave] */
public class ListarDepartamentos {
    public static void main(String[] args) throws Exception {
        String user = args.length > 1 ? args[1] : "";
        String pass = args.length > 2 ? args[2] : "";
        try (Connection c = DriverManager.getConnection(args[0], user, pass);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM departamentos")) {
            System.out.println("== " + args[0]);
            while (rs.next())
                System.out.printf("%d, %s, %s%n", rs.getInt(1), rs.getString(2), rs.getString(3));
        }
    }
}
