package uvigo.tfgalmacen.mobile_android.models;

public class itemProductos {

    private final int idProducto;
    private String name;      // identificadorProducto
    private final String Cantidad; // cantidad

    public itemProductos(int idProducto, String name, String Cantidad) {
        this.idProducto = idProducto;
        this.name = name;
        this.Cantidad = Cantidad;
    }

    public int getIdProducto() { return idProducto; }

    public String getName() { return name; }
    public String getTime() { return Cantidad; }

    public void setName(String name) { this.name = name; }
}
