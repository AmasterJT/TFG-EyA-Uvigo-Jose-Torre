package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class PedidoDAO {

    // Crear pedido
    private static final String INSERT_PEDIDO_SQL = "INSERT INTO pedidos (id_usuario, estado) VALUES (?, ?)";

    public static void createPedido(Connection connection, int id_usuario, String estado) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PEDIDO_SQL)) {
            preparedStatement.setInt(1, id_usuario);
            preparedStatement.setString(2, estado);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Pedido creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer pedidos
    private static final String SELECT_ALL_PEDIDOS_SQL = "SELECT * FROM pedidos";

    public static void printPedidosData(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PEDIDOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codigo_referencia = resultSet.getString("codigo_referencia");
                int id_pedido = resultSet.getInt("id_pedido");
                int id_usuario = resultSet.getInt("id_usuario");
                String estado = resultSet.getString("estado");

                System.out.println(CYAN + "Codigo Referencia: " + RESET + codigo_referencia + CYAN + ", ID: " + RESET + id_pedido + CYAN + ", Usuario ID: " + RESET + id_usuario + CYAN + ", Estado: " + RESET + estado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Pedido> getPedidosAllData(Connection connection) {

        List<Pedido> pedidos= new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PEDIDOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codigo_referencia = resultSet.getString("codigo_referencia");
                int id_pedido = resultSet.getInt("id_pedido");
                int id_usuario = resultSet.getInt("id_usuario");
                String estado = resultSet.getString("estado");

                Pedido pedido = new Pedido(codigo_referencia, id_pedido, id_usuario, estado);
                pedidos.add(pedido);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }


    private static final String SELECT_PEDIDOS_BY_ESTADO_SQL = "SELECT * FROM pedidos WHERE estado = ?";

    public static List<Pedido> getPedidosPendientes(Connection connection) {
        return getPedidosPorEstado(connection, "Pendiente");
    }

    public static List<Pedido> getPedidosEnProceso(Connection connection) {
        return getPedidosPorEstado(connection, "En proceso");
    }

    public static List<Pedido> getPedidosCancelados(Connection connection) {
        return getPedidosPorEstado(connection, "Cancelado");
    }

    public static List<Pedido> getPedidosCompletados(Connection connection) {
        return getPedidosPorEstado(connection, "Completado");
    }

    // Método reutilizable para evitar duplicación de código
    private static List<Pedido> getPedidosPorEstado(Connection connection, String estado) {
        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PEDIDOS_BY_ESTADO_SQL)) {
            preparedStatement.setString(1, estado);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codigo_referencia = resultSet.getString("codigo_referencia");
                int id_pedido = resultSet.getInt("id_pedido");
                int id_usuario = resultSet.getInt("id_usuario");
                String estadoResult = resultSet.getString("estado");

                Pedido pedido = new Pedido(codigo_referencia, id_pedido, id_usuario, estadoResult);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }
}