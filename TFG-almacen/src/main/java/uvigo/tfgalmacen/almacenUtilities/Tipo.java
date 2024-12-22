package uvigo.tfgalmacen.almacenUtilities;
import javafx.scene.paint.Color;

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
    public Color color_javaFx(){

        String[] c = this.getColor().split(",");
        //System.out.println(new Color(Double.parseDouble(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]), 1));
        return new Color(Double.parseDouble(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]), 1);

    }

    @Override
    public  String toString(){
        return Colores.VERDE + "Tipo: " + this.getIdTipo() + " Palets: " + this.getNumPalets() + " Cantidad de produto: " + this.getCantidadDeTipo() + " (" + this.getColor() + ")" + Colores.RESET_COLOR;
    }

}
