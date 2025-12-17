package uvigo.tfgalmacen.almacenapi.dto;

public class ActualizarPaletRequest {

    // Todos opcionales: si vienen null, no se cambian
    private String idProducto;

    private Integer alto;
    private Integer ancho;
    private Integer largo;

    private Integer cantidadDeProducto;

    private Integer estanteria;
    private Integer balda;
    private Integer posicion;

    private Boolean delante;

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public Integer getAlto() { return alto; }
    public void setAlto(Integer alto) { this.alto = alto; }

    public Integer getAncho() { return ancho; }
    public void setAncho(Integer ancho) { this.ancho = ancho; }

    public Integer getLargo() { return largo; }
    public void setLargo(Integer largo) { this.largo = largo; }

    public Integer getCantidadDeProducto() { return cantidadDeProducto; }
    public void setCantidadDeProducto(Integer cantidadDeProducto) { this.cantidadDeProducto = cantidadDeProducto; }

    public Integer getEstanteria() { return estanteria; }
    public void setEstanteria(Integer estanteria) { this.estanteria = estanteria; }

    public Integer getBalda() { return balda; }
    public void setBalda(Integer balda) { this.balda = balda; }

    public Integer getPosicion() { return posicion; }
    public void setPosicion(Integer posicion) { this.posicion = posicion; }

    public Boolean getDelante() { return delante; }
    public void setDelante(Boolean delante) { this.delante = delante; }
}
