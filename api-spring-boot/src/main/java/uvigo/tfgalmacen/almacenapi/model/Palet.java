package uvigo.tfgalmacen.almacenapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "palets")
public class Palet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_palet")
    private Integer idPalet;

    @Column(name = "identificador", nullable = false, unique = true)
    private String identificador;

    // OJO: en tu tabla, id_producto referencia a productos(identificador_producto)
    // Es un VARCHAR, no un INT
    @Column(name = "id_producto", nullable = false)
    private String idProducto; // aquí guardas el identificador_producto

    @Column(name = "alto", nullable = false)
    private Integer alto;

    @Column(name = "ancho", nullable = false)
    private Integer ancho;

    @Column(name = "largo", nullable = false)
    private Integer largo;

    @Column(name = "cantidad_de_producto", nullable = false)
    private Integer cantidadDeProducto;

    @Column(name = "estanteria", nullable = false)
    private Integer estanteria;

    @Column(name = "balda", nullable = false)
    private Integer balda;

    @Column(name = "posicion", nullable = false)
    private Integer posicion;

    @Column(name = "delante", nullable = false)
    private Boolean delante;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Getters / Setters

    public Integer getIdPalet() { return idPalet; }
    public void setIdPalet(Integer idPalet) { this.idPalet = idPalet; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

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

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
