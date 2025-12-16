package uvigo.tfgalmacen.mobile_android.models;

public class itemProductos {

    private final int idProducto;
    private String name;      // identificadorProducto
    private final String Cantidad; // cantidad

    private boolean estadoProductoPedido;
    private final int idDetalle;



    public itemProductos(int idDetalle, int idProducto, String name, String Cantidad, boolean estadoProductoPedido) {
        this.idDetalle = idDetalle;
        this.idProducto = idProducto;
        this.name = name;
        this.Cantidad = Cantidad;
        this.estadoProductoPedido = estadoProductoPedido;
    }

    public int getIdProducto() { return idProducto; }

    public String getName() { return name; }
    public String getTime() { return Cantidad; }

    public String getCantidad() {
        return Cantidad;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public boolean isEstadoProductoPedido() {
        return estadoProductoPedido;
    }

    public void setEstadoProductoPedido(boolean estadoProductoPedido) {
        this.estadoProductoPedido = estadoProductoPedido;
    }

    public void setName(String name) { this.name = name; }
}
