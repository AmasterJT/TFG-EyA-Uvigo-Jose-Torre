package uvigo.tfgalmacen.mobile_android.models;

public class DetallePedidoDto {
    private int idDetalle;
    private int idProducto;
    private int cantidad;
    private boolean estadoProductoPedido;

    public int getIdProducto() { return idProducto; }
    public int getCantidad() { return cantidad; }
    public boolean isEstadoProductoPedido() { return estadoProductoPedido; }

    public void setEstadoProductoPedido(boolean estadoProductoPedido) {
        this.estadoProductoPedido = estadoProductoPedido;
    }

    public int getIdDetalle() { return idDetalle; }
}
