import java.io.File;
import java.sql.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import ejercicio13.DatosempledepartType;
import ejercicio13.Departamentos;
import ejercicio13.Empleados;
import ejercicio13.ObjectFactory;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 13
 * A partir del XSD src/main/xsd/empleydepar.xsd, Maven genera con xjc las clases del
 * paquete ejercicio13 (DatosempledepartType, Departamentos, Empleados y ObjectFactory).
 * Esta clase usa esas clases para generar el fichero empleydepar.xml con los datos de
 * empleados y departamentos de MySQL, y después lo lee y lo visualiza.
 *
 * Ejecución:  ./ejecutar.sh Ejercicio13
 */
public class Ejercicio13 {

    static final String FICHERO = "./empleydepar.xml";

    public static void main(String[] args) {
        ObjectFactory factoria = new ObjectFactory();
        DatosempledepartType raiz = factoria.createDatosempledepartType();

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");
             Statement st = con.createStatement();
             ResultSet rsDep = st.executeQuery("SELECT dept_no, dnombre, loc FROM departamentos ORDER BY dept_no");
             PreparedStatement psEmp = con.prepareStatement(
                     "SELECT emp_no, apellido, oficio, salario FROM empleados WHERE dept_no = ? ORDER BY emp_no")) {

            while (rsDep.next()) {
                Departamentos dep = factoria.createDepartamentos();
                dep.setDepno(rsDep.getInt(1));
                dep.setDnombre(rsDep.getString(2));
                dep.setLoc(rsDep.getString(3));
                psEmp.setInt(1, dep.getDepno());
                try (ResultSet rsEmp = psEmp.executeQuery()) {
                    while (rsEmp.next()) {
                        Empleados emp = factoria.createEmpleados();
                        emp.setEmpno(rsEmp.getInt(1));
                        emp.setApellido(rsEmp.getString(2));
                        emp.setOficio(rsEmp.getString(3));
                        emp.setSalario(rsEmp.getFloat(4));
                        dep.getEmpleados().add(emp);
                    }
                }
                raiz.getDepartamento().add(dep);
            }

            // Generar el XML (el elemento raíz se crea con la ObjectFactory)
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(factoria.createDatosempledepart(raiz), new File(FICHERO));
            System.out.println("Fichero " + FICHERO + " creado.\n");

            // Leer el XML y visualizarlo
            Unmarshaller u = context.createUnmarshaller();
            @SuppressWarnings("unchecked")
            JAXBElement<DatosempledepartType> leido =
                    (JAXBElement<DatosempledepartType>) u.unmarshal(new File(FICHERO));
            for (Departamentos d : leido.getValue().getDepartamento()) {
                System.out.printf("Departamento: %d, %s, %s%n", d.getDepno(), d.getDnombre(), d.getLoc());
                for (Empleados e : d.getEmpleados())
                    System.out.printf("   Empleado: %d, %s, %s, %.2f%n",
                            e.getEmpno(), e.getApellido(), e.getOficio(), e.getSalario());
            }
        } catch (SQLException e) {
            System.out.println("ERROR de base de datos: " + e.getMessage());
        } catch (JAXBException e) {
            System.out.println("ERROR JAXB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
