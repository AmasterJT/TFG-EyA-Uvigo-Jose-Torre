package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetallePedidoDAO {

    // Crear detalle de pedido
    private static final String INSERT_DETALLE_PEDIDO_SQL = "INSERT INTO DetallesPedido (id_pedido, id_palet, cantidad) VALUES (?, ?, ?)";

    public static void createDetallePedido(Connection connection, int id_pedido, int id_palet, int cantidad) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DETALLE_PEDIDO_SQL)) {
            preparedStatement.setInt(1, id_pedido);
            preparedStatement.setInt(2, id_palet);
            preparedStatement.setInt(3, cantidad);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Detalle de pedido creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}