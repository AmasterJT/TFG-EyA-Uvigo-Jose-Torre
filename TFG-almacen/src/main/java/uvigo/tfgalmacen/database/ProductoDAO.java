package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    // Crear producto
    private static final String INSERT_PRODUCTO_SQL = "INSERT INTO Productos (nombre_producto, descripcion, precio) VALUES (?, ?, ?)";

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
    private static final String SELECT_ALL_PRODUCTOS_SQL = "SELECT * FROM Productos";

    public static void readProductos(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PRODUCTOS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_producto = resultSet.getInt("id_producto");
                String nombre = resultSet.getString("nombre_producto");
                String descripcion = resultSet.getString("descripcion");
                double precio = resultSet.getDouble("precio");

                System.out.println("ID: " + id_producto + ", Nombre: " + nombre + ", Descripción: " + descripcion + ", Precio: " + precio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar producto
    private static final String UPDATE_PRODUCTO_SQL = "UPDATE Productos SET nombre_producto = ?, descripcion = ?, precio = ? WHERE id_producto = ?";

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
    private static final String DELETE_PRODUCTO_SQL = "DELETE FROM Productos WHERE id_producto = ?";

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
}
