import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

/**
 * COMPRUEBA TU APRENDIZAJE · EJERCICIO 11
 * Con JAXB (Capítulo 1) se crean las clases necesarias para obtener un documento XML con
 * los datos de departamentos y empleados de la BD MySQL ejemplo. También hay un método
 * para visualizar el contenido del fichero XML creado. Estructura:
 *   <Datosempledepart>
 *     <Departamento> <depno/> <dnombre/> <loc/>
 *       <Empleados> <Emple> <empno/> <apellido/> <salario/> <oficio/> </Emple> ... </Empleados>
 *     </Departamento> ...
 *   </Datosempledepart>
 *
 * Ejecución:  ./ejecutar.sh Ejercicio11       -> crea Datosempledepart.xml y lo visualiza
 */
public class Ejercicio11 {

    static final String FICHERO = "./Datosempledepart.xml";

    public static void main(String[] args) {
        try {
            Datosempledepart datos = leerDeMySQL();
            crearXML(datos, FICHERO);
            visualizarXML(FICHERO);
        } catch (SQLException e) {
            System.out.println("ERROR de base de datos: " + e.getMessage());
        } catch (JAXBException e) {
            System.out.println("ERROR JAXB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Carga los departamentos y sus empleados desde MySQL en los objetos JAXB */
    static Datosempledepart leerDeMySQL() throws SQLException {
        Datosempledepart datos = new Datosempledepart();
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost/ejemplo", "ejemplo", "ejemplo");
             Statement st = con.createStatement();
             ResultSet rsDep = st.executeQuery("SELECT dept_no, dnombre, loc FROM departamentos ORDER BY dept_no");
             PreparedStatement psEmp = con.prepareStatement(
                     "SELECT emp_no, apellido, salario, oficio FROM empleados WHERE dept_no = ? ORDER BY emp_no")) {
            while (rsDep.next()) {
                Departamento d = new Departamento();
                d.setDepno(rsDep.getInt(1));
                d.setDnombre(rsDep.getString(2));
                d.setLoc(rsDep.getString(3));
                psEmp.setInt(1, d.getDepno());
                try (ResultSet rsEmp = psEmp.executeQuery()) {
                    while (rsEmp.next()) {
                        Emple e = new Emple();
                        e.setEmpno(rsEmp.getInt(1));
                        e.setApellido(rsEmp.getString(2));
                        e.setSalario(rsEmp.getFloat(3));
                        e.setOficio(rsEmp.getString(4));
                        d.getEmpleados().add(e);
                    }
                }
                datos.getDepartamentos().add(d);
            }
        }
        return datos;
    }

    /** Genera el documento XML */
    static void crearXML(Datosempledepart datos, String fichero) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Datosempledepart.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        m.marshal(datos, new File(fichero));
        System.out.println("Fichero " + fichero + " creado.\n");
    }

    /** Visualiza el contenido del fichero XML (lo lee con JAXB) */
    static void visualizarXML(String fichero) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Datosempledepart.class);
        Unmarshaller u = context.createUnmarshaller();
        Datosempledepart datos = (Datosempledepart) u.unmarshal(new File(fichero));
        System.out.println("CONTENIDO DE " + fichero);
        System.out.println("==========================================");
        for (Departamento d : datos.getDepartamentos()) {
            System.out.printf("Departamento: %d, %s, %s%n", d.getDepno(), d.getDnombre(), d.getLoc());
            if (d.getEmpleados().isEmpty()) System.out.println("   (sin empleados)");
            for (Emple e : d.getEmpleados())
                System.out.printf("   Empleado: %d, %s, %.2f, %s%n",
                        e.getEmpno(), e.getApellido(), e.getSalario(), e.getOficio());
        }
    }

    // ===================== Clases JAXB =====================

    @XmlRootElement(name = "Datosempledepart")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Datosempledepart {
        @XmlElement(name = "Departamento")
        private List<Departamento> departamentos = new ArrayList<>();
        public List<Departamento> getDepartamentos() { return departamentos; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"depno", "dnombre", "loc", "empleados"})
    public static class Departamento {
        private int depno;
        private String dnombre;
        private String loc;
        @XmlElementWrapper(name = "Empleados")
        @XmlElement(name = "Emple")
        private List<Emple> empleados = new ArrayList<>();

        public int getDepno() { return depno; }
        public void setDepno(int depno) { this.depno = depno; }
        public String getDnombre() { return dnombre; }
        public void setDnombre(String dnombre) { this.dnombre = dnombre; }
        public String getLoc() { return loc; }
        public void setLoc(String loc) { this.loc = loc; }
        public List<Emple> getEmpleados() { return empleados; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"empno", "apellido", "salario", "oficio"})
    public static class Emple {
        private int empno;
        private String apellido;
        private float salario;
        private String oficio;

        public int getEmpno() { return empno; }
        public void setEmpno(int empno) { this.empno = empno; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public float getSalario() { return salario; }
        public void setSalario(float salario) { this.salario = salario; }
        public String getOficio() { return oficio; }
        public void setOficio(String oficio) { this.oficio = oficio; }
    }
}
