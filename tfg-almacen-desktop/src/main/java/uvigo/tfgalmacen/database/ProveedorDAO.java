package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProveedorDAO {

    private static final Logger LOGGER = Logger.getLogger(ProveedorDAO.class.getName());


    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }


    public static int getIdProveedorByNombre(Connection conn, String nombreProveedor) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdProveedorByNombre()");
            return -1;
        }

        final String SQL = "SELECT id_proveedor FROM proveedores WHERE nombre = ?";

        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, nombreProveedor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id_proveedor");
                LOGGER.fine("ID del proveedor encontrado: " + id + " (" + nombreProveedor + ")");
                return id;
            } else {
                LOGGER.warning("No se encontró proveedor con nombre: " + nombreProveedor);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo id_proveedor para " + nombreProveedor, e);
        }
        return -1;
    }

}
