import java.sql.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 7
 * Listado de las ventas de un cliente. Parámetros: base de datos (1 = MySQL,
 * 2 = Oracle, 3 = SQLite) e identificador del cliente.
 *
 * Ejemplo:  java Ejercicio7.java 1 1
 */
public class Ejercicio7 {

    static Connection conectar(int bd) throws SQLException {
        switch (bd) {
            case 1: return DriverManager.getConnection("jdbc:mysql://localhost/unidad2", "unidad2", "unidad2");
            case 2: return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "unidad2", "unidad2");
            case 3: return DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.home")
                                                       + "/DB/SQLITE/UNIDAD2.DB");
            default: throw new IllegalArgumentException("Base de datos no válida");
        }
    }

    public static void main(String[] args) {
        int bd, idCliente;
        try {
            bd = Integer.parseInt(args[0]);
            idCliente = Integer.parseInt(args[1]);
            if (bd < 1 || bd > 3) throw new NumberFormatException();
        } catch (Exception e) {
            System.out.println("Uso: java Ejercicio7.java BD(1-3) IDCLIENTE");
            return;
        }

        try (Connection con = conectar(bd)) {
            PreparedStatement psCli = con.prepareStatement("SELECT NOMBRE FROM CLIENTES WHERE ID = ?");
            psCli.setInt(1, idCliente);
            ResultSet rsCli = psCli.executeQuery();
            if (!rsCli.next()) {
                System.out.println("No existe el cliente " + idCliente + ".");
                return;
            }
            System.out.println("Ventas del cliente: " + rsCli.getString(1));
            rsCli.close();
            psCli.close();

            String sql = "SELECT v.IDVENTA, v.FECHAVENTA, p.DESCRIPCION, v.CANTIDAD, p.PVP "
                       + "FROM VENTAS v JOIN PRODUCTOS p ON v.IDPRODUCTO = p.ID "
                       + "WHERE v.IDCLIENTE = ? ORDER BY v.IDVENTA";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            int numVentas = 0;
            double total = 0;
            while (rs.next()) {
                int cantidad = rs.getInt("CANTIDAD");
                double pvp = rs.getDouble("PVP");
                double importe = cantidad * pvp;
                String fecha = rs.getString("FECHAVENTA");
                if (fecha != null && fecha.length() > 10) fecha = fecha.substring(0, 10); // Oracle: quita la hora
                System.out.printf("Venta: %d, Fecha venta: %s%n", rs.getInt("IDVENTA"), fecha);
                System.out.printf("        Producto: %s%n", rs.getString("DESCRIPCION"));
                System.out.printf("        Cantidad: %d     PVP: %.2f%n", cantidad, pvp);
                System.out.printf("        Importe: %.2f%n", importe);
                numVentas++;
                total += importe;
            }
            rs.close();
            ps.close();

            System.out.println("Número total de ventas: " + numVentas);
            System.out.printf("Importe Total: %.2f%n", total);
        } catch (SQLException e) {
            System.out.println("ERROR (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }
}
