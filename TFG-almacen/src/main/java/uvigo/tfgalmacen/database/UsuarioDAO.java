package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Crear usuario
    private static final String INSERT_USER_SQL = "INSERT INTO Usuarios (nombre, email, contraseña, id_rol) VALUES (?, ?, ?, ?)";

    public static void createUser(Connection connection, String nombre, String email, String contraseña, int id_rol) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, contraseña);
            preparedStatement.setInt(4, id_rol);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Usuario creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer todos los usuarios
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM Usuarios";

    public static void readUsers(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_usuario = resultSet.getInt("id_usuario");
                String nombre = resultSet.getString("nombre");
                String email = resultSet.getString("email");
                String contraseña = resultSet.getString("contraseña");
                int id_rol = resultSet.getInt("id_rol");
                String fecha_registro = resultSet.getString("fecha_registro");

                System.out.println("ID: " + id_usuario + ", Nombre: " + nombre + ", Email: " + email +
                        ", Rol ID: " + id_rol + ", Fecha Registro: " + fecha_registro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar un usuario
    private static final String UPDATE_USER_SQL = "UPDATE Usuarios SET nombre = ?, email = ?, contraseña = ?, id_rol = ? WHERE id_usuario = ?";

    public static void updateUser(Connection connection, int id_usuario, String nombre, String email, String contraseña, int id_rol) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, contraseña);
            preparedStatement.setInt(4, id_rol);
            preparedStatement.setInt(5, id_usuario);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Usuario actualizado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar un usuario
    private static final String DELETE_USER_SQL = "DELETE FROM Usuarios WHERE id_usuario = ?";

    public static void deleteUser(Connection connection, int id_usuario) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {
            preparedStatement.setInt(1, id_usuario);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Usuario eliminado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
