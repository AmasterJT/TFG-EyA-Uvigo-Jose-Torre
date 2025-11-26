package uvigo.tfgalmacen.database;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import uvigo.tfgalmacen.PaletSalida;
import uvigo.tfgalmacen.utils.ColorFormatter;

public class PaletSalidaDAO {

    private static final Logger LOGGER = Logger.getLogger(PaletSalidaDAO.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        var ch = new java.util.logging.ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (var h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    public static PaletSalida getPaletSalidaById(Connection conn, int idPaletSalida) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getPaletSalidaById()");
            return null;
        }

        final String SQL = """
                SELECT id_palet_salida, sscc, cantidad_total, numero_productos, id_pedido
                FROM palet_salida
                WHERE id_palet_salida = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, idPaletSalida);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PaletSalida(
                            rs.getInt("id_palet_salida"),
                            rs.getInt("id_pedido"),
                            rs.getString("sscc"),
                            rs.getInt("cantidad_total"),
                            rs.getInt("numero_productos")
                    );
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo PaletSalida con id=" + idPaletSalida, e);
        }

        return null;
    }


    // Record de apoyo: una línea del palet de salida
    public record LineaPaletSalida(int idProducto, int cajas, Integer idDetallePedido) {
    }

    private static final String INSERT_PALET_SALIDA_SQL =
            "INSERT INTO palet_salida (sscc, cantidad_total, numero_productos, id_pedido) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String INSERT_PALET_SALIDA_DETALLE_SQL =
            "INSERT INTO palet_salida_detalle (id_palet_salida, id_producto, cajas) " +
                    "VALUES (?, ?, ?)";

    private static final String UPDATE_DETALLE_PALETIZADO_SQL =
            "UPDATE detalles_pedido SET paletizado = 1 WHERE id_detalle = ?";

    /**
     * Crea un palet_salida con sus detalles y marca las líneas de pedido como paletizadas.
     *
     * @param conn     conexión activa
     * @param sscc     código SSCC del palet
     * @param idPedido id del pedido al que pertenece este palet
     * @param lineas   lista de líneas con producto, cajas e id_detalle_pedido (puede ser null)
     * @return id generado del palet_salida
     */
    public static int crearPaletSalidaConDetalles(Connection conn,
                                                  String sscc,
                                                  int idPedido,
                                                  List<LineaPaletSalida> lineas) throws SQLException {

        Objects.requireNonNull(conn, "Conexión nula en crearPaletSalidaConDetalles");
        Objects.requireNonNull(sscc, "SSCC nulo");
        Objects.requireNonNull(lineas, "Lista de líneas nula");

        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear un palet_salida sin líneas de detalle.");
        }

        int cantidadTotal = lineas.stream().mapToInt(LineaPaletSalida::cajas).sum();
        int numeroProductos = (int) lineas.stream().map(LineaPaletSalida::idProducto).distinct().count();

        boolean prevAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (PreparedStatement psPalet = conn.prepareStatement(
                INSERT_PALET_SALIDA_SQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psDet = conn.prepareStatement(INSERT_PALET_SALIDA_DETALLE_SQL);
             PreparedStatement psUpd = conn.prepareStatement(UPDATE_DETALLE_PALETIZADO_SQL)) {

            // 1) Insertar cabecera palet_salida
            psPalet.setString(1, sscc);
            psPalet.setInt(2, cantidadTotal);
            psPalet.setInt(3, numeroProductos);
            psPalet.setInt(4, idPedido);

            int rows = psPalet.executeUpdate();
            if (rows != 1) {
                conn.rollback();
                throw new SQLException("Error insertando palet_salida: filas afectadas = " + rows);
            }

            int idPaletSalida;
            try (ResultSet rs = psPalet.getGeneratedKeys()) {
                if (!rs.next()) {
                    conn.rollback();
                    throw new SQLException("No se pudo obtener id_palet_salida generado.");
                }
                idPaletSalida = rs.getInt(1);
            }

            // 2) Insertar detalles del palet
            for (LineaPaletSalida linea : lineas) {
                psDet.setInt(1, idPaletSalida);
                psDet.setInt(2, linea.idProducto());
                psDet.setInt(3, linea.cajas());
                psDet.addBatch();
            }
            psDet.executeBatch();

            // 3) Marcar detalles_pedido como paletizados
            for (LineaPaletSalida linea : lineas) {
                if (linea.idDetallePedido() != null) {
                    psUpd.setInt(1, linea.idDetallePedido());
                    psUpd.addBatch();
                }
            }
            psUpd.executeBatch();

            conn.commit();

            LOGGER.info(() -> String.format(
                    "Palet_salida creado (id=%d, sscc=%s, pedido=%d, cantidad_total=%d, productos=%d)",
                    idPaletSalida, sscc, idPedido, cantidadTotal, numeroProductos
            ));

            return idPaletSalida;

        } catch (SQLException e) {
            conn.rollback();
            LOGGER.log(Level.SEVERE, "Error creando palet_salida con detalles", e);
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(prevAutoCommit);
        }
    }

    // DTO sencillo para devolver datos al controlador
    public record PaletSalidaResumen(
            int idPaletSalida,
            String sscc,
            int cantidadTotal,
            int numeroProductos,
            Timestamp fechaCreacion
    ) {
    }

    private static final String SQL_SELECT_BY_PEDIDO = """
            SELECT id_palet_salida, sscc, fecha_creacion, cantidad_total, numero_productos
            FROM palet_salida
            WHERE id_pedido = ?
            ORDER BY fecha_creacion ASC
            """;

    public static List<PaletSalidaResumen> getPaletsSalidaPorPedido(Connection cn, int idPedido) {
        List<PaletSalidaResumen> lista = new ArrayList<>();
        if (cn == null) {
            LOGGER.severe("Conexión nula en getPaletsSalidaPorPedido");
            return lista;
        }

        try (PreparedStatement ps = cn.prepareStatement(SQL_SELECT_BY_PEDIDO)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new PaletSalidaResumen(
                            rs.getInt("id_palet_salida"),
                            rs.getString("sscc"),
                            rs.getInt("cantidad_total"),
                            rs.getInt("numero_productos"),
                            rs.getTimestamp("fecha_creacion")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error obteniendo palets_salida para id_pedido=" + idPedido, e);
        }
        return lista;
    }


    private static final String SELECT_PALETS_SALIDA_AGRUPADOS_SQL =
            "SELECT ps.id_pedido, ps.id_palet_salida " +
                    "FROM palet_salida ps " +
                    "JOIN pedidos p ON ps.id_pedido = p.id_pedido " +
                    "WHERE p.estado <> 'Enviado' " +           // ⚠ excluir pedidos Enviados
                    "  AND (p.enviado = 0 OR p.enviado IS NULL) " + // opcional, por si usas el boolean
                    "ORDER BY ps.id_pedido, ps.id_palet_salida";


    /**
     * Devuelve un mapa: id_pedido -> lista de id_palet_salida asociados.
     * Solo considera pedidos que:
     * - Tienen al menos un palet_salida
     * - Y NO están en estado 'Enviado'
     */
    public static Map<Integer, List<Integer>> getPaletsSalidaAgrupadosPorPedido(Connection conn) {
        Map<Integer, List<Integer>> resultado = new LinkedHashMap<>();

        if (conn == null) {
            LOGGER.severe("Conexión nula en getPaletsSalidaAgrupadosPorPedido()");
            return resultado;
        }

        try (PreparedStatement ps = conn.prepareStatement(SELECT_PALETS_SALIDA_AGRUPADOS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");
                int idPaletSalida = rs.getInt("id_palet_salida");

                resultado
                        .computeIfAbsent(idPedido, _k -> new ArrayList<>())
                        .add(idPaletSalida);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo palets_salida agrupados por pedido", e);
        }

        return resultado;
    }


}
