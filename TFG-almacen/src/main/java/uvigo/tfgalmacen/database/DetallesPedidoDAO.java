package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.ProductoPedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetallesPedidoDAO {

    private static final Logger LOGGER = Logger.getLogger(DetallesPedidoDAO.class.getName());

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

    /* ===========================
       SELECT: productos por pedido
       =========================== */
    private static final String SELECT_PRODUCTOS_POR_PEDIDO_SQL = """
            SELECT p.identificador_producto, dp.cantidad, dp.estado_producto_pedido, dp.id_detalle
            FROM detalles_pedido dp
            JOIN pedidos pe  ON dp.id_pedido   = pe.id_pedido
            JOIN productos p ON dp.id_producto = p.id_producto
            WHERE pe.codigo_referencia = ?
            """;

    public static List<ProductoPedido> getProductosPorCodigoReferencia(Connection connection, String codigoReferencia) {
        List<ProductoPedido> productos = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Conexión nula en getProductosPorCodigoReferencia()");
            return productos;
        }

        try (PreparedStatement ps = connection.prepareStatement(SELECT_PRODUCTOS_POR_PEDIDO_SQL)) {
            ps.setString(1, codigoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String identificadorProducto = rs.getString("identificador_producto");
                    int cantidad = rs.getInt("cantidad");
                    boolean isComplete = rs.getBoolean("estado_producto_pedido");
                    int id_detalle_BDD = rs.getInt("id_detalle");

                    productos.add(new ProductoPedido(identificadorProducto, cantidad, isComplete, id_detalle_BDD));
                }
            }
            LOGGER.fine(() -> "Productos recuperados para código " + codigoReferencia + ": " + productos.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo productos por código de referencia: " + codigoReferencia, e);
            e.printStackTrace();
        }
        return productos;
    }

    /* ===========================
       INSERT: detalle del pedido
       =========================== */
    private static final String INSERT_COMPLETE_DETALLE_SQL = """
            INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido)
            VALUES (?, ?, ?, FALSE)
            """;

    /**
     * Inserta un producto dentro de un pedido en la tabla detalles_pedido.
     *
     * @param connection conexión abierta a la base de datos.
     * @param idPedido   ID del pedido al que pertenece este detalle.
     * @param idProducto ID del producto incluido en el pedido.
     * @param cantidad   Cantidad solicitada de ese producto.
     * @return true si se insertó correctamente, false en caso contrario.
     */
    public static boolean insertarDetallePedido(Connection connection,
                                                int idPedido,
                                                int idProducto,
                                                int cantidad) {
        if (connection == null) {
            LOGGER.severe("Conexión nula al insertar detalle de pedido.");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(INSERT_COMPLETE_DETALLE_SQL)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                LOGGER.fine(() -> String.format("✅ Detalle insertado (pedido=%d, producto=%d, cantidad=%d)", idPedido, idProducto, cantidad));
                return true;
            } else {
                LOGGER.warning(() -> String.format("No se insertó detalle (pedido=%d, producto=%d)", idPedido, idProducto));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, String.format("Error al insertar detalle (pedido=%d, producto=%d)", idPedido, idProducto), e);
        }
        return false;
    }

    /* ==================================
       SELECT: ids de detalle por pedido
       ================================== */
    private static final String SQL_IDS_DETALLE_BY_PEDIDO =
            "SELECT id_detalle FROM detalles_pedido WHERE id_pedido = ? ORDER BY id_detalle";

    /**
     * Devuelve los id_detalle asociados a un id_pedido.
     *
     * @param conn     conexión abierta
     * @param idPedido id del pedido
     * @return lista (posiblemente vacía) de ids de detalle
     */
    public static List<Integer> getIdsDetallePorPedido(Connection conn, int idPedido) {
        List<Integer> ids = new ArrayList<>();
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdsDetallePorPedido()");
            return ids;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_IDS_DETALLE_BY_PEDIDO)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
            LOGGER.fine(() -> "Ids detalle recuperados para pedido " + idPedido + ": " + ids);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo ids de detalle por pedido: " + idPedido, e);
        }
        return ids;
    }

    /* ===========================
       DELETE: detalle por id_detalle
       =========================== */
    private static final String DELETE_DETALLE_BY_ID_SQL =
            "DELETE FROM detalles_pedido WHERE id_detalle = ?";

    /**
     * Elimina un registro de la tabla detalles_pedido por su id_detalle.
     *
     * @param conn      conexión abierta
     * @param idDetalle id del detalle a eliminar
     * @return true si se eliminó exactamente un registro; false si no existía o hubo error
     */
    public static boolean borrarDetallePorId(Connection conn, int idDetalle) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en borrarDetallePorId()");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(DELETE_DETALLE_BY_ID_SQL)) {
            ps.setInt(1, idDetalle);
            int rows = ps.executeUpdate();

            if (rows == 1) {
                LOGGER.fine(() -> "🗑️ Producto eliminado correctamente de la BDD (id_detalle=" + idDetalle + ")");
                return true;
            } else if (rows == 0) {
                LOGGER.warning(() -> "No se encontró detalle con id_detalle=" + idDetalle + " para eliminar.");
                return false;
            } else {
                // Muy raro en PK; lo dejamos por robustez
                LOGGER.warning(() -> "Se eliminaron " + rows + " filas para id_detalle=" + idDetalle + " (esperado 1).");
                return rows > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error eliminando detalle con id_detalle=" + idDetalle, e);
            return false;
        }
    }


    /* ===========================
       SELECT: identificador_producto y cantidad por id_detalle
       =========================== */
    private static final String SQL_IDENTIFICADOR_Y_CANTIDAD_BY_DETALLE = """
            SELECT p.identificador_producto, dp.cantidad
            FROM detalles_pedido dp
            JOIN productos p ON dp.id_producto = p.id_producto
            WHERE dp.id_detalle = ?
            """;

    /**
     * Devuelve el identificador_producto (código de negocio) y la cantidad para un id_detalle.
     *
     * @param conn      conexión abierta
     * @param idDetalle id del detalle del pedido
     * @return un par [identificador_producto, cantidad], o null si no existe
     */
    public static String[] getIdentificadorYCantidadPorDetalle(Connection conn, int idDetalle) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdentificadorYCantidadPorDetalle()");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_IDENTIFICADOR_Y_CANTIDAD_BY_DETALLE)) {
            ps.setInt(1, idDetalle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String identificador = rs.getString("identificador_producto");
                    int cantidad = rs.getInt("cantidad");
                    LOGGER.info(() -> String.format("Detalle %d → identificador='%s', cantidad=%d", idDetalle, identificador, cantidad));
                    return new String[]{identificador, String.valueOf(cantidad)};
                } else {
                    LOGGER.warning("No se encontró detalle con id_detalle=" + idDetalle);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo identificador_producto y cantidad para id_detalle=" + idDetalle, e);
        }
        return null;
    }


    private static final String UPDATE_DETALLE_SQL =
            "UPDATE detalles_pedido SET id_producto = ?, cantidad = ? WHERE id_detalle = ?";

    /**
     * Actualiza el id_producto y la cantidad de un detalle de pedido existente.
     *
     * @param connection    Conexión activa a la base de datos
     * @param idDetalle     ID del detalle a modificar
     * @param nuevoProducto ID del nuevo producto
     * @param nuevaCantidad Nueva cantidad
     * @return true si la actualización fue exitosa, false si no se encontró el registro o falló la operación
     */
    public static boolean updateDetalleProductoYCantidad(Connection connection,
                                                         int idDetalle,
                                                         int nuevoProducto,
                                                         int nuevaCantidad) {
        if (connection == null) {
            LOGGER.warning("Conexión nula al intentar actualizar detalle de pedido.");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_DETALLE_SQL)) {
            ps.setInt(1, nuevoProducto);
            ps.setInt(2, nuevaCantidad);
            ps.setInt(3, idDetalle);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                LOGGER.fine(() -> String.format(
                        "Detalle actualizado correctamente (id_detalle=%d, id_producto=%d, cantidad=%d)",
                        idDetalle, nuevoProducto, nuevaCantidad));
                return true;
            } else {
                LOGGER.warning(() -> String.format(
                        "No se encontró ningún detalle con id_detalle=%d", idDetalle));
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar detalle de pedido: " + e.getMessage(), e);
            return false;
        }
    }


    private static final String DELETE_BY_PEDIDO_SQL =
            "DELETE FROM detalles_pedido WHERE id_pedido = ?";

    private static final String INSERT_TOTAL_SQL =
            "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (?, ?, ?, 0)";

    /**
     * Borra todos los detalles de un pedido.
     */
    public static void deleteByPedido(Connection cn, int idPedido) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(DELETE_BY_PEDIDO_SQL)) {
            ps.setInt(1, idPedido);
            int rows = ps.executeUpdate();
            LOGGER.fine(() -> "Detalles borrados para pedido " + idPedido + ": " + rows);
        }
    }

    /**
     * Inserta en bloque los totales (id_producto -> cantidad) para un pedido.
     */
    public static void bulkInsertTotals(Connection cn, int idPedido, Map<Integer, Integer> totales) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(INSERT_TOTAL_SQL)) {
            for (var e : totales.entrySet()) {
                ps.setInt(1, idPedido);
                ps.setInt(2, e.getKey());
                ps.setInt(3, e.getValue());
                ps.addBatch();
            }
            int[] counts = ps.executeBatch();
            LOGGER.fine(() -> "Insertados " + counts.length + " detalles agregados para pedido " + idPedido);
        }
    }

    /**
     * Reemplaza los detalles de un pedido por los totales suministrados.
     * No hace commit; lo gestiona el llamante (controller).
     */
    public static void replaceDetallesWithTotals(Connection cn, int idPedido, Map<Integer, Integer> totales) throws SQLException {
        if (cn == null) {
            LOGGER.warning("Conexión nula en replaceDetallesWithTotals");
            return;
        }
        // Borrar todo y reinsertar agregados
        deleteByPedido(cn, idPedido);
        if (totales != null && !totales.isEmpty()) {
            bulkInsertTotals(cn, idPedido, totales);
        } else {
            LOGGER.info("No hay totales que insertar; el pedido queda sin detalles.");
        }
    }

    /**
     * Elimina múltiples detalles por sus IDs. Maneja listas grandes en tandas para no superar límites de placeholders.
     *
     * @param connection conexión activa
     * @param idsDetalle lista de id_detalle a eliminar
     * @return número total de filas eliminadas
     */
    public static int borrarDetallesPorIds(Connection connection, List<Integer> idsDetalle) {
        if (connection == null) {
            LOGGER.warning("Conexión nula en borrarDetallesPorIds");
            return 0;
        }
        if (idsDetalle == null || idsDetalle.isEmpty()) {
            LOGGER.fine("Lista de idsDetalle vacía; no se elimina nada.");
            return 0;
        }

        // Tamaño de tanda prudente para el IN (...)
        final int CHUNK_SIZE = 500;
        int totalEliminados = 0;

        for (int start = 0; start < idsDetalle.size(); start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, idsDetalle.size());
            List<Integer> slice = idsDetalle.subList(start, end);

            StringJoiner sj = new StringJoiner(",", "(", ")");
            for (int i = 0; i < slice.size(); i++) sj.add("?");

            String sql = "DELETE FROM detalles_pedido WHERE id_detalle IN " + sj;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int idx = 1;
                for (Integer id : slice) {
                    ps.setInt(idx++, id);
                }
                int rows = ps.executeUpdate();
                totalEliminados += rows;

                int finalStart = start;
                LOGGER.fine(() -> "Borrado por lote: " + rows + " filas (rangos " + finalStart + "-" + (end - 1) + ")");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE,
                        "Error en borrado por lote de detalles (rangos " + start + "-" + (end - 1) + ")", e);
                // Continúa con siguientes tandas; si prefieres abortar, lanza la excepción.
            }
        }

        int finalTotalEliminados = totalEliminados;
        LOGGER.info(() -> "Total de detalles eliminados: " + finalTotalEliminados + " de " + idsDetalle.size());
        return totalEliminados;
    }


    private static final String UPDATE_ESTADO_PRODUCTO_PEDIDO_SQL =
            "UPDATE detalles_pedido SET estado_producto_pedido = ? WHERE id_detalle = ?";

    /**
     * Actualiza el campo {@code estado_producto_pedido} de un detalle de pedido.
     * <p>
     * El valor se suele mapear desde un CheckBox en la UI:
     * <ul>
     *   <li>{@code true}  → se guarda 1 (producto listo)</li>
     *   <li>{@code false} → se guarda 0 (producto no listo)</li>
     * </ul>
     *
     * @param connection    conexión activa a la base de datos
     * @param idDetalle     id del registro en {@code detalles_pedido}
     * @param productoListo {@code true} si el producto está listo, {@code false} en caso contrario
     * @return {@code true} si se actualizó exactamente una fila, {@code false} si no se encontró el registro o hubo error
     */
    public static boolean actualizarEstadoProductoPedido(Connection connection,
                                                         int idDetalle,
                                                         boolean productoListo) {
        if (connection == null) {
            LOGGER.severe("Conexión nula en actualizarEstadoProductoPedido()");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_PRODUCTO_PEDIDO_SQL)) {
            ps.setBoolean(1, productoListo);
            ps.setInt(2, idDetalle);

            int rows = ps.executeUpdate();

            if (rows == 1) {
                LOGGER.fine(() -> String.format(
                        "Estado de producto actualizado (id_detalle=%d, estado_producto_pedido=%s)",
                        idDetalle, productoListo));
                return true;
            } else if (rows == 0) {
                LOGGER.warning(() -> "No se encontró detalle con id_detalle=" + idDetalle
                        + " para actualizar estado_producto_pedido.");
                return false;
            } else {
                LOGGER.warning(() -> "Se actualizaron " + rows + " filas al cambiar estado_producto_pedido para id_detalle="
                        + idDetalle + " (se esperaba 1).");
                return rows > 0;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error actualizando estado_producto_pedido para id_detalle=" + idDetalle, e);
            return false;
        }
    }


    private static final String SELECT_ID_PEDIDO_BY_ID_DETALLE =
            "SELECT id_pedido FROM detalles_pedido WHERE id_detalle = ?";

    public static Integer getIdPedidoByIdDetalle(Connection conn, int idDetalle) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdPedidoByIdDetalle()");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ID_PEDIDO_BY_ID_DETALLE)) {
            ps.setInt(1, idDetalle);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_pedido");
                }
            }

            LOGGER.warning("No se encontró id_pedido para id_detalle=" + idDetalle);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo id_pedido por id_detalle=" + idDetalle, e);
        }

        return null;
    }


    private static final String SELECT_DETALLE_BY_ID_SQL =
            "SELECT id_pedido, id_producto, cantidad, estado_producto_pedido, paletizado " +
                    "FROM detalles_pedido WHERE id_detalle = ?";

    private static final String UPDATE_CANTIDAD_DETALLE_SQL =
            "UPDATE detalles_pedido SET cantidad = ? WHERE id_detalle = ?";

    private static final String INSERT_DETALLE_SQL2 =
            "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido, paletizado) " +
                    "VALUES (?, ?, ?, ?, ?)";

    public static int splitDetalle(Connection conn, int idDetalle, int cantidadNueva) throws SQLException {
        if (conn == null) {
            throw new SQLException("Conexión nula en DetallesPedidoDAO.splitDetalle");
        }

        boolean prevAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (
                PreparedStatement psSel = conn.prepareStatement(SELECT_DETALLE_BY_ID_SQL);
                PreparedStatement psUpd = conn.prepareStatement(UPDATE_CANTIDAD_DETALLE_SQL);
                PreparedStatement psIns = conn.prepareStatement(INSERT_DETALLE_SQL2, Statement.RETURN_GENERATED_KEYS)
        ) {
            // 1) Leer el detalle original
            psSel.setInt(1, idDetalle);
            int idPedido, idProducto, cantidadOriginal;
            boolean estadoProducto, paletizado;

            try (ResultSet rs = psSel.executeQuery()) {
                if (!rs.next()) {
                    conn.rollback();
                    LOGGER.warning("No se encontró detalle_pedido con id_detalle=" + idDetalle);
                    return -1;
                }

                idPedido = rs.getInt("id_pedido");
                idProducto = rs.getInt("id_producto");
                cantidadOriginal = rs.getInt("cantidad");
                estadoProducto = rs.getBoolean("estado_producto_pedido");
                paletizado = rs.getBoolean("paletizado");
            }

            // 2) Validar cantidades
            if (cantidadNueva <= 0 || cantidadNueva >= cantidadOriginal) {
                conn.rollback();
                LOGGER.warning(String.format(
                        "Cantidad nueva inválida para split (id_detalle=%d, cantidadOriginal=%d, cantidadNueva=%d)",
                        idDetalle, cantidadOriginal, cantidadNueva));
                return -1;
            }

            int cantidadRestante = cantidadOriginal - cantidadNueva;

            // 3) Actualizar cantidad del detalle original
            psUpd.setInt(1, cantidadNueva);
            psUpd.setInt(2, idDetalle);
            int updRows = psUpd.executeUpdate();
            if (updRows != 1) {
                conn.rollback();
                LOGGER.warning("No se pudo actualizar cantidad del detalle original id_detalle=" + idDetalle);
                return -1;
            }

            // 4) Insertar el nuevo detalle con la cantidad restante
            psIns.setInt(1, idPedido);
            psIns.setInt(2, idProducto);
            psIns.setInt(3, cantidadRestante);
            psIns.setBoolean(4, estadoProducto);
            psIns.setBoolean(5, paletizado);
            int insRows = psIns.executeUpdate();
            if (insRows != 1) {
                conn.rollback();
                LOGGER.warning("No se pudo insertar el nuevo detalle en splitDetalle para id_detalle=" + idDetalle);
                return -1;
            }

            int nuevoIdDetalle;
            try (ResultSet rsKeys = psIns.getGeneratedKeys()) {
                if (rsKeys.next()) {
                    nuevoIdDetalle = rsKeys.getInt(1);
                } else {
                    conn.rollback();
                    LOGGER.warning("No se obtuvo id generado para el nuevo detalle en splitDetalle");
                    return -1;
                }
            }

            conn.commit();

            LOGGER.info(String.format(
                    "Split detalle_pedido OK. id_detalle_original=%d -> nuevoCantidad=%d, nuevoDetalle id=%d con cantidad=%d",
                    idDetalle, cantidadNueva, nuevoIdDetalle, cantidadRestante
            ));

            return nuevoIdDetalle;

        } catch (SQLException e) {
            conn.rollback();
            LOGGER.log(Level.SEVERE, "Error en splitDetalle(id_detalle=" + idDetalle + ")", e);
            throw e;
        } finally {
            conn.setAutoCommit(prevAutoCommit);
        }
    }

    public static void setDetallePaletizado(Connection conn, int idDetalle) {
        final String SQL = "UPDATE detalles_pedido SET paletizado = 1 WHERE id_detalle = ?";

        if (conn == null) {
            LOGGER.severe("Conexión nula en setDetallePaletizado()");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, idDetalle);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                LOGGER.info("Detalle marcado como paletizado (id_detalle=" + idDetalle + ")");
            } else {
                LOGGER.warning("No se encontró detalle con id_detalle=" + idDetalle);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error actualizando paletizado=true en id_detalle=" + idDetalle, e);
        }
    }
}
