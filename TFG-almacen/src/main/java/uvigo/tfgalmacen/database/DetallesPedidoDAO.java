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
            "SELECT p.identificador_producto, dp.cantidad " +
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

                productos.add(new ProductoPedido(identificadorProducto, cantidad));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }
}
