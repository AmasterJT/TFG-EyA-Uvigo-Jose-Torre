package uvigo.tfgalmacen.almacenManagement;
import javafx.scene.paint.Color;
import uvigo.tfgalmacen.utils.TerminalColors;

public class Tipo {

    private String color, idTipo;
    private Color color_javafx;
    private int cantidadDeTipo = 0, numPalets = 0;

    // =========================CONSTRUCTORES===============================
    public Tipo(String color, String idTipo){
        this.color = color;
        this.idTipo = idTipo;
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

    @Override
    public  String toString(){
        return TerminalColors.VERDE + "Tipo: " + this.getIdTipo() + " Palets: " + this.getNumPalets() + " Cantidad de produto: " + this.getCantidadDeTipo() + " (" + this.getColor() + ")" + TerminalColors.RESET;
    }

}
