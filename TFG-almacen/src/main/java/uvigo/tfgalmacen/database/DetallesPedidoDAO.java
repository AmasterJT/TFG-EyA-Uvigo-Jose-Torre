package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.ProductoPedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            SELECT p.identificador_producto, dp.cantidad, dp.estado_producto_pedido
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

                    productos.add(new ProductoPedido(identificadorProducto, cantidad, isComplete));
                }
            }
            LOGGER.fine(() -> "Productos recuperados para código " + codigoReferencia + ": " + productos.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo productos por código de referencia: " + codigoReferencia, e);
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
   SELECT: id_producto y cantidad por id_detalle
   =========================== */
    private static final String SQL_PRODUCTO_Y_CANTIDAD_BY_DETALLE =
            "SELECT id_producto, cantidad FROM detalles_pedido WHERE id_detalle = ?";

    /**
     * Devuelve un par (id_producto, cantidad) a partir de un id_detalle.
     *
     * @param conn      conexión abierta
     * @param idDetalle id del detalle del pedido
     * @return un array [id_producto, cantidad], o null si no existe
     */
    public static int[] getProductoYCantidadPorDetalle(Connection conn, int idDetalle) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getProductoYCantidadPorDetalle()");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_PRODUCTO_Y_CANTIDAD_BY_DETALLE)) {
            ps.setInt(1, idDetalle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idProducto = rs.getInt("id_producto");
                    int cantidad = rs.getInt("cantidad");
                    LOGGER.fine(() -> String.format("Detalle %d → id_producto=%d, cantidad=%d", idDetalle, idProducto, cantidad));
                    return new int[]{idProducto, cantidad};
                } else {
                    LOGGER.warning("No se encontró detalle con id_detalle=" + idDetalle);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo id_producto y cantidad para id_detalle=" + idDetalle, e);
        }
        return null;
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


    private static final String INSERT_DETALLE_SQL = "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (?, ?, ?, 0)";


    /**
     * x
     * Inserta un nuevo producto dentro de un pedido existente.
     *
     * @param connection Conexión activa a la base de datos.
     * @param idPedido   ID del pedido al que se le agregará el producto.
     * @param idProducto ID del producto que se agregará.
     * @param cantidad   Cantidad del producto.
     */
    public static void insertNuevoProductoEnPedido(Connection connection,
                                                   int idPedido,
                                                   int idProducto,
                                                   int cantidad) {
        if (connection == null) {
            LOGGER.warning("Conexión nula al intentar insertar nuevo producto en pedido.");
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(INSERT_DETALLE_SQL)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                LOGGER.fine(() -> String.format(
                        "Nuevo producto insertado en pedido (id_pedido=%d, id_producto=%d, cantidad=%d)",
                        idPedido, idProducto, cantidad));
            } else {
                LOGGER.warning(() -> String.format(
                        "No se insertó ningún registro (id_pedido=%d, id_producto=%d)", idPedido, idProducto));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error insertando nuevo producto en pedido: " + e.getMessage(), e);
        }
    }


    public static Integer upsertDetalleSumandoCantidad(Connection cn,
                                                       int idPedido,
                                                       int idProducto,
                                                       int cantidadASumar) {
        final String SQL = """
                INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido)
                VALUES (?, ?, ?, 0)
                ON DUPLICATE KEY UPDATE
                    id_detalle = LAST_INSERT_ID(id_detalle),
                    cantidad   = cantidad + VALUES(cantidad)
                """;
        try (PreparedStatement ps = cn.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadASumar);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1); // id_detalle
            }
            return null; // raro, pero controlable
        } catch (SQLException e) {
            Logger.getLogger(DetallesPedidoDAO.class.getName()).log(Level.SEVERE, "UPSERT detalle", e);
            return null;
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
}
