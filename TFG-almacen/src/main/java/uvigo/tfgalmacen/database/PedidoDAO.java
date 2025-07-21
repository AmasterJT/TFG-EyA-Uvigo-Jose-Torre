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

    /**
     * Obtiene una lista de todos los pedidos con todos sus datos.
     * @param connection La conexión a la base de datos.
     * @return Una lista de objetos Pedido con todos sus datos.
     */
    public static List<Pedido> getPedidosAllData(Connection connection) {

        List<Pedido> pedidos= new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PEDIDOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codigo_referencia = resultSet.getString("codigo_referencia");
                int id_pedido = resultSet.getInt("id_pedido");
                int id_cliente = resultSet.getInt("id_cliente");
                int id_usuario = resultSet.getInt("id_usuario");
                String estado = resultSet.getString("estado");
                String fecha_pedido = resultSet.getString("fecha_pedido");

                Pedido pedido = new Pedido(codigo_referencia, id_pedido, id_cliente, id_usuario, estado, fecha_pedido);
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

    /**
     * Método genérico para obtener una lista de pedidos filtrados por estado.
     * @param connection La conexión a la base de datos.
     * @param estado El estado del pedido que se desea filtrar.
     * @return Una lista de objetos Pedido que coinciden con el estado especificado.
     */
    private static List<Pedido> getPedidosPorEstado(Connection connection, String estado) {
        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PEDIDOS_BY_ESTADO_SQL)) {
            preparedStatement.setString(1, estado);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codigo_referencia = resultSet.getString("codigo_referencia");
                int id_pedido = resultSet.getInt("id_pedido");
                int id_usuario = resultSet.getInt("id_usuario");
                int id_cliente = resultSet.getInt("id_cliente");
                String estadoResult = resultSet.getString("estado");
                String fecha_pedido = resultSet.getString("fecha_pedido");

                Pedido pedido = new Pedido(codigo_referencia, id_pedido, id_cliente, id_usuario, estadoResult, fecha_pedido);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }


    private static final String UPDATE_ESTADO_PEDIDO_SQL = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";

    public static boolean updateEstadoPedido(Connection connection, int idPedido, String nuevoEstado) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ESTADO_PEDIDO_SQL)) {
            preparedStatement.setString(1, nuevoEstado);  // nuevo estado: "Pendiente", "En proceso", etc.
            preparedStatement.setInt(2, idPedido);        // ID del pedido a modificar

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Estado del pedido actualizado correctamente.");
                return true;
            } else {
                System.out.println("⚠️ No se encontró ningún pedido con ese ID.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando el estado del pedido: " + e.getMessage());
            return false;
        }
    }



    private static final String UPDATE_USUARIO_PEDIDO_SQL = "UPDATE pedidos SET id_usuario = ? WHERE id_pedido = ?";
    // PedidoDAO.updateUsuarioPedido(connection, 12, 5);  // Asignar el usuario con ID 5 al pedido con ID 12
    public static boolean updateUsuarioPedido(Connection connection, int idPedido, int nuevoIdUsuario) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USUARIO_PEDIDO_SQL)) {
            preparedStatement.setInt(1, nuevoIdUsuario);  // Nuevo ID de usuario
            preparedStatement.setInt(2, idPedido);        // ID del pedido a modificar

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Usuario del pedido actualizado correctamente.");
                return true;
            } else {
                System.out.println("⚠️ No se encontró ningún pedido con ese ID.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando el usuario del pedido: " + e.getMessage());
            return false;
        }
    }

}