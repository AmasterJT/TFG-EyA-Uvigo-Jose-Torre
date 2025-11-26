package uvigo.tfgalmacen.gs1;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.Barcode128;

import java.io.OutputStream;

/**
 * Generación de etiqueta logística GS1 para PALET MIXTO.
 * <p>
 * - Un único código de barras GS1-128 con AI(00) SSCC.
 * - El detalle de productos va en tabla / packing list (NO en el código de barras).
 */
public class EtiquetaGS1 {

    // Crea una imagen GS1-128 a partir de una cadena con AIs entre paréntesis
    private static Image gs1Image(PdfWriter writer, String aiString) throws BadElementException {
        Barcode128 bc = new Barcode128();
        bc.setCodeType(Barcode128.CODE128_UCC); // GS1-128
        bc.setCode(aiString);                   // Ej: "(00)3xxxxxxxxxxxxxxx"
        bc.setFont(null);                       // sin texto human-readable debajo
        return bc.createImageWithBarcode(writer.getDirectContent(), null, null);
    }

    // Escala y centra
    private static void fit(Image img, float maxWidth, float targetHeight, float spacingTop, float spacingBottom) {
        img.scaleToFit(maxWidth, targetHeight);
        img.setAlignment(Element.ALIGN_CENTER);
        img.setSpacingBefore(spacingTop);
        img.setSpacingAfter(spacingBottom);
    }

    /**
     * Crea un PDF A6 apaisado con:
     * - Remitente / destinatario / descripción
     * - Código GS1-128 con SSCC (AI(00))
     *
     * @param sscc             SSCC (18 dígitos, sin AI)
     * @param remitente        texto libre
     * @param destinatario     texto libre
     * @param descripcionCarga ej. "PALET MIXTO", "PALET GRANEL", etc.
     * @param out              OutputStream donde escribir el PDF
     */
    public static void crearEtiquetaPaletMixto(String sscc,
                                               String remitente,
                                               String destinatario,
                                               String descripcionCarga,
                                               OutputStream out) throws Exception {

        // Validación SSCC
        if (sscc == null || sscc.length() != 18 || !sscc.matches("\\d+")) {
            throw new IllegalArgumentException("SSCC debe tener 18 dígitos numéricos");
        }

        String aiSSCC = GS1Utils.ai00(sscc); // "(00)3xxxxxxxxxxxxxxx"

        Document doc = new Document(PageSize.A6.rotate());
        doc.setMargins(10, 10, 10, 10);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        Font fHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font fNormal = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        if (remitente != null && !remitente.isEmpty()) {
            doc.add(new Paragraph("REMITE: " + remitente, fHeader));
        }
        if (destinatario != null && !destinatario.isEmpty()) {
            doc.add(new Paragraph("DESTINATARIO: " + destinatario, fHeader));
        }
        if (descripcionCarga != null && !descripcionCarga.isEmpty()) {
            doc.add(new Paragraph("CONTENIDO: " + descripcionCarga, fHeader));
        }

        doc.add(new Paragraph(" ", fNormal)); // pequeño espacio

        float usableWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();

        // --- ÚNICO código GS1-128: SSCC del palet ---
        Image imgSSCC = gs1Image(writer, aiSSCC);   // "(00)..."
        fit(imgSSCC, usableWidth, 45f, 8f, 0f);
        doc.add(imgSSCC);

        doc.close();
    }


}
