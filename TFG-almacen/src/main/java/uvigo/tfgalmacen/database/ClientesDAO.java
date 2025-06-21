package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientesDAO {

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

    // Puedes añadir otros métodos aquí si lo necesitas, como create, update, delete, etc.
}
