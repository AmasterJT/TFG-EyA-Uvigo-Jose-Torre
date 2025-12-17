package uvigo.tfgalmacen.mobile_android.models;

public class CambiarPaletsPedidoDto {
    private int paletsDelPedido;

    public CambiarPaletsPedidoDto(int paletsDelPedido) {
        this.paletsDelPedido = paletsDelPedido;
    }

    public int getPaletsDelPedido() { return paletsDelPedido; }
}
