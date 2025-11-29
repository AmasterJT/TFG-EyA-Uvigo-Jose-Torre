package uvigo.tfgalmacen;


import static uvigo.tfgalmacen.database.ProductoDAO.getColorByIdentificadorProducto;

public class ProductoPedido {

    private final String identificadorProducto;
    private final int cantidad;


    private final int id_detalle_BDD;
    public Boolean isComplete;
    public Boolean isPaletizado;
    public String colorHEX = "";


    public ProductoPedido(String identificadorProducto, int cantidad, Boolean isComplete, int id_detalle_BDD, Boolean isPaletizado) {
        this.identificadorProducto = identificadorProducto;
        this.cantidad = cantidad;
        this.isComplete = (isComplete != null && isComplete); // maneja null como false o tu lógica
        this.id_detalle_BDD = id_detalle_BDD;
        this.isPaletizado = isPaletizado;
        colorHEX = convertColorToHEX(getColorByIdentificadorProducto(Main.connection, identificadorProducto));
    }

    public String getIdentificadorProducto() {
        return identificadorProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String convertColorToHEX(String color) {
        try {
            // Dividir el color en componentes RGB
            String[] components = color.split(",");

            // Asegurarse de que se tengan exactamente tres componentes
            if (components.length != 3) {
                throw new IllegalArgumentException("Color debe tener 3 componentes (R, G, B).");
            }

            // Convertir cada componente a entero en el rango de 0-255
            int r = (int) (Double.parseDouble(components[0]) * 255);
            int g = (int) (Double.parseDouble(components[1]) * 255);
            int b = (int) (Double.parseDouble(components[2]) * 255);

            // Convertir los valores RGB a hexadecimal

            return String.format("#%02X%02X%02X", r, g, b);
        } catch (Exception e) {
            // En caso de error, devolver un color por defecto en formato HEX
            System.err.println("❌ Error al convertir color: " + e.getMessage());
            return "#808080"; // Color por defecto (gris)
        }
    }

    public int getId_detalle_BDD() {
        return id_detalle_BDD;
    }

    @Override
    public String toString() {
        return "Producto: " + identificadorProducto + ", Cantidad: " + cantidad;
    }
}
