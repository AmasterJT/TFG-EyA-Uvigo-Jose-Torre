package uvigo.tfgalmacen.almacenManagement;
import javafx.scene.paint.Color;
import uvigo.tfgalmacen.utils.TerminalColors;

public class Tipo {

    private String color, idTipo;
    private Color color_javafx;
    private int cantidadDeTipo = 0, numPalets = 0;

    public String colorHEX;

    // =========================CONSTRUCTORES===============================
    public Tipo(String color, String idTipo){
        this.color = color;
        this.idTipo = idTipo;

        colorHEX = convertColorToHEX(color);
    }
    //======================================================================

    //=======================GETTERS Y SETTERS =============================
    public String getColor() {
        return color;
    }

    public Color getColor_javafx() {
        return color_javafx;
    }

    public void setColor_javafx(Color color_javafx) {
        this.color_javafx = color_javafx;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    public int getCantidadDeTipo() {
        return cantidadDeTipo;
    }

    public void setCantidadDeTipo(int cantidadDeTipo) {
        this.cantidadDeTipo = cantidadDeTipo;
    }

    public int getNumPalets() {
        return numPalets;
    }

    public void setNumPalets(int numPalets) {
        this.numPalets = numPalets;
    }
    //==========================================================================

    //==================================METODOS=================================
 /*
    public Color color_javaFx(){

        String[] c = this.getColor().split(",");
        //System.out.println(new Color(Double.parseDouble(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]), 1));
        return new Color(Double.parseDouble(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]), 1);

    }
*/
    public Color color_javaFx() {
        if (this.color_javafx == null) {
            try {
                String[] c = this.getColor().split(",");
                this.color_javafx = new Color(
                        Double.parseDouble(c[0]),
                        Double.parseDouble(c[1]),
                        Double.parseDouble(c[2]),
                        1.0
                );
            } catch (Exception e) {
                System.err.println("❌ Error al parsear color en Tipo (" + this.idTipo + "): " + e.getMessage());
                this.color_javafx = Color.GRAY; // Color por defecto
            }
        }
        return this.color_javafx;
    }


    public String convertColorToHEX(String color) {
        try {
            // Dividir el color en componentes RGB
            String[] components = color.split(",");
            // Asegurarse de que se tengan exactamente tres componentes
            if (components.length != 3) {
                throw new IllegalArgumentException("Color debe tener 3 componentes (R, G, B).");
            }

            // Convertir cada componente a entero en el rango de 0-255
            int r = (int) (Double.parseDouble(components[0]) * 255);
            int g = (int) (Double.parseDouble(components[1]) * 255);
            int b = (int) (Double.parseDouble(components[2]) * 255);

            // Convertir los valores RGB a hexadecimal
            String hex = String.format("#%02X%02X%02X", r, g, b);

            return hex;
        } catch (Exception e) {
            // En caso de error, devolver un color por defecto en formato HEX
            System.err.println("❌ Error al convertir color: " + e.getMessage());
            return "#808080"; // Color por defecto (gris)
        }
    }

    public String applyPastelFilter(String hexColor) {
        // Elimina el "#" si está presente
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }

        // Parsear componentes RGB desde hexadecimal
        int r = Integer.parseInt(hexColor.substring(0, 2), 16);
        int g = Integer.parseInt(hexColor.substring(2, 4), 16);
        int b = Integer.parseInt(hexColor.substring(4, 6), 16);

        // Mezclar con blanco (255) para suavizar: factor 0.5 para pastel
        r = (r + 255) / 2;
        g = (g + 255) / 2;
        b = (b + 255) / 2;

        // Asegurar que los valores estén en el rango [0, 255]
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        // Convertir de nuevo a HEX y devolver
        return String.format("#%02X%02X%02X", r, g, b);
    }


    @Override
    public  String toString(){
        return TerminalColors.VERDE + "Tipo: " + this.getIdTipo() + " Palets: " + this.getNumPalets() + " Cantidad de produto: " + this.getCantidadDeTipo() + " (" + this.getColor() + ")" + TerminalColors.RESET;
    }

}
