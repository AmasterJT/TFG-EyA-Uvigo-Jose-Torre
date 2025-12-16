package uvigo.tfgalmacen.mobile_android.models;

public class CambiarEstadoProductoDto {

    private boolean estadoProductoPedido;

    public CambiarEstadoProductoDto(boolean estadoProductoPedido) {
        this.estadoProductoPedido = estadoProductoPedido;
    }

    public boolean isEstadoProductoPedido() {
        return estadoProductoPedido;
    }

    public void setEstadoProductoPedido(boolean estadoProductoPedido) {
        this.estadoProductoPedido = estadoProductoPedido;
    }
}
