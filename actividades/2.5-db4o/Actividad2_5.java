import java.io.File;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

/**
 * ACTIVIDAD 2.5
 * Crea una base de datos Db4o de nombre EMPLEDEP.YAP e inserta objetos EMPLEADOS y
 * DEPARTAMENTOS en ella. Después obtén todos los objetos empleado de un departamento
 * concreto. Visualiza también el nombre de dicho departamento.
 *
 * Ejecución:  java Actividad2_5.java [num_departamento]      (por defecto 10)
 * Necesita el jar de Db4o en DB/jars (db4o-8.0.276.16149-all-java5.jar).
 */
public class Actividad2_5 {

    static String BDEmpleDep = System.getProperty("user.home") + "/DB/DB4O/EMPLEDEP.YAP";

    public static void main(String[] args) {
        int dep = args.length > 0 ? Integer.parseInt(args[0]) : 10;

        boolean nueva = !new File(BDEmpleDep).exists();
        ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), BDEmpleDep);
        try {
            if (nueva) {
                insertarDatos(db);
                System.out.println("Base de datos EMPLEDEP.YAP creada con sus datos.");
            }

            // Buscar el departamento (QBE: solo se rellena el campo por el que se busca)
            ObjectSet<Departamento> deps = db.queryByExample(new Departamento(dep, null, null));
            if (deps.isEmpty()) {
                System.out.println("El departamento " + dep + " no existe.");
                return;
            }
            Departamento d = deps.next();
            System.out.printf("Departamento: %d - %s (%s)%n", d.getDeptNo(), d.getDnombre(), d.getLoc());

            // Empleados de ese departamento
            Empleado ejemplo = new Empleado();
            ejemplo.setDeptNo(dep);
            ObjectSet<Empleado> emps = db.queryByExample(ejemplo);
            System.out.printf("Número de empleados: %d%n", emps.size());
            while (emps.hasNext()) {
                Empleado e = emps.next();
                System.out.printf("  %d, %s, %s, %.2f%n",
                        e.getEmpNo(), e.getApellido(), e.getOficio(), e.getSalario());
            }
        } finally {
            db.close(); // cerrar base de datos
        }
    }

    static void insertarDatos(ObjectContainer db) {
        db.store(new Departamento(10, "CONTABILIDAD", "SEVILLA"));
        db.store(new Departamento(20, "INVESTIGACIÓN", "MADRID"));
        db.store(new Departamento(30, "VENTAS", "BARCELONA"));
        db.store(new Departamento(40, "PRODUCCIÓN", "BILBAO"));

        db.store(new Empleado(7369, "SANCHEZ", "EMPLEADO", 7902, "1990-12-17", 1040, 0, 20));
        db.store(new Empleado(7499, "ARROYO", "VENDEDOR", 7698, "1990-02-20", 1500, 390, 30));
        db.store(new Empleado(7521, "SALA", "VENDEDOR", 7698, "1991-02-22", 1625, 650, 30));
        db.store(new Empleado(7566, "JIMENEZ", "DIRECTOR", 7839, "1991-04-02", 2900, 0, 20));
        db.store(new Empleado(7654, "MARTIN", "VENDEDOR", 7698, "1991-09-29", 1600, 1020, 30));
        db.store(new Empleado(7698, "NEGRO", "DIRECTOR", 7839, "1991-05-01", 3005, 0, 30));
        db.store(new Empleado(7782, "CEREZO", "DIRECTOR", 7839, "1991-06-09", 2885, 0, 10));
        db.store(new Empleado(7788, "GIL", "ANALISTA", 7566, "1991-11-09", 3000, 0, 20));
        db.store(new Empleado(7839, "REY", "PRESIDENTE", 0, "1991-11-17", 4100, 0, 10));
        db.store(new Empleado(7844, "TOVAR", "VENDEDOR", 7698, "1991-09-08", 1350, 0, 30));
        db.store(new Empleado(7876, "ALONSO", "EMPLEADO", 7788, "1991-09-23", 1430, 0, 20));
        db.store(new Empleado(7900, "JIMENO", "EMPLEADO", 7698, "1991-12-03", 1335, 0, 30));
        db.store(new Empleado(7902, "FERNANDEZ", "ANALISTA", 7566, "1991-12-03", 3000, 0, 20));
        db.store(new Empleado(7934, "MUÑOZ", "EMPLEADO", 7782, "1992-01-23", 1690, 0, 10));
    }

    // ------------------------------------------------------------------
    public static class Departamento {
        private int deptNo;
        private String dnombre;
        private String loc;

        public Departamento() { }
        public Departamento(int deptNo, String dnombre, String loc) {
            this.deptNo = deptNo; this.dnombre = dnombre; this.loc = loc;
        }
        public int getDeptNo() { return deptNo; }
        public void setDeptNo(int deptNo) { this.deptNo = deptNo; }
        public String getDnombre() { return dnombre; }
        public void setDnombre(String dnombre) { this.dnombre = dnombre; }
        public String getLoc() { return loc; }
        public void setLoc(String loc) { this.loc = loc; }
    }

    public static class Empleado {
        private int empNo;
        private String apellido;
        private String oficio;
        private int dir;
        private String fechaAlt;
        private float salario;
        private float comision;
        private int deptNo;

        public Empleado() { }
        public Empleado(int empNo, String apellido, String oficio, int dir, String fechaAlt,
                        float salario, float comision, int deptNo) {
            this.empNo = empNo; this.apellido = apellido; this.oficio = oficio; this.dir = dir;
            this.fechaAlt = fechaAlt; this.salario = salario; this.comision = comision; this.deptNo = deptNo;
        }
        public int getEmpNo() { return empNo; }
        public void setEmpNo(int empNo) { this.empNo = empNo; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public String getOficio() { return oficio; }
        public void setOficio(String oficio) { this.oficio = oficio; }
        public int getDir() { return dir; }
        public void setDir(int dir) { this.dir = dir; }
        public String getFechaAlt() { return fechaAlt; }
        public void setFechaAlt(String fechaAlt) { this.fechaAlt = fechaAlt; }
        public float getSalario() { return salario; }
        public void setSalario(float salario) { this.salario = salario; }
        public float getComision() { return comision; }
        public void setComision(float comision) { this.comision = comision; }
        public int getDeptNo() { return deptNo; }
        public void setDeptNo(int deptNo) { this.deptNo = deptNo; }
    }
}
