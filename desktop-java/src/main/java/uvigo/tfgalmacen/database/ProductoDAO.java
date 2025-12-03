package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
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


    private static final String INSERT_PRODUCTO_SQL2 = """
                INSERT INTO productos (identificador_producto, tipo_producto, descripcion, precio)
                VALUES (?, ?, ?, ?)
            """;

    /**
     * Inserta un nuevo producto en la base de datos.
     *
     * @param conn        conexión activa a la base de datos
     * @param nombre      identificador del producto (nombre)
     * @param tipo        tipo de producto (id_tipo en la tabla tipos)
     * @param descripcion descripción del producto (puede ser null)
     * @param precio      precio del producto (puede ser null)
     */
    public static void crearNuevoProducto(Connection conn,
                                          String nombre,
                                          String tipo,
                                          String descripcion,
                                          String precio) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en crearNuevoProducto()");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(INSERT_PRODUCTO_SQL2, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, tipo);

            // Descripción (puede ser null)
            if (descripcion == null || descripcion.isBlank() || descripcion.equals("-")) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, descripcion);
            }

            // Precio (puede ser null)
            if (precio == null || precio.isBlank() || precio.equals("-")) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, new java.math.BigDecimal(precio));
            }

            int filas = ps.executeUpdate();

            if (filas > 0) {
                LOGGER.fine("Producto '" + nombre + "' creado correctamente en la base de datos.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear nuevo producto: " + e.getMessage(), e);
        }

    }


    public static int getIdProductoByIdentificadorProducto(Connection conn, String identificadorProducto) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getIdProductoByIdentificadorProducto()");
            return -1;
        }

        final String SQL = "SELECT id_producto FROM productos WHERE identificador_producto = ?";

        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, identificadorProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id_producto");
                LOGGER.fine("ID del producto encontrado: " + id + " (" + identificadorProducto + ")");
                return id;
            } else {
                LOGGER.warning("No se encontró producto con identificador: " + identificadorProducto);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo id_producto para " + identificadorProducto, e);
        }
        return -1;
    }


    /**
     * DTO / record para devolver los datos por defecto de un producto
     * según la tabla proveedor_producto.
     */
    public record DefaultProductoData(
            int alto,
            int ancho,
            int largo,
            int unidadesPorPaletDefault
    ) {
    }

    // Puedes ajustar la estrategia (por proveedor, etc.)
    private static final String SQL_GET_DEFAULT_DATA = """
            SELECT alto, ancho, largo, unidades_por_palet_default
            FROM proveedor_producto
            WHERE id_producto = ?
            ORDER BY id_proveedor ASC
            LIMIT 1
            """;

    /**
     * Devuelve los datos por defecto de un producto (alto, ancho, largo, unidades_por_palet_default)
     * tomando el primer registro de proveedor_producto para ese id_producto.
     *
     * @param conn       conexión abierta a la BDD
     * @param idProducto id de la tabla productos
     * @return DefaultProductoData o null si no hay datos
     */
    public static DefaultProductoData getDefaultData(Connection conn, int idProducto) {
        if (conn == null) {
            LOGGER.severe("Conexión nula recibida en getDefaultData");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_DEFAULT_DATA)) {
            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int alto = rs.getInt("alto");
                    int ancho = rs.getInt("ancho");
                    int largo = rs.getInt("largo");
                    int unidades = rs.getInt("unidades_por_palet_default");

                    LOGGER.fine(() -> String.format(
                            "getDefaultData(idProducto=%d) -> alto=%d, ancho=%d, largo=%d, unidades=%d",
                            idProducto, alto, ancho, largo, unidades
                    ));

                    return new DefaultProductoData(alto, ancho, largo, unidades);
                } else {
                    LOGGER.warning("No se encontraron datos por defecto en proveedor_producto para id_producto=" + idProducto);
                    return null;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en getDefaultData(idProducto=" + idProducto + ")", e);
            return null;
        }
    }
}
