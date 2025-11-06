package uvigo.tfgalmacen.database;

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

public class ProductoDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductoDAO.class.getName());


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


    private static final String ORANGE = "\033[34m";
    private static final String RESET = "\033[0m";

    // Crear producto
    private static final String INSERT_PRODUCTO_SQL = "INSERT INTO productos (nombre_producto, descripcion, precio) VALUES (?, ?, ?)";

    public static void createProducto(Connection connection, String nombre, String descripcion, double precio) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCTO_SQL)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setDouble(3, precio);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Producto creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer productos
    private static final String SELECT_ALL_PRODUCTOS_SQL = "SELECT * FROM productos";

    public static void readProductos(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PRODUCTOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_producto = resultSet.getInt("id_producto");
                String nombre = resultSet.getString("identificador_producto");
                String descripcion = resultSet.getString("descripcion");
                String tipo_producto = resultSet.getString("tipo_producto");


                //System.out.println( ORANGE + "ID: " + RESET + id_producto + ", " + ORANGE + "Nombre: " + RESET + nombre + ORANGE + ", Descripción: " + RESET + descripcion + ", " + ORANGE +  "Tipo_producto: " + RESET + tipo_producto);
                System.out.println(nombre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar producto
    private static final String UPDATE_PRODUCTO_SQL = "UPDATE productos SET nombre_producto = ?, descripcion = ?, precio = ? WHERE id_producto = ?";

    public static void updateProducto(Connection connection, int id_producto, String nombre, String descripcion, double precio) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCTO_SQL)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setDouble(3, precio);
            preparedStatement.setInt(4, id_producto);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Producto actualizado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar producto
    private static final String DELETE_PRODUCTO_SQL = "DELETE FROM productos WHERE id_producto = ?";

    public static void deleteProducto(Connection connection, int id_producto) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCTO_SQL)) {
            preparedStatement.setInt(1, id_producto);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Producto eliminado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Obtener el color del tipo de producto por identificador_producto
    public static String getColorByIdentificadorProducto(Connection connection, String identificadorProducto) {
        String sql = """
                SELECT t.color
                FROM productos p
                JOIN tipos t ON p.tipo_producto = t.id_tipo
                WHERE p.identificador_producto = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, identificadorProducto);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("color");
            } else {
                System.err.println("No se encontró ningún producto con identificador: " + identificadorProducto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el color del tipo para el producto: " + identificadorProducto);
            e.printStackTrace();
        }
        return null;
    }


    private static final String SELECT_ALL_PRODUCT_NAMES_SQL =
            "SELECT identificador_producto FROM productos";

    /**
     * Obtiene todos los nombres (identificadores) de productos.
     *
     * @param connection conexión activa a la base de datos
     * @return lista de nombres de productos
     */
    public static List<String> getAllProductNames(Connection connection) {
        List<String> nombres = new ArrayList<>();

        if (connection == null) {
            LOGGER.warning("Conexión nula al intentar obtener los nombres de productos.");
            return nombres;
        }

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PRODUCT_NAMES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("identificador_producto");
                if (nombre != null) {
                    nombres.add(nombre);
                }
            }

            LOGGER.info("Productos cargados: " + nombres.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los nombres de productos", e);
        }

        return nombres;
    }
}
