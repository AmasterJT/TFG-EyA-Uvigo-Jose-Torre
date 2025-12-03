package uvigo.tfgalmacen.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfUtils {

    public static Image cargarPrimeraPaginaComoImagen(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            // página 0, 150 dpi
            BufferedImage bim = renderer.renderImageWithDPI(0, 150);
            return SwingFXUtils.toFXImage(bim, null);
        }
    }
}
