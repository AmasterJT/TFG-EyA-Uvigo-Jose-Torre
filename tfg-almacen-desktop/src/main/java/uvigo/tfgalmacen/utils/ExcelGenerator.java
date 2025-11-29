package uvigo.tfgalmacen.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import java.io.File;
import java.sql.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExcelGenerator {

    private static final Logger LOGGER = Logger.getLogger(ExcelGenerator.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    public static void exportarTodasLasTablasAExcel(Connection cn,
                                                    List<String> nombresTablas,
                                                    File destinoXlsx) throws IOException {

        if (cn == null) {
            throw new IllegalArgumentException("Conexión nula en exportarBDaExcel");
        }

        try (Workbook wb = new XSSFWorkbook()) {

            for (String tabla : nombresTablas) {
                try {
                    exportarTablaAHoja(cn, wb, tabla);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE,
                            "Error al exportar tabla " + tabla + ": " + e.getMessage(), e);
                    // NO cerramos la conexión aquí, continuamos con el resto
                }
            }

            try (FileOutputStream fos = new FileOutputStream(destinoXlsx)) {
                wb.write(fos);
            }
        }
    }


    private static void exportarTablaAHoja(Connection cn,
                                           Workbook wb,
                                           String nombreTabla) throws SQLException {

        String sql = "SELECT * FROM " + nombreTabla;
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            Sheet sheet = wb.createSheet(nombreTabla);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Cabecera
            Row header = sheet.createRow(0);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = header.createCell(i - 1);
                cell.setCellValue(meta.getColumnName(i));
            }

            // Datos
            int rowIndex = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);
                    Object value = rs.getObject(i);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            LOGGER.info("Tabla exportada a hoja: " + nombreTabla + " (" + (rowIndex - 1) + " filas)");

        } // aquí SOLO se cierran Statement y ResultSet, NO la Connection
    }

    private static String sanitizeSheetName(String original) {
        String s = original;
        // Quitamos caracteres ilegales
        s = s.replaceAll("[\\\\/?*\\[\\]]", "_");
        // Recortar a 31 caracteres máximo
        if (s.length() > 31) {
            s = s.substring(0, 31);
        }
        // Nombre vacío o raro -> nombre genérico
        if (s.isBlank()) {
            s = "Hoja";
        }
        return s;
    }
}
