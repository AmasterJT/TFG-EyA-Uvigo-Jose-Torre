package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
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
     *
     * @param connection La conexión a la base de datos.
     * @return Una lista de objetos Pedido con todos sus datos.
     */
    public static List<Pedido> getPedidosAllData(Connection connection) {

        List<Pedido> pedidos = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PEDIDOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codigo_referencia = resultSet.getString("codigo_referencia");
                int id_pedido = resultSet.getInt("id_pedido");
                int id_cliente = resultSet.getInt("id_cliente");
                int id_usuario = resultSet.getInt("id_usuario");
                String estado = resultSet.getString("estado");
                String fecha_pedido = resultSet.getString("fecha_pedido");
                String hora_salida = resultSet.getString("hora_salida");

                Pedido pedido = new Pedido(codigo_referencia, id_pedido, id_cliente, id_usuario, estado, fecha_pedido, hora_salida);
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
     *
     * @param connection La conexión a la base de datos.
     * @param estado     El estado del pedido que se desea filtrar.
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
                String hora_salida = resultSet.getString("hora_salida");

                Pedido pedido = new Pedido(codigo_referencia, id_pedido, id_cliente, id_usuario, estadoResult, fecha_pedido, hora_salida);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ordenar: primero "primera_hora", luego "segunda_hora", y dejar nulls al final
        pedidos.sort(Comparator.comparing(
                p -> {
                    String hora = p.getHoraSalida();
                    if (hora == null) return 2; // null al final
                    return hora.equals("primera_hora") ? 0 : 1;
                }
        ));

        return pedidos;
    }


    private static final String UPDATE_ESTADO_PEDIDO_SQL = "UPDATE pedidos SET estado = ?, hora_salida = ? WHERE id_pedido = ?";

    public static void updateEstadoPedido(Connection connection, int idPedido, String nuevoEstado, String hora_salida_nueva) {
        if (connection == null) {
            System.err.println("❌ Conexión nula al actualizar el pedido.");
            return;
        }

        // Validación rápida para evitar errores de ENUM en MySQL
        final List<String> ESTADOS_VALIDOS = List.of("Pendiente", "Completado", "En proceso", "Cancelado");
        final List<String> HORAS_VALIDAS = List.of("primera_hora", "segunda_hora");

        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            System.err.println("❌ Estado no válido: " + nuevoEstado);
            return;
        }
        if (hora_salida_nueva != null && !hora_salida_nueva.isBlank() && !HORAS_VALIDAS.contains(hora_salida_nueva)) {
            System.err.println("❌ hora_salida no válida: " + hora_salida_nueva);
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_PEDIDO_SQL)) {
            // 1) estado
            ps.setString(1, nuevoEstado);

            // 2) hora_salida (ENUM nullable)
            if (hora_salida_nueva == null || hora_salida_nueva.isBlank()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, hora_salida_nueva);
            }

            // 3) id_pedido
            ps.setInt(3, idPedido);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Estado/hora_salida actualizados (id_pedido=" + idPedido + ").");
            } else {
                System.out.println("⚠️ No se encontró pedido con id=" + idPedido + ".");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando pedido: " + e.getMessage());
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


    private static final String UPDATE_ESTADO_PEDIDO_CANCELADO_COMPLETADO_SQL =
            "UPDATE pedidos SET id_usuario = ? WHERE id_pedido = ?";

    public static void updateEstadoPedidoCanceladoCompletado(Connection connection, int idPedido) {
        if (connection == null) {
            System.err.println("❌ Conexión nula al actualizar el pedido.");
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_PEDIDO_CANCELADO_COMPLETADO_SQL)) {
            ps.setNull(1, java.sql.Types.INTEGER);  // tipo de la columna
            ps.setInt(2, idPedido);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Estado/hora_salida actualizados (id_pedido=" + idPedido + ").");
            } else {
                System.out.println("⚠️ No se encontró pedido con id=" + idPedido + ".");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando pedido: " + e.getMessage());
        }
    }


}