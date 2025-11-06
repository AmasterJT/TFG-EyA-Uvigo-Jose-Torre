package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class PedidoDAO {

    private static final Logger LOGGER = Logger.getLogger(PedidoDAO.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }


    // Crear pedido
    private static final String INSERT_PEDIDO_SQL = "INSERT INTO pedidos (id_usuario, estado) VALUES (?, ?)";

    public static void createPedido(Connection connection, int id_usuario, String estado) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PEDIDO_SQL)) {
            preparedStatement.setInt(1, id_usuario);
            preparedStatement.setString(2, estado);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                LOGGER.info("Pedido creado exitosamente (id_usuario=" + id_usuario + ", estado=" + estado + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear el pedido", e);
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

                LOGGER.info(CYAN + "Código Ref: " + RESET + codigo_referencia +
                        CYAN + ", ID: " + RESET + id_pedido +
                        CYAN + ", Usuario ID: " + RESET + id_usuario +
                        CYAN + ", Estado: " + RESET + estado);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener datos de los pedidos", e);
        }
    }

    // Obtener todos los pedidos
    public static List<Pedido> getPedidosAllData(Connection connection) {
        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PEDIDOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Pedido pedido = new Pedido(
                        resultSet.getString("codigo_referencia"),
                        resultSet.getInt("id_pedido"),
                        resultSet.getInt("id_cliente"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("estado"),
                        resultSet.getString("fecha_pedido"),
                        resultSet.getString("hora_salida")
                );
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la lista completa de pedidos", e);
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

    private static List<Pedido> getPedidosPorEstado(Connection connection, String estado) {
        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PEDIDOS_BY_ESTADO_SQL)) {
            preparedStatement.setString(1, estado);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Pedido pedido = new Pedido(
                        resultSet.getString("codigo_referencia"),
                        resultSet.getInt("id_pedido"),
                        resultSet.getInt("id_cliente"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("estado"),
                        resultSet.getString("fecha_pedido"),
                        resultSet.getString("hora_salida")
                );
                pedidos.add(pedido);
            }

            pedidos.sort(Comparator.comparing(
                    p -> {
                        String hora = p.getHoraSalida();
                        if (hora == null) return 2;
                        return hora.equals("primera_hora") ? 0 : 1;
                    }
            ));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener pedidos por estado: " + estado, e);
        }
        return pedidos;
    }

    private static final String UPDATE_ESTADO_PEDIDO_SQL =
            "UPDATE pedidos SET estado = ?, hora_salida = ? WHERE id_pedido = ?";

    public static void updateEstadoPedido(Connection connection, int idPedido, String nuevoEstado, String hora_salida_nueva) {
        if (connection == null) {
            LOGGER.severe("Conexión nula al actualizar el pedido");
            return;
        }

        final List<String> ESTADOS_VALIDOS = List.of("Pendiente", "Completado", "En proceso", "Cancelado");
        final List<String> HORAS_VALIDAS = List.of("primera_hora", "segunda_hora");

        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            LOGGER.warning("Estado no válido: " + nuevoEstado);
            return;
        }
        if (hora_salida_nueva != null && !hora_salida_nueva.isBlank() && !HORAS_VALIDAS.contains(hora_salida_nueva)) {
            LOGGER.warning("hora_salida no válida: " + hora_salida_nueva);
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_PEDIDO_SQL)) {
            ps.setString(1, nuevoEstado);
            if (hora_salida_nueva == null || hora_salida_nueva.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, hora_salida_nueva);
            }
            ps.setInt(3, idPedido);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                LOGGER.info("Pedido actualizado (id=" + idPedido + ")");
            } else {
                LOGGER.warning("No se encontró pedido con id=" + idPedido);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando pedido (id=" + idPedido + ")", e);
        }
    }

    private static final String UPDATE_USUARIO_PEDIDO_SQL =
            "UPDATE pedidos SET id_usuario = ? WHERE id_pedido = ?";

    public static boolean updateUsuarioPedido(Connection connection, int idPedido, int nuevoIdUsuario) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_USUARIO_PEDIDO_SQL)) {
            ps.setInt(1, nuevoIdUsuario);
            ps.setInt(2, idPedido);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                LOGGER.info("Usuario del pedido actualizado correctamente (id=" + idPedido + ")");
                return true;
            } else {
                LOGGER.warning("No se encontró pedido con id=" + idPedido);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando usuario del pedido (id=" + idPedido + ")", e);
            return false;
        }
    }

    private static final String UPDATE_ESTADO_PEDIDO_CANCELADO_COMPLETADO_SQL =
            "UPDATE pedidos SET id_usuario = ? WHERE id_pedido = ?";

    public static void updateEstadoPedidoCanceladoCompletado(Connection connection, int idPedido) {
        if (connection == null) {
            LOGGER.severe("Conexión nula al actualizar el pedido");
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_PEDIDO_CANCELADO_COMPLETADO_SQL)) {
            ps.setNull(1, Types.INTEGER);
            ps.setInt(2, idPedido);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                LOGGER.info("Pedido cancelado/completado (id=" + idPedido + ")");
            } else {
                LOGGER.warning("No se encontró pedido con id=" + idPedido);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando pedido cancelado/completado (id=" + idPedido + ")", e);
        }
    }

    private static final String UPDATE_PEDIDO_USUARIO_Y_HORA_SQL =
            "UPDATE pedidos SET id_usuario = ?, hora_salida = ? WHERE id_pedido = ?";

    public static void updateUsuarioYHoraSalidaPedido(Connection connection, int idPedido, Integer idUsuario, String horaSalida) {
        if (connection == null) {
            LOGGER.severe("Conexión nula al actualizar el pedido");
            return;
        }

        if (horaSalida == null || (!horaSalida.equals("primera_hora") && !horaSalida.equals("segunda_hora"))) {
            LOGGER.warning("Valor de hora_salida inválido: " + horaSalida);
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_PEDIDO_USUARIO_Y_HORA_SQL)) {
            if (idUsuario != null) ps.setInt(1, idUsuario);
            else ps.setNull(1, Types.INTEGER);

            ps.setString(2, horaSalida);
            ps.setInt(3, idPedido);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                LOGGER.info(String.format("Pedido %d actualizado → id_usuario=%s, hora_salida=%s",
                        idPedido, (idUsuario != null ? idUsuario : "NULL"), horaSalida));
            } else {
                LOGGER.warning("No se encontró pedido con id=" + idPedido);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando usuario y hora_salida del pedido (id=" + idPedido + ")", e);
        }
    }
}
