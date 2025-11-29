package uvigo.tfgalmacen;

public class PaletSalida {


    private int idPaletSalida;
    private int idPedido;
    private String sscc;
    private int cantidadTotal;
    private int numeroProductos;


    public int getIdPaletSalida() {
        return idPaletSalida;
    }

    public void setIdPaletSalida(int idPaletSalida) {
        this.idPaletSalida = idPaletSalida;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getSscc() {
        return sscc;
    }

    public void setSscc(String sscc) {
        this.sscc = sscc;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public int getNumeroProductos() {
        return numeroProductos;
    }

    public void setNumeroProductos(int numeroProductos) {
        this.numeroProductos = numeroProductos;
    }
    
    public PaletSalida(int idPaletSalida, int idPedido, String sscc, int cantidadTotal, int numeroProductos) {
        this.idPaletSalida = idPaletSalida;
        this.idPedido = idPedido;
        this.sscc = sscc;
        this.cantidadTotal = cantidadTotal;
        this.numeroProductos = numeroProductos;
    }
}
