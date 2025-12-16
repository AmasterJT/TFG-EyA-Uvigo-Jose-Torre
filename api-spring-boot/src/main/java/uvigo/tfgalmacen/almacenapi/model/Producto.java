package uvigo.tfgalmacen.almacenapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "identificador_producto", nullable = false, unique = true)
    private String identificadorProducto;

    @Column(name = "tipo_producto", nullable = false)
    private String tipoProducto;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // getters & setters

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdentificadorProducto() {
        return identificadorProducto;
    }

    public void setIdentificadorProducto(String identificadorProducto) {
        this.identificadorProducto = identificadorProducto;
    }
}
