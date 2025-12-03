package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.*;

public class ProveedorProductoDAO {

    private static final Logger LOGGER = Logger.getLogger(ProveedorProductoDAO.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);
    }

    public static List<String> obtenerIdentificadoresProductosPorProveedor(Connection conn, int idProveedor) throws SQLException {
        String sql = """
                    SELECT p.identificador_producto
                    FROM proveedor_producto pp
                    JOIN productos p ON p.id_producto = pp.id_producto
                    WHERE pp.id_proveedor = ?
                    ORDER BY p.identificador_producto
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> res = new ArrayList<>();
                while (rs.next()) res.add(rs.getString(1));
                return res;
            }
        }
    }


    private static final String INSERT_RELACION_SQL = """
                INSERT INTO proveedor_producto
                (id_proveedor, id_producto, alto, ancho, largo, unidades_por_palet_default)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

    /**
     * Crea la relación entre un proveedor y un producto.
     *
     * @param conn        conexión a la base de datos
     * @param idProducto  id del producto
     * @param idProveedor id del proveedor
     * @param alto        altura del palet
     * @param ancho       ancho del palet
     * @param largo       largo del palet
     * @param unidades    número de unidades por palet (por defecto)
     */
    public static void setRelacionProductoProveedor(Connection conn,
                                                    int idProducto,
                                                    int idProveedor,
                                                    int alto,
                                                    int ancho,
                                                    int largo,
                                                    int unidades) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en setRelacionProductoProveedor()");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(INSERT_RELACION_SQL)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ps.setInt(3, alto);
            ps.setInt(4, ancho);
            ps.setInt(5, largo);
            ps.setInt(6, unidades > 0 ? unidades : 1); // seguridad: mínimo 1 unidad

            int filas = ps.executeUpdate();
            if (filas > 0) {
                LOGGER.fine(String.format("✅ Relación creada: proveedor=%d, producto=%d, %dx%dx%d (%d unidades)",
                        idProveedor, idProducto, alto, ancho, largo, unidades));
            } else {
                LOGGER.warning("No se insertó ninguna fila en proveedor_producto.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear relación proveedor-producto", e);
        }
    }

}
