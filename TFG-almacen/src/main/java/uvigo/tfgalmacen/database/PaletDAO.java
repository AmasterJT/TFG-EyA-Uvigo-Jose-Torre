package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.almacenManagement.Palet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaletDAO {

    // Crear palet
    private static final String INSERT_PALET_SQL = "INSERT INTO palets (id_producto, cantidad, estanteria, balda, posicion) VALUES (?, ?, ?, ?, ?)";

    public static void createPalet(Connection connection, int id_producto, int cantidad, int estanteria, int balda, int posicion) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PALET_SQL)) {
            preparedStatement.setInt(1, id_producto);
            preparedStatement.setInt(2, cantidad);
            preparedStatement.setInt(3, estanteria);
            preparedStatement.setInt(3, balda);
            preparedStatement.setInt(3, posicion);


            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Palet creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer palets
    private static final String SELECT_ALL_PALETS_SQL = "SELECT * FROM palets";

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

    public static List<Palet> getAllPalets(Connection connection) {
        List<Palet> palets = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PALETS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                palets.add(new Palet(String.valueOf(resultSet.getInt("alto")),
                        String.valueOf(resultSet.getInt("ancho")),
                        String.valueOf(resultSet.getInt("largo")),

                        resultSet.getString("id_producto"),
                        String.valueOf(resultSet.getInt("cantidad_de_producto")),

                        resultSet.getString("identificador"),

                        resultSet.getInt("estanteria"),
                        resultSet.getInt("balda"),
                        String.valueOf(resultSet.getInt("posicion")),
                        String.valueOf(resultSet.getBoolean("delante"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palets;
    }

    // Actualizar palet
    private static final String UPDATE_PALET_SQL = "UPDATE palets SET id_producto = ?, cantidad = ?, ubicacion = ? WHERE id_palet = ?";

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
    private static final String DELETE_PALET_SQL = "DELETE FROM palets WHERE id_palet = ?";

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