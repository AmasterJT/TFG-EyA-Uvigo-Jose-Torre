package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MovimientoDAO {

    // Crear movimiento
    private static final String INSERT_MOVIMIENTO_SQL = "INSERT INTO movimientos (id_usuario, id_palet, tipo_movimiento, cantidad, observaciones) VALUES (?, ?, ?, ?, ?)";

    public static void createMovimiento(Connection connection, int id_usuario, int id_palet, String tipo_movimiento, int cantidad, String observaciones) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MOVIMIENTO_SQL)) {
            preparedStatement.setInt(1, id_usuario);
            preparedStatement.setInt(2, id_palet);
            preparedStatement.setString(3, tipo_movimiento);
            preparedStatement.setInt(4, cantidad);
            preparedStatement.setString(5, observaciones);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Movimiento creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer movimientos
    private static final String SELECT_ALL_MOVIMIENTOS_SQL = "SELECT * FROM Movimientos";

    public static void readMovimientos(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_MOVIMIENTOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_movimiento = resultSet.getInt("id_movimiento");
                int id_usuario = resultSet.getInt("id_usuario");
                int id_palet = resultSet.getInt("id_palet");
                String tipo_movimiento = resultSet.getString("tipo_movimiento");
                int cantidad = resultSet.getInt("cantidad");
                String observaciones = resultSet.getString("observaciones");

                System.out.println("ID: " + id_movimiento + ", Usuario ID: " + id_usuario + ", Palet ID: " + id_palet +
                        ", Tipo: " + tipo_movimiento + ", Cantidad: " + cantidad + ", Observaciones: " + observaciones);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}