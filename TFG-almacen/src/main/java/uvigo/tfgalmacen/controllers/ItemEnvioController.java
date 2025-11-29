package uvigo.tfgalmacen.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import uvigo.tfgalmacen.PaletSalida;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.gs1.EtiquetaGS1;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DataConfig.COMPANY_NAME;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

import net.sf.jasperreports.engine.*;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import uvigo.tfgalmacen.PaletSalida;
import uvigo.tfgalmacen.Pedido;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ItemEnvioController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ItemEnvioController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        var ch = new java.util.logging.ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (var h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    private boolean tieneEtiqueta = false;

    String color_sin_etiqueta = "#c30d00";
    String color_con_etiqueta = "#38a107";


    @FXML
    private Label cliente_label;

    @FXML
    private Label codigo_pedido_label;


    @FXML
    private Button generar_etiqueta_btn;

    @FXML
    private CheckBox seleccionar_generar_etiqueta_check;

    @FXML
    private Label sscc_label;


    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Circle indicador_etiqueta;

    private apartadoEnvioController parent;
    private File etiquetaFile;
    PaletSalida palet_salida;
    Pedido pedido;

    File salida;


    // Más adelante:
    // public void setData(Pedido pedido, PaletSalida palet) { ... }

    public void setData(Pedido pedido, PaletSalida palet) {
        this.palet_salida = palet;
        this.pedido = pedido;
        sscc_label.setText(palet.getSscc());
        codigo_pedido_label.setText(pedido.getCodigo_referencia());
        cliente_label.setText(pedido.getNombre_cliente());


        // --- Verificar si la etiqueta existe ---
        boolean existe = etiquetaExiste(palet.getSscc());


        Path etiquetasDir = Paths.get(
                System.getProperty("user.home"),
                "Downloads",
                "Etiquetas"
        );

        // Ajusta el nombre si en tu generación usas otro patrón (p. ej. "Etiqueta_"+sscc+".pdf")
        etiquetaFile = etiquetasDir.resolve("ETIQUETA_" + palet.getSscc() + ".pdf").toFile();
        tieneEtiqueta = etiquetaFile.exists();

        if (existe) {
            indicador_etiqueta.setFill(Paint.valueOf(color_con_etiqueta));
            tieneEtiqueta = true;
        } else {
            indicador_etiqueta.setFill(Paint.valueOf(color_sin_etiqueta));
            tieneEtiqueta = false;
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generar_etiqueta_btn.setOnAction(_ -> {
            generarEtiqueta();

            ventana_success("Etiqueta del palet",
                    "Etiqueta generada correctamente:\n\n" +
                            salida.getAbsolutePath(), ""
            );
        });

        if (tieneEtiqueta) {
            indicador_etiqueta.setFill(Paint.valueOf(color_con_etiqueta));

        } else {
            indicador_etiqueta.setFill(Paint.valueOf(color_sin_etiqueta));
        }


        // Click en todo el item → intentar mostrar el PDF si existe
        backgroundAnchorPane.setOnMouseClicked(event -> {
            if (tieneEtiqueta && etiquetaFile != null && etiquetaFile.exists()) {
                // Mostrar la etiqueta correspondiente a ESTE item
                if (parent != null) {
                    parent.mostrarPdf(etiquetaFile, this);
                }
            } else {
                // No hay etiqueta → si este item era el que se estaba mostrando, se limpia la imagen
                if (parent != null) {
                    parent.limpiarPdfSiMostradoDesde(this);
                }
                System.out.println("No hay etiqueta generada para SSCC: " + sscc_label.getText());
            }
        });


    }


    public void generarEtiqueta() {
        String sscc = sscc_label.getText();
        String codPedido = codigo_pedido_label.getText();
        String cliente = cliente_label.getText();
        if (sscc == null || sscc.isBlank()) {
            System.out.println("SSCC vacío, no se puede generar etiqueta.");
            return;
        }

        // 1) Carpeta destino: Descargas/Etiquetas
        File carpetaEtiquetas = getCarpetaEtiquetas();
        // Nombre de fichero, por ejemplo:
        String fileName = "ETIQUETA_" + sscc + ".pdf";
        salida = new File(carpetaEtiquetas, fileName);

        // 2) Si ya existe, preguntar si queremos sustituir
        if (salida.exists()) {
            boolean sobrescribir = ventana_confirmacion(
                    "Etiqueta ya existente",
                    "Ya existe una etiqueta para el SSCC:\n\n" + sscc +
                            "\n\nFichero:\n" + salida.getAbsolutePath() +
                            "\n\n¿Quieres reemplazarla?"
            );

            if (!sobrescribir) {
                System.out.println("Usuario canceló la sobrescritura de la etiqueta.");
                return;
            }
        }


        // 3) Generar PDF usando OutputStream
        try (OutputStream os = new FileOutputStream(salida)) {

            // Aquí llamas a tu clase EtiquetaGS1.
            // ADAPTA esta línea a la firma real de tu método.
            EtiquetaGS1.crearEtiquetaPaletMixto(
                    sscc,
                    COMPANY_NAME,
                    cliente,
                    codPedido,
                    os      // <-- OutputStream requerido
            );
            // Ejemplo genérico:
            // etiqueta.generarEtiqueta(os, sscc, ...otrosDatosQueNecesites...);

            System.out.println("Etiqueta generada en: " + salida.getAbsolutePath());

            tieneEtiqueta = true;

            indicador_etiqueta.setFill(Paint.valueOf(color_con_etiqueta));


        } catch (IOException e) {
            Logger.getLogger(ItemEnvioController.class.getName())
                    .log(Level.SEVERE, "Error generando etiqueta PDF para SSCC " + sscc, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /*
      private void generarEtiqueta() {
        try {
            String sscc = sscc_label.getText();           // del item
            String pedido = codigo_pedido_label.getText();  // del item
            String cliente = cliente_label.getText();       // del item

            // Si quieres, puedes recuperar más datos del pedido/cliente desde la BDD (dirección, fecha, etc.)
            String direccion = ""; // TODO: cargar de ClienteDAO
            String fecha = java.time.LocalDate.now().toString();
            String textoExtra = "Palet de salida";

            // Cargar el .jrxml compilado (.jasper). Puedes compilarlo una vez al inicio.
            JasperReport reporte = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/uvigo/tfgalmacen/gs1/etiqueta_palet_gs1.jrxml")
            );

            Map<String, Object> params = new HashMap<>();
            params.put("SSCC", sscc);
            params.put("CODIGO_PEDIDO", pedido);
            params.put("CLIENTE", cliente);
            params.put("DIRECCION", direccion);
            params.put("FECHA", fecha);
            params.put("TEXTO_EXTRA", textoExtra);

            JasperPrint print = JasperFillManager.fillReport(reporte, params, new JREmptyDataSource());

            // Ruta de salida (puedes usar FileChooser o una carpeta fija tipo "output_labels")
            java.io.File carpeta = new java.io.File("output_labels");
            carpeta.mkdirs();
            java.io.File salida = new java.io.File(carpeta, "etiqueta_" + sscc + ".pdf");

            try (OutputStream os = new java.io.FileOutputStream(salida)) {
                JasperExportManager.exportReportToPdfStream(print, os);
            }

            System.out.println("Etiqueta generada: " + salida.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public Button getGenerar_etiqueta_btn() {
        return generar_etiqueta_btn;
    }

    public void setGenerar_etiqueta_btn(Button generar_etiqueta_btn) {
        this.generar_etiqueta_btn = generar_etiqueta_btn;
    }

    public boolean estaSeleccionado() {
        return seleccionar_generar_etiqueta_check.isSelected();
    }

    public String getSscc() {
        return sscc_label.getText();
    }


    private File getCarpetaEtiquetas() {
        String userHome = System.getProperty("user.home");

        // Intentamos primero "Downloads", luego "Descargas"
        File downloads = new File(userHome, "Downloads");
        if (!downloads.exists()) {
            downloads = new File(userHome, "Descargas");
        }

        // Si tampoco existe, usamos directamente el home
        File base = downloads.exists() ? downloads : new File(userHome);

        File etiquetasDir = new File(base, "Etiquetas");
        if (!etiquetasDir.exists()) {
            boolean ok = etiquetasDir.mkdirs();
            if (!ok) {
                Logger.getLogger(ItemEnvioController.class.getName())
                        .warning("No se pudo crear la carpeta de etiquetas en: " + etiquetasDir.getAbsolutePath());
            }
        }

        return etiquetasDir;
    }


    public boolean isTieneEtiqueta() {
        return tieneEtiqueta;
    }

    public void setTieneEtiqueta(boolean tieneEtiqueta) {
        this.tieneEtiqueta = tieneEtiqueta;
    }

    public int getIdPedido() {
        return pedido.getId_pedido();
    }

    private boolean etiquetaExiste(String sscc) {
        File carpeta = getCarpetaEtiquetas();
        File pdf = new File(carpeta, "etiqueta_" + sscc + ".pdf");
        return pdf.exists();
    }

    public void setApartadoEnvioParent(apartadoEnvioController parent) {
        this.parent = parent;
    }

    public void setSeleccionado(boolean seleccionado) {
        if (seleccionar_generar_etiqueta_check != null) {
            seleccionar_generar_etiqueta_check.setSelected(seleccionado);
        }
    }


}
