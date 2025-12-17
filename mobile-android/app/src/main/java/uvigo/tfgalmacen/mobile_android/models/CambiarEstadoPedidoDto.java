package uvigo.tfgalmacen.mobile_android.models;

public class CambiarEstadoPedidoDto {
    private String estado;

    public CambiarEstadoPedidoDto(String estado) {
        this.estado = estado;
    }

    public String getEstado() { return estado; }
}
