package uvigo.tfgalmacen.almacenapi.dto;

public class ActualizarDimensionesPaletRequest {
    private Integer alto;
    private Integer ancho;
    private Integer largo;

    public Integer getAlto() { return alto; }
    public void setAlto(Integer alto) { this.alto = alto; }

    public Integer getAncho() { return ancho; }
    public void setAncho(Integer ancho) { this.ancho = ancho; }

    public Integer getLargo() { return largo; }
    public void setLargo(Integer largo) { this.largo = largo; }
}
