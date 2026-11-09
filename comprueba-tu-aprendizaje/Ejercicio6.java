import java.sql.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 6
 * Inserta ventas en la tabla VENTAS. Parámetros desde la línea de comandos:
 *   1) base de datos (1 = MySQL, 2 = Oracle, 3 = SQLite)
 *   2) identificador de venta   3) identificador de cliente
 *   4) identificador de producto   5) cantidad
 * Comprobaciones antes de insertar:
 *   - el identificador de venta no existe en VENTAS
 *   - el cliente existe en CLIENTES
 *   - el producto existe en PRODUCTOS
 *   - la cantidad es > 0
 *   - la fecha de venta es la fecha actual
 *
 * Ejemplo:  java Ejercicio6.java 1 1 1 4 3
 */
public class Ejercicio6 {

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
        int bd, idVenta, idCliente, idProducto, cantidad;
        try {
            bd = Integer.parseInt(args[0]);
            idVenta = Integer.parseInt(args[1]);
            idCliente = Integer.parseInt(args[2]);
            idProducto = Integer.parseInt(args[3]);
            cantidad = Integer.parseInt(args[4]);
            if (bd < 1 || bd > 3) throw new NumberFormatException();
        } catch (Exception e) {
            System.out.println("Uso: java Ejercicio6.java BD(1-3) IDVENTA IDCLIENTE IDPRODUCTO CANTIDAD");
            return;
        }

        try (Connection con = conectar(bd)) {
            boolean correcto = true;
            if (existe(con, "SELECT IDVENTA FROM VENTAS WHERE IDVENTA = ?", idVenta)) {
                System.out.println("NO SE INSERTA: ya existe la venta " + idVenta + ".");
                correcto = false;
            }
            if (!existe(con, "SELECT ID FROM CLIENTES WHERE ID = ?", idCliente)) {
                System.out.println("NO SE INSERTA: no existe el cliente " + idCliente + ".");
                correcto = false;
            }
            if (!existe(con, "SELECT ID FROM PRODUCTOS WHERE ID = ?", idProducto)) {
                System.out.println("NO SE INSERTA: no existe el producto " + idProducto + ".");
                correcto = false;
            }
            if (cantidad <= 0) {
                System.out.println("NO SE INSERTA: la cantidad debe ser mayor que 0.");
                correcto = false;
            }
            if (!correcto) return;

            String sql = "INSERT INTO VENTAS (IDVENTA, FECHAVENTA, IDCLIENTE, IDPRODUCTO, CANTIDAD) "
                       + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idVenta);
                // En SQLite la fecha se guarda como texto 'aaaa-mm-dd' para que sea legible
                if (bd == 3) ps.setString(2, java.time.LocalDate.now().toString());
                else ps.setDate(2, Date.valueOf(java.time.LocalDate.now()));
                ps.setInt(3, idCliente);
                ps.setInt(4, idProducto);
                ps.setInt(5, cantidad);
                ps.executeUpdate();
                System.out.println("Venta " + idVenta + " insertada correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }

    static boolean existe(Connection c, String sql, int valor) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
