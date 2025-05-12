package uvigo.tfgalmacen;

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
    private final int id_usuario;
    private String estado;

    public Pedido(String codigo_referencia, int id_pedido, int id_usuario, String estado, String fechaPedidoRaw) {
        this.codigo_referencia = codigo_referencia;
        this.id_pedido = id_pedido;
        this.id_usuario = id_usuario;
        this.estado = estado;
        this.fechaPedidoRaw =fechaPedidoRaw;
        this.fechaPedido = LocalDateTime.parse(fechaPedidoRaw, FORMATTER);

        switch (estado) {
            case "Pendiente" -> emoji = "📌";
            case "En proceso" -> emoji = "⏳" + NARANJA;
            case "Cancelado" -> emoji = "❌" + ROJO;
            case "Completado" -> emoji = "✅" + VERDE;
        }
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

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }


    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return CYAN + "Codigo Referencia: " + RESET +
                codigo_referencia + CYAN + ", Fecha Creacion: " + RESET +
                fechaPedido + CYAN + ", ID: " + RESET +
                id_pedido + CYAN + ", Usuario ID: " + RESET +
                id_usuario + CYAN + ", Estado: " + RESET +
                emoji + " "+ estado + RESET;
    }

    @Override
    public int compareTo(Pedido other) {
        return this.fechaPedido.compareTo(other.fechaPedido); // ascendente
    }
}

