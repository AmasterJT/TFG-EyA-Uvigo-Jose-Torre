package uvigo.tfgalmacen.almacenapi.dto;

public class ActualizarEstadoPedidoRequest {
    private String estado; // "Pendiente", "En proceso", "Completado", etc.

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
