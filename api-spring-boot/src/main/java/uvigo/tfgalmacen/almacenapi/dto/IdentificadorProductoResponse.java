package uvigo.tfgalmacen.almacenapi.dto;

public class IdentificadorProductoResponse {

    private String identificadorProducto;

    public IdentificadorProductoResponse(String identificadorProducto) {
        this.identificadorProducto = identificadorProducto;
    }

    public String getIdentificadorProducto() {
        return identificadorProducto;
    }

    public void setIdentificadorProducto(String identificadorProducto) {
        this.identificadorProducto = identificadorProducto;
    }
}
