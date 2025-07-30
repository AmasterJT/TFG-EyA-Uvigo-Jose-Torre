package uvigo.tfgalmacen.almacenManagement;


import uvigo.tfgalmacen.utils.TerminalColors;

public class Producto {
    private String identificadorProducto, idTipo;
    private int numPalets = 0, cantidadDeProducto = 0;

    public Tipo tipo = null;

    // =========================CONSTRUCTORES===============================
    public Producto(String idProducto, String idTipo) {
        this.identificadorProducto = idProducto;
        this.idTipo = idTipo;
    }
    //======================================================================

    //=======================GETTERS Y SETTERS =============================
    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    public String getIdentificadorProducto() {
        return identificadorProducto;
    }

    public void setIdentificadorProducto(String identificadorProducto) {
        this.identificadorProducto = identificadorProducto;
    }

    public int getNumPalets() {
        return numPalets;
    }

    public void setNumPalets(int numPalets) {
        this.numPalets = numPalets;
    }

    public int getCantidadDeProducto() {
        return cantidadDeProducto;
    }

    public void setCantidadDeProducto(int litros) {
        this.cantidadDeProducto = litros;
    }
    //======================================================================

    @Override
    public String toString() {
        return TerminalColors.AMARILLO + "Producto: " + "id:" + this.getIdentificadorProducto() +
                " tipo:" + this.getIdTipo() +
                " Palets: " + this.getNumPalets() +
                " Cantidad de producto: " + this.getCantidadDeProducto() + TerminalColors.RESET;
    }

    public void setTipo(Tipo tipo) {

        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }
}