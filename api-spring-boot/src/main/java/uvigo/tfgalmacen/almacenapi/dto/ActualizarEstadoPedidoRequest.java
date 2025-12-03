package uvigo.tfgalmacen.almacenapi.dto;

public class ActualizarEstadoPedidoRequest {

    private String estado;      // "Pendiente", "Completado", "En proceso", "Cancelado", "Enviado"
    private String horaSalida;  // "primera_hora" o "segunda_hora" (solo para "En proceso")
    private Integer idUsuario;  // solo para "En proceso"

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}
