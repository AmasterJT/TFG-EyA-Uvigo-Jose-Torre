package uvigo.tfgalmacen;

import java.util.List;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class Pedido {

    List<String> ESTADOS_DEL_PEDIDO = List.of("Pendiente", "En proceso", "Cancelado", "Completado");
    private String emoji;

    public String codigo_referencia;
    public int id_pedido;
    public int id_usuario;
    public String estado;

    public Pedido(String codigo_referencia, int id_pedido, int id_usuario, String estado) {
        this.codigo_referencia = codigo_referencia;
        this.id_pedido = id_pedido;
        this.id_usuario = id_usuario;
        this.estado = estado;

        switch (estado) {
            case "Pendiente" -> emoji = "📌";
            case "En proceso" -> emoji = "⏳" + NARANJA;
            case "Cancelado" -> emoji = "❌" + ROJO;
            case "Completado" -> emoji = "✅" + VERDE;
        }
    }

    @Override
    public String toString() {
        return CYAN + "Codigo Referencia: " + RESET + codigo_referencia + CYAN + ", ID: " + RESET + id_pedido + CYAN + ", Usuario ID: " + RESET + id_usuario + CYAN + ", Estado: " + RESET + emoji + " "+ estado + RESET;
    }
}
