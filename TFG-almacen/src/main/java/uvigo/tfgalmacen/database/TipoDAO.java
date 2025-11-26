package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.almacenManagement.Tipo;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class TipoDAO {

    private static final Logger LOGGER = Logger.getLogger(TipoDAO.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) h.setLevel(Level.ALL);
    }


    // Inserción estricta: falla si el id_tipo ya existe (PK duplicada)
    private static final String SQL_INSERT_TIPO_STRICT = """
                INSERT INTO tipos (id_tipo, color)
                VALUES (?, ?)
            """;

    /**
     * Inserta un tipo de forma estricta (fallará si el id ya existe).
     */
    public static boolean insertTipoStrict(Connection conn, Tipo tipo) {
        if (conn == null || tipo == null) {
            LOGGER.severe("Conexión o tipo nulo en insertTipoStrict()");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_TIPO_STRICT)) {
            ps.setString(1, tipo.getIdTipo());
            ps.setString(2, tipo.getColor());
            int rows = ps.executeUpdate();
            LOGGER.fine(() -> "insertTipoStrict filas=" + rows + " id_tipo=" + tipo.getIdTipo());
            return rows > 0;
        } catch (SQLIntegrityConstraintViolationException dup) {
            LOGGER.warning("El tipo ya existe (PK duplicada): " + tipo.getIdTipo());
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error insertando tipo (strict): " + tipo.getIdTipo(), e);
            return false;
        }
    }


    private static final String SQL_TIPOS_BY_PROVEEDOR = """
                SELECT DISTINCT t.id_tipo
                FROM tipos t
                JOIN productos p ON p.tipo_producto = t.id_tipo
                JOIN proveedor_producto pp ON pp.id_producto = p.id_producto
                WHERE pp.id_proveedor = ?
                ORDER BY t.id_tipo;
            """;

    /**
     * Devuelve una lista con los nombres (id_tipo) de los tipos que pertenecen a un proveedor dado.
     *
     * @param conn        conexión abierta a la base de datos
     * @param idProveedor ID del proveedor
     * @return Lista de nombres de tipos asociados al proveedor
     */
    public static List<String> getTiposByProveedor(Connection conn, int idProveedor) {
        List<String> tipos = new ArrayList<>();

        if (conn == null) {
            LOGGER.severe("Conexión nula en getTiposByProveedor()");
            return tipos;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_TIPOS_BY_PROVEEDOR)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tipos.add(rs.getString("id_tipo"));
                }
            }
            LOGGER.fine(() -> "Tipos obtenidos para proveedor " + idProveedor + ": " + tipos.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo tipos para proveedor " + idProveedor, e);
        }

        return tipos;
    }
}
