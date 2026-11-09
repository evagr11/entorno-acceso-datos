import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * ACTIVIDADES DE AMPLIACIÓN · 1
 * Modifica departamentos de la tabla departamentos (MySQL) a partir de un fichero de
 * texto. Cada línea:  departamento a modificar, nuevo nombre, nueva localidad
 *   10,INFORMÁTICA,          -> cambia el nombre del 10, no la localidad
 *   20, NÓMINAS, SEVILLA     -> cambia nombre y localidad del 20
 *   40, , SANTANDER          -> cambia solo la localidad del 40
 *   30,,  / 40 / 50 ,        -> no hay datos para modificar
 * Se controlan los errores: departamento incorrecto, no hay datos para modificar,
 * el departamento no existe, etc.
 *
 * Ejecución:  java ModificarDepartamentos.java [fichero]   (por defecto departamentos.txt)
 */
public class ModificarDepartamentos {
    public static void main(String[] args) {
        String fichero = args.length > 0 ? args[0] : "departamentos.txt";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                     new FileInputStream(fichero), StandardCharsets.UTF_8));
             Connection con = DriverManager.getConnection(
                     "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo")) {

            String linea;
            int numLinea = 0, modificados = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                if (linea.isBlank()) continue;
                String[] campos = linea.split(",", -1);
                String depTxt = campos[0].trim();
                String nombre = campos.length > 1 ? campos[1].trim() : "";
                String loc = campos.length > 2 ? campos[2].trim() : "";
                String prefijo = String.format("Línea %d [%s]: ", numLinea, linea.trim());

                int dep;
                try {
                    dep = Integer.parseInt(depTxt);
                } catch (NumberFormatException e) {
                    System.out.println(prefijo + "ERROR, departamento incorrecto (debe ser numérico).");
                    continue;
                }
                if (nombre.isEmpty() && loc.isEmpty()) {
                    System.out.println(prefijo + "no hay datos para modificar.");
                    continue;
                }
                if (nombre.length() > 15 || loc.length() > 15) {
                    System.out.println(prefijo + "ERROR, el nombre y la localidad tienen como máximo 15 caracteres.");
                    continue;
                }

                // Construir el UPDATE solo con los campos que vienen en la línea
                StringBuilder sql = new StringBuilder("UPDATE departamentos SET ");
                if (!nombre.isEmpty()) sql.append("dnombre = ?");
                if (!loc.isEmpty()) sql.append(nombre.isEmpty() ? "" : ", ").append("loc = ?");
                sql.append(" WHERE dept_no = ?");

                try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                    int i = 1;
                    if (!nombre.isEmpty()) ps.setString(i++, nombre);
                    if (!loc.isEmpty()) ps.setString(i++, loc);
                    ps.setInt(i, dep);
                    int filas = ps.executeUpdate();
                    if (filas == 0) {
                        System.out.println(prefijo + "ERROR, el departamento " + dep + " no existe.");
                    } else {
                        modificados++;
                        System.out.println(prefijo + "departamento " + dep + " modificado"
                                + (nombre.isEmpty() ? "" : ", nombre = " + nombre)
                                + (loc.isEmpty() ? "" : ", localidad = " + loc) + ".");
                    }
                } catch (SQLException e) {
                    System.out.println(prefijo + "ERROR SQL: " + e.getMessage());
                }
            }
            System.out.println("Modificaciones realizadas: " + modificados);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: no existe el fichero " + fichero);
        } catch (IOException e) {
            System.out.println("ERROR de E/S: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("ERROR de conexión: " + e.getMessage());
        }
    }
}
