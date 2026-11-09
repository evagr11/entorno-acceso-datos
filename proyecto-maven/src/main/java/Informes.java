import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Utilidad común para los informes con JasperReports: compila la plantilla .jrxml,
 * la rellena con los datos de la conexión, la exporta a HTML, PDF y XML en la carpeta
 * informes/ y, si hay pantalla, la muestra en el visor.
 */
public class Informes {

    /** Parámetros típicos del libro: titulo, autor y fecha */
    public static Map<String, Object> parametros(String titulo) {
        Map<String, Object> params = new HashMap<>();
        params.put("titulo", titulo);
        params.put("autor", "Eva Gallardo");
        params.put("fecha", (new java.util.Date()).toString());
        return params;
    }

    /**
     * @param plantilla  ruta de la plantilla, p. ej. "./plantillas/plantilla.jrxml"
     * @param salida     nombre base de los ficheros de salida, p. ej. "Informe"
     * @param cerrarAlSalir true si al cerrar el visor debe terminar el programa
     */
    public static void generar(String plantilla, String salida, Map<String, Object> params,
                               Connection conn, boolean cerrarAlSalir) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(plantilla);
        JasperPrint miInforme = JasperFillManager.fillReport(jasperReport, params, conn);

        JasperExportManager.exportReportToHtmlFile(miInforme, "./informes/" + salida + ".html");
        JasperExportManager.exportReportToPdfFile(miInforme, "./informes/" + salida + ".pdf");
        JasperExportManager.exportReportToXmlFile(miInforme, "./informes/" + salida + ".xml", false);
        System.out.println("ARCHIVOS CREADOS: informes/" + salida + ".html, .pdf y .xml");

        if (!GraphicsEnvironment.isHeadless()) {
            JasperViewer.viewReport(miInforme, cerrarAlSalir); // visualizar en pantalla
        } else {
            System.out.println("(Sin pantalla: abre el PDF o el HTML de la carpeta informes/)");
        }
    }
}
