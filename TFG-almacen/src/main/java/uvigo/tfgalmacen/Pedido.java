package uvigo.tfgalmacen;

import uvigo.tfgalmacen.database.ClientesDAO;
import uvigo.tfgalmacen.database.PedidoDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class Pedido implements Comparable<Pedido> {

    List<String> ESTADOS_DEL_PEDIDO = List.of("Pendiente", "En proceso", "Cancelado", "Completado");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private String emoji;
    private String fechaPedidoRaw;
    private final LocalDateTime fechaPedido;

    private final String codigo_referencia;
    private final int id_pedido;
    private final int id_cliente;
    private int id_usuario;
    private String estado;

    private final String horaSalida;


    private String colorEstadoHEX;


    private final String nombre_cliente;

    public Pedido(String codigo_referencia, int id_pedido, int id_cliente, int id_usuario, String estado, String fechaPedidoRaw, String hora_salida) {
        this.codigo_referencia = codigo_referencia;
        this.id_pedido = id_pedido;
        this.id_cliente = id_cliente;
        this.id_usuario = id_usuario;
        this.estado = estado;
        this.fechaPedidoRaw = fechaPedidoRaw;
        this.fechaPedido = LocalDateTime.parse(fechaPedidoRaw, FORMATTER);
        this.horaSalida = hora_salida;


        this.nombre_cliente = setNombreCliente(id_cliente); // Inicializar como null, se puede establecer más tarde si es necesario


        setDataEstado(estado);

    }


    private void setDataEstado(String estado) {
        switch (estado) {
            case "Pendiente" -> {
                this.colorEstadoHEX = "#bfbfbf";
                this.emoji = "📌";
            }
            case "En proceso" -> {
                this.colorEstadoHEX = "#edf55f";
                this.emoji = "⏳" + NARANJA;
            }
            case "Cancelado" -> {
                this.colorEstadoHEX = "#9e3a2c";
                this.emoji = "❌" + ROJO;
            }
            case "Completado" -> {
                this.colorEstadoHEX = "#4bb030";
                this.emoji = "✅" + VERDE;
            }
        }
    }

    private String setNombreCliente(int idCliente) {

        return ClientesDAO.getNombreClienteById(Main.connection, idCliente);
    }


    public String getCodigo_referencia() {
        return codigo_referencia;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public String getColorEstadoHEX() {
        return colorEstadoHEX;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setUsuario(User nombre_usuario) {
        this.id_usuario = UsuarioDAO.getIdUsuarioByNombre(Main.connection, nombre_usuario.getUsername());
    }


    @Override
    public String toString() {
        return CYAN + "Codigo Referencia: " + RESET + codigo_referencia +
                CYAN + ", Fecha Creacion: " + RESET + fechaPedido +
                CYAN + ", ID: " + RESET + id_pedido +
                CYAN + ", Nombre Cliente: " + RESET + nombre_cliente +
                CYAN + ", Usuario ID: " + RESET + id_usuario +
                CYAN + ", Estado: " + RESET + emoji + " " + estado + RESET;
    }

    @Override
    public int compareTo(Pedido other) {
        return this.fechaPedido.compareTo(other.fechaPedido); // ascendente
    }
}

