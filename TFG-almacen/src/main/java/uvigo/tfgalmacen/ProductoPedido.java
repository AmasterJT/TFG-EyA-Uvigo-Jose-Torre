package uvigo.tfgalmacen;

public class ProductoPedido {

    private String identificadorProducto;
    private int cantidad;

    public ProductoPedido(String identificadorProducto, int cantidad) {
        this.identificadorProducto = identificadorProducto;
        this.cantidad = cantidad;
    }

    public String getIdentificadorProducto() {
        return identificadorProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public String toString() {
        return "Producto: " + identificadorProducto + ", Cantidad: " + cantidad;
    }
}
