package uvigo.tfgalmacen.almacenapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "estado_producto_pedido")
    private Boolean estadoProductoPedido;

    @Column(name = "paletizado")
    private Boolean paletizado;

    // getters y setters

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Boolean getEstadoProductoPedido() { return estadoProductoPedido; }
    public void setEstadoProductoPedido(Boolean estadoProductoPedido) { this.estadoProductoPedido = estadoProductoPedido; }

    public Boolean getPaletizado() { return paletizado; }
    public void setPaletizado(Boolean paletizado) { this.paletizado = paletizado; }
}
