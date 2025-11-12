package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.ProductoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetallesPedidoDAO {

    // Consulta para obtener los productos y cantidades según el código de referencia del pedido
    private static final String SELECT_PRODUCTOS_POR_PEDIDO_SQL =
            "SELECT p.identificador_producto, dp.cantidad, dp.estado_producto_pedido " +
                    "FROM detalles_pedido dp " +
                    "JOIN pedidos pe ON dp.id_pedido = pe.id_pedido " +
                    "JOIN productos p ON dp.id_producto = p.id_producto " +
                    "WHERE pe.codigo_referencia = ?";

    public static List<ProductoPedido> getProductosPorCodigoReferencia(Connection connection, String codigoReferencia) {
        List<ProductoPedido> productos = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCTOS_POR_PEDIDO_SQL)) {
            preparedStatement.setString(1, codigoReferencia);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String identificadorProducto = resultSet.getString("identificador_producto");
                int cantidad = resultSet.getInt("cantidad");
                Boolean isComplete = resultSet.getBoolean("estado_producto_pedido");

                productos.add(new ProductoPedido(identificadorProducto, cantidad, isComplete));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }


    private static final String INSERT_DETALLE_SQL = """
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
            System.err.println("❌ Conexión nula al insertar detalle de pedido.");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(INSERT_DETALLE_SQL)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("\t - ✅ Producto insertado correctamente (pedido=" + idPedido +
                        ", producto=" + idProducto + ", cantidad=" + cantidad + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar detalle de pedido: " + e.getMessage());
        }

        return false;
    }
}
