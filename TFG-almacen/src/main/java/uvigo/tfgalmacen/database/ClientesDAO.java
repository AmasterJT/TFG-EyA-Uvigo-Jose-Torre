package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.Cliente;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientesDAO {
    private static final Logger LOGGER = Logger.getLogger(ClientesDAO.class.getName());

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


    private static final String SELECT_NOMBRE_BY_ID_SQL = "SELECT nombre FROM clientes WHERE id_cliente = ?";

    // Método que devuelve el nombre del cliente dado su ID
    public static String getNombreClienteById(Connection connection, int id_cliente) {
        String nombre = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_NOMBRE_BY_ID_SQL)) {
            preparedStatement.setInt(1, id_cliente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    nombre = resultSet.getString("nombre");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombre;
    }

    private static final String SELECT_ALL_CLIENTES_SQL =
            "SELECT id_cliente, nombre, direccion, telefono, email, fecha_registro, ultima_actualizacion " +
                    "FROM clientes";

    /**
     * Devuelve un ArrayList con todos los clientes de la base de datos.
     *
     * @param connection conexión activa a la base de datos
     * @return ArrayList de objetos Cliente con todos los datos
     */
    public static ArrayList<Cliente> getAllClientes(Connection connection) {
        ArrayList<Cliente> clientes = new ArrayList<>();

        if (connection == null) {
            LOGGER.warning("Conexión nula al intentar obtener la lista de clientes.");
            return clientes;
        }

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_CLIENTES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getTimestamp("ultima_actualizacion")
                );
                clientes.add(cliente);
            }

            LOGGER.info("Clientes obtenidos: " + clientes.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la lista de clientes", e);
        }

        return clientes;
    }
}
