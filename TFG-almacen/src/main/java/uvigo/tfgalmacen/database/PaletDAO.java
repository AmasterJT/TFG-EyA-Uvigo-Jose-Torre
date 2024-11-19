package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaletDAO {

    // Crear palet
    private static final String INSERT_PALET_SQL = "INSERT INTO Palets (id_producto, cantidad, ubicacion) VALUES (?, ?, ?)";

    public static void createPalet(Connection connection, int id_producto, int cantidad, String ubicacion) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PALET_SQL)) {
            preparedStatement.setInt(1, id_producto);
            preparedStatement.setInt(2, cantidad);
            preparedStatement.setString(3, ubicacion);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Palet creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer palets
    private static final String SELECT_ALL_PALETS_SQL = "SELECT * FROM Palets";

    public static void readPalets(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PALETS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_palet = resultSet.getInt("id_palet");
                int id_producto = resultSet.getInt("id_producto");
                int cantidad = resultSet.getInt("cantidad");
                String ubicacion = resultSet.getString("ubicacion");

                System.out.println("ID: " + id_palet + ", Producto ID: " + id_producto + ", Cantidad: " + cantidad + ", Ubicación: " + ubicacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar palet
    private static final String UPDATE_PALET_SQL = "UPDATE Palets SET id_producto = ?, cantidad = ?, ubicacion = ? WHERE id_palet = ?";

    public static void updatePalet(Connection connection, int id_palet, int id_producto, int cantidad, String ubicacion) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PALET_SQL)) {
            preparedStatement.setInt(1, id_producto);
            preparedStatement.setInt(2, cantidad);
            preparedStatement.setString(3, ubicacion);
            preparedStatement.setInt(4, id_palet);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Palet actualizado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar palet
    private static final String DELETE_PALET_SQL = "DELETE FROM Palets WHERE id_palet = ?";

    public static void deletePalet(Connection connection, int id_palet) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PALET_SQL)) {
            preparedStatement.setInt(1, id_palet);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Palet eliminado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}