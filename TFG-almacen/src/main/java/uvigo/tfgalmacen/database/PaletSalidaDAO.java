package uvigo.tfgalmacen.database;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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
}
