package uvigo.tfgalmacen.almacenapi.dto;

public class ActualizarUbicacionPaletRequest {

    private Integer estanteria;
    private Integer balda;
    private Integer posicion;
    private Boolean delante;

    public Integer getEstanteria() { return estanteria; }
    public void setEstanteria(Integer estanteria) { this.estanteria = estanteria; }

    public Integer getBalda() { return balda; }
    public void setBalda(Integer balda) { this.balda = balda; }

    public Integer getPosicion() { return posicion; }
    public void setPosicion(Integer posicion) { this.posicion = posicion; }

    public Boolean getDelante() { return delante; }
    public void setDelante(Boolean delante) { this.delante = delante; }
}
