package uvigo.tfgalmacen.gs1;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.time.LocalDate;

public class EtiquetaGS1 {

    // Crea una imagen GS1-128 a partir de una cadena con AIs entre paréntesis
    private static Image gs1Image(PdfWriter writer, String aiString) throws BadElementException {
        Barcode128 bc = new Barcode128();
        bc.setCodeType(Barcode.CODE128_UCC); // <- GS1-128
        bc.setCode(aiString);                // <- con AIs "(01)...(10)...(37)..."
        bc.setFont(null);                    // sin texto human-readable debajo
        return bc.createImageWithBarcode(writer.getDirectContent(), null, null);
    }

    // Escala y centra
    private static void fit(Image img, float maxWidth, float targetHeight, float spacingTop, float spacingBottom) {
        img.scaleToFit(maxWidth, targetHeight);
        img.setAlignment(Element.ALIGN_CENTER);
        img.setSpacingBefore(spacingTop);
        img.setSpacingAfter(spacingBottom);
    }

    public static void main(String[] args) throws Exception {
        // === 1) Construyes tus datos con GS1Utils ===
        String gtin = GS1Utils.generateGTIN14(0, "8412348", 678908L);               // (01)
        String sscc = GS1Utils.generateSSCC(3, "8412348", 12345L);                  // (00)
        String aiProd = GS1Utils.ai01(gtin);                                       // "(01)..."
        String aiCant = GS1Utils.ai37(10);                                         // "(37)10"
        String aiLote = GS1Utils.ai10("A1B2C3");                                   // "(10)A1B2C3"
        String aiCaduc = GS1Utils.ai15(LocalDate.of(2025, 12, 31));                 // "(15)251231"
        String aiPalet = GS1Utils.ai00(sscc);                                       // "(00)..."

        // Ejemplos de cadenas GS1-128:
        // a) Producto con cantidad
        String gs1ProductoSimple = aiProd + aiCant;                       // "(01)...(37)10"
        // b) Producto con lote (variable) + fecha + cantidad
        String gs1ProductoDetallado = aiProd + aiLote + aiCaduc + aiCant; // "(01)...(10)...(15)...(37)10"
        // c) SSCC del palet
        String gs1SSCC = aiPalet;                                         // "(00)..."

        // === 2) Renderizas en PDF ===
        Document doc = new Document(PageSize.A6.rotate());
        doc.setMargins(10, 10, 10, 10);
        PdfWriter writer = PdfWriter.getInstance(doc, new java.io.FileOutputStream("etiqueta-sscc.pdf"));
        doc.open();

        Font f = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        doc.add(new Paragraph("REMITE: Almacen Intermedio Vigo S.L.", f));
        doc.add(new Paragraph("DESTINATARIO: Librería Gallega S.A.", f));
        doc.add(new Paragraph("CONTENIDO: PALLET MIXTO", f));

        float usableWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();

        // --- Código 1: Producto (simple) ---
        Image img1 = gs1Image(writer, gs1ProductoSimple);     // "(01)...(37)..."
        fit(img1, usableWidth, 40f, 6f, 6f);
        doc.add(img1);

        // --- Código 2: Producto (detallado con lote y fecha) ---
        Image img2 = gs1Image(writer, gs1ProductoDetallado);  // "(01)...(10)...(15)...(37)..."
        fit(img2, usableWidth, 40f, 0f, 10f);
        doc.add(img2);

        // (Aquí podrías añadir tu tabla con setSpacingBefore(...) para separar)
        // ...

        // --- Código 3: SSCC del palet ---
        Image img3 = gs1Image(writer, gs1SSCC);               // "(00)..."
        fit(img3, usableWidth, 45f, 8f, 0f);
        doc.add(img3);

        doc.close();
        System.out.println("PDF listo: etiqueta-sscc.pdf");
    }
}
