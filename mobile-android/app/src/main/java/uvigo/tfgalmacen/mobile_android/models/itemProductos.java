package uvigo.tfgalmacen.mobile_android.models;

public class itemProductos {

    private String nombreProducto;
    private String Cantidad;

    private int id_pedido;

    public itemProductos(String name, String time) {
        this.nombreProducto = name;
        this.Cantidad = time;
    }

    public String getName() {
        return nombreProducto;
    }

    public String getTime() {
        return Cantidad;
    }

    public int getId_pedido() {
        return id_pedido;
    }
}
