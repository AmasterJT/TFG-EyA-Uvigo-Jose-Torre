package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.time.LocalDate;


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
                        resultSet.getString("hora_salida"),
                        resultSet.getInt("paelts_del_pedido")
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
                        resultSet.getString("hora_salida"),
                        resultSet.getInt("palets_del_pedido")
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
            if (rows < 0) {
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

    // ============================================================
    // SQL: pone el id_usuario en NULL para un pedido concreto
    // Se usa cuando un usuario es eliminado o se reasigna el pedido
    // ============================================================
    private static final String UPDATE_ESTADO_PEDIDO_CANCELADO_COMPLETADO_SQL =
            "UPDATE pedidos SET id_usuario = ? WHERE id_pedido = ?";

    /**
     * Marca el pedido indicado como “huérfano” (sin usuario asignado).
     * <p>
     * Este método establece el campo {@code id_usuario = NULL} en la tabla {@code pedidos}
     * para el registro cuyo {@code id_pedido} coincide con el proporcionado.
     * Se utiliza normalmente cuando un usuario ha sido eliminado y
     * sus pedidos deben quedar pendientes de reasignación.
     *
     * @param connection conexión activa a la base de datos (no debe ser nula)
     * @param idPedido   identificador del pedido que se desea actualizar
     */
    public static void updateEstadoPedidoCanceladoCompletado(Connection connection, int idPedido) {
        if (connection == null) {
            LOGGER.severe("No se puede actualizar el pedido: conexión nula recibida.");
            return;
        }


        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_PEDIDO_CANCELADO_COMPLETADO_SQL)) {
            // Asigna null al id_usuario
            ps.setNull(1, Types.INTEGER);
            ps.setInt(2, idPedido);

            int rows = ps.executeUpdate();

            if (rows < 0) {
                LOGGER.warning(() -> String.format("No se encontró ningún pedido con id=%d. No se aplicó actualización.", idPedido));
            }

            LOGGER.fine(() -> "Pedido cancelado/completado (" + idPedido + ") actualizado -> id_usuario = NULL.");


        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Error al actualizar el pedido cancelado/completado (id=%d): %s",
                            idPedido, e.getMessage()),
                    e);
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
                LOGGER.fine(String.format("Pedido %d actualizado → id_usuario=%s, hora_salida=%s",
                        idPedido, (idUsuario != null ? idUsuario : "NULL"), horaSalida));
            } else {
                LOGGER.warning("No se encontró pedido con id=" + idPedido);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando usuario y hora_salida del pedido (id=" + idPedido + ")", e);
        }
    }


    private static final String SQL_INSERT =
            "INSERT INTO pedidos (id_usuario, id_cliente, fecha_entrega, estado, hora_salida) " +
                    "VALUES (?, ?, ?, 'Pendiente', ?)";

    private static final String SQL_SELECT_CODIGO =
            "SELECT codigo_referencia FROM pedidos WHERE id_pedido = ?";


    /**
     * Crea un pedido y devuelve el codigo_referencia generado por el trigger.
     *
     * @param conn         conexión abierta a MySQL (no se cierra aquí).
     * @param idCliente    id del cliente (FK).
     * @param fechaEntrega fecha de entrega (LocalDate).
     * @return codigo_referencia generado, por ejemplo "PED-20251112-00000A".
     * @throws SQLException si algo falla al insertar o recuperar el código.
     */
    public static Map<String, Integer> crearPedidoYObtenerCodigo(Connection conn,
                                                                 int idCliente,
                                                                 LocalDate fechaEntrega) throws SQLException {


        Map<String, Integer> pedido_nuevo = new HashMap<>();

        boolean prevAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (PreparedStatement psIns = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psSel = conn.prepareStatement(SQL_SELECT_CODIGO)) {

            // id_usuario -> NULL
            psIns.setNull(1, Types.INTEGER);
            // id_cliente
            psIns.setInt(2, idCliente);
            // fecha_entrega
            psIns.setDate(3, Date.valueOf(fechaEntrega));
            // hora_salida -> NULL
            psIns.setNull(4, Types.VARCHAR);

            int rows = psIns.executeUpdate();
            if (rows != 1) {
                conn.rollback();
                throw new SQLException("La inserción de pedido no afectó exactamente a 1 fila.");
            }

            // id generado por AUTO_INCREMENT
            int idPedido;
            try (ResultSet rsKeys = psIns.getGeneratedKeys()) {
                if (!rsKeys.next()) {
                    conn.rollback();
                    throw new SQLException("No se obtuvo id_pedido generado.");
                }
                idPedido = rsKeys.getInt(1);
            }

            // Recuperar el código que generó el trigger BEFORE INSERT
            psSel.setInt(1, idPedido);
            String codigo;
            try (ResultSet rs = psSel.executeQuery()) {
                if (!rs.next()) {
                    conn.rollback();
                    throw new SQLException("No se encontró el pedido recién insertado (id=" + idPedido + ").");
                }
                codigo = rs.getString(1);
            }

            conn.commit();
            pedido_nuevo.put(codigo, idPedido);
            return pedido_nuevo;


        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(prevAutoCommit);
        }
    }


    private static final String SQL_SELECT_TODOS_PEDIDOS =
            "SELECT codigo_referencia, id_pedido, id_cliente, id_usuario, estado, fecha_pedido, hora_salida, palets_del_pedido " +
                    "FROM pedidos " +
                    "ORDER BY fecha_pedido DESC";

    // Usamos el mismo patrón que espera el constructor de Pedido
    private static final java.time.format.DateTimeFormatter DB_TS_FMT =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ArrayList<Pedido> getTodosLosPedidos(Connection conn) {
        ArrayList<Pedido> pedidos = new ArrayList<>();
        if (conn == null) {
            LOGGER.severe("Conexión nula en getTodosLosPedidos()");
            return pedidos;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_TODOS_PEDIDOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String codigoReferencia = rs.getString("codigo_referencia");
                int idPedido = rs.getInt("id_pedido");
                int idCliente = rs.getInt("id_cliente");
                int palets_del_pedido = rs.getInt("palets_del_pedido");

                // id_usuario puede ser NULL; getInt devuelve 0 si NULL. Podemos conservar 0.
                int idUsuario = rs.getInt("id_usuario");
                if (rs.wasNull()) {
                    idUsuario = 0; // coherente con tu modelo (int primitivo en Pedido)
                }

                String estado = rs.getString("estado");

                // Formateamos fecha_pedido al String que espera el constructor
                java.sql.Timestamp ts = rs.getTimestamp("fecha_pedido");
                String fechaPedidoRaw = ts.toLocalDateTime().format(DB_TS_FMT);

                String horaSalida = rs.getString("hora_salida"); // puede ser null

                // Construimos el Pedido como en tu clase
                Pedido p = new Pedido(
                        codigoReferencia,
                        idPedido,
                        idCliente,
                        idUsuario,
                        estado,
                        fechaPedidoRaw,
                        horaSalida,
                        palets_del_pedido
                );

                pedidos.add(p);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error obteniendo pedidos: " + e.getMessage());
        }

        return pedidos;
    }

    private static final String SQL_ID_BY_CODIGO =
            "SELECT id_pedido FROM pedidos WHERE codigo_referencia = ?";


    /**
     * Devuelve el id_pedido a partir del código de referencia.
     *
     * @param conn             conexión abierta
     * @param codigoReferencia código de referencia (PED-YYYYMMDD-XXXXXX)
     * @return id_pedido o null si no existe
     */
    public static Integer getIdPedidoPorCodigo(Connection conn, String codigoReferencia) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdPedidoPorCodigo()");
            return null;
        }
        if (codigoReferencia == null || codigoReferencia.isBlank()) {
            LOGGER.warning("codigoReferencia vacío/nulo en getIdPedidoPorCodigo()");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_ID_BY_CODIGO)) {
            ps.setString(1, codigoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error obteniendo id_pedido por código: " + e.getMessage());
        }
        return null;
    }


    private static final String SQL_ID_USUARIO_BY_PEDIDO =
            "SELECT id_usuario FROM pedidos WHERE id_pedido = ?";

    /**
     * Devuelve el id_usuario asignado a un pedido específico.
     *
     * @param conn     conexión abierta a la base de datos.
     * @param idPedido identificador del pedido.
     * @return id_usuario si existe (puede ser null si el pedido no tiene usuario asignado),
     * o null si no se encuentra el pedido.
     */
    public static Integer getIdUsuarioPorPedido(Connection conn, int idPedido) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdUsuarioPorPedido()");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_ID_USUARIO_BY_PEDIDO)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idUsuario = rs.getInt("id_usuario");
                    return rs.wasNull() ? null : idUsuario; // Manejo de valores NULL correctamente
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error obteniendo id_usuario por pedido: " + e.getMessage());
        }

        return null;
    }


    private static final String DELETE_PEDIDO_BY_ID_SQL =
            "DELETE FROM pedidos WHERE id_pedido = ?";

    /**
     * Borra el pedido por id. Devuelve true si se eliminó una fila.
     */
    public static boolean deletePedidoById(Connection cn, int idPedido) throws SQLException {
        if (cn == null) {
            throw new SQLException("Conexión nula en deletePedidoById");
        }
        try (PreparedStatement ps = cn.prepareStatement(DELETE_PEDIDO_BY_ID_SQL)) {
            ps.setInt(1, idPedido);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                LOGGER.fine(() -> "Pedido eliminado. id_pedido=" + idPedido);
                return true;
            } else {
                LOGGER.warning(() -> "No existe pedido con id_pedido=" + idPedido);
                return false;
            }
        }
    }


    // ============================================================
// Marcar pedido como COMPLETADO y dejarlo sin usuario (id_usuario = NULL)
// usando el codigo_referencia
// ============================================================
    private static final String UPDATE_COMPLETADO_SIN_USUARIO_BY_CODIGO_SQL =
            "UPDATE pedidos SET estado = 'Completado', id_usuario = NULL WHERE codigo_referencia = ?";

    /**
     * Marca el pedido identificado por {@code codigoReferencia} como
     * {@code estado = 'Completado'} y asigna {@code id_usuario = NULL}.
     * <p>
     * No modifica la hora_salida. Está pensado para casos en los que
     * se quiere dejar constancia de que el pedido se ha completado pero
     * sin asociarlo a ningún usuario.
     *
     * @param connection       conexión activa a la base de datos (no debe ser nula)
     * @param codigoReferencia código de referencia único del pedido
     * @return {@code true} si se actualizó exactamente una fila, {@code false} en caso contrario
     */
    public static boolean marcarPedidoCompletadoSinUsuarioPorCodigo(Connection connection,
                                                                    String codigoReferencia) {
        if (connection == null) {
            LOGGER.severe("No se puede marcar pedido como completado: conexión nula recibida.");
            return false;
        }
        if (codigoReferencia == null || codigoReferencia.isBlank()) {
            LOGGER.warning("codigoReferencia vacío o nulo en marcarPedidoCompletadoSinUsuarioPorCodigo()");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_COMPLETADO_SIN_USUARIO_BY_CODIGO_SQL)) {
            ps.setString(1, codigoReferencia);

            int rows = ps.executeUpdate();

            if (rows == 1) {
                LOGGER.info(() -> String.format(
                        "Pedido con codigo_referencia='%s' marcado como 'Completado' y id_usuario=NULL.",
                        codigoReferencia));
                return true;
            } else if (rows == 0) {
                LOGGER.warning(() -> "No se encontró ningún pedido con codigo_referencia='" + codigoReferencia + "'.");
                return false;
            } else {
                // En teoría no debería pasar porque codigo_referencia es UNIQUE
                LOGGER.warning(() -> String.format(
                        "Se actualizaron %d filas para codigo_referencia='%s'. " +
                                "Revisa la unicidad del campo en la BD.",
                        rows, codigoReferencia));
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Error al marcar como completado el pedido con codigo_referencia='%s'.",
                            codigoReferencia),
                    e);
            return false;
        }
    }


    private static final String SQL_UPDATE_PALETS_PEDIDO =
            "UPDATE pedidos SET palets_del_pedido = ? WHERE id_pedido = ?";

    /**
     * Actualiza el número de palets de un pedido.
     *
     * @param conn            conexión abierta a la base de datos
     * @param idPedido        ID del pedido a modificar
     * @param paletsDelPedido nuevo valor del campo palets_del_pedido
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public static boolean actualizarPaletsDelPedido(Connection conn, int idPedido, int paletsDelPedido) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en actualizarPaletsDelPedido()");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PALETS_PEDIDO)) {
            ps.setInt(1, paletsDelPedido);
            ps.setInt(2, idPedido);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                LOGGER.info(() -> String.format("Pedido %d actualizado con palets_del_pedido=%d", idPedido, paletsDelPedido));
                return true;
            } else {
                LOGGER.warning(() -> String.format("No se encontró pedido con id_pedido=%d", idPedido));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando palets_del_pedido en pedido id=" + idPedido, e);
        }
        return false;
    }


    private static final String SQL_SELECT_PALETS_PEDIDO =
            "SELECT palets_del_pedido FROM pedidos WHERE id_pedido = ?";

    /**
     * Obtiene el valor del campo palets_del_pedido para un pedido dado.
     *
     * @param conn     conexión abierta a la base de datos
     * @param idPedido id del pedido
     * @return el número de palets_del_pedido, o -1 si no se encuentra o hay error
     */
    public static int getPaletsDelPedido(Connection conn, int idPedido) {
        if (conn == null) {
            LOGGER.severe("❌ Conexión nula en getPaletsDelPedido()");
            return -1;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_PALETS_PEDIDO)) {
            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int palets = rs.getInt("palets_del_pedido");
                    LOGGER.fine(() -> String.format("📊 Pedido %d → palets_del_pedido=%d", idPedido, palets));
                    return palets;
                } else {
                    LOGGER.warning(() -> String.format("⚠️ No se encontró pedido con id_pedido=%d", idPedido));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo palets_del_pedido para pedido id=" + idPedido, e);
        }
        return -1;
    }
}
