package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Crear usuario
    private static final String INSERT_USER_SQL = "INSERT INTO usuarios (nombre, email, contraseña, id_rol) VALUES (?, ?, ?, ?)";

    public static void createUser(Connection connection, String nombre, String email, String password, int id_rol) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
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
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM usuarios";

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
    private static final String UPDATE_USER_SQL = "UPDATE usuarios SET nombre = ?, email = ?, contraseña = ?, id_rol = ? WHERE id_usuario = ?";

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
    private static final String DELETE_USER_SQL = "DELETE FROM usuarios WHERE id_usuario = ?";

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

    private static final String CHECK_USER_SQL = "SELECT * FROM usuarios WHERE nombre = ? AND contraseña = ?";

    public static boolean SQLcheckUser(Connection connection, String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER_SQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener el rol del usuario a partir del nombre y la contraseña
    private static final String GET_USER_ROLE_SQL = "SELECT id_rol FROM usuarios WHERE nombre = ? AND contraseña = ?";

    public static int getUserRole(Connection connection, String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_ROLE_SQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_rol");
            } else {
                return -1; // Usuario no encontrado o contraseña incorrecta
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Error en la base de datos
        }
    }

    // Obtener el nombre completo del usuario por ID
    private static final String GET_USERNAME_BY_ID_SQL = "SELECT nombre, apellido FROM usuarios WHERE id_usuario = ?";

    public static String getNombreUsuarioById(Connection connection, int id_usuario) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USERNAME_BY_ID_SQL)) {
            preparedStatement.setInt(1, id_usuario);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                return nombre + " " + apellido;
            } else {
                return null; // Usuario no encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Error en la base de datos
        }
    }


    // Obtener el nombre del rol de un usuario por su ID
    private static final String GET_ROLE_NAME_BY_USER_ID_SQL =
            "SELECT r.nombre_rol " +
                    "FROM usuarios u " +
                    "JOIN roles r ON u.id_rol = r.id_rol " +
                    "WHERE u.id_usuario = ?";

    public static String getRoleNameByUserId(Connection connection, int id_usuario) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ROLE_NAME_BY_USER_ID_SQL)) {
            preparedStatement.setInt(1, id_usuario);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("nombre_rol");
            } else {
                return null; // Usuario o rol no encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<User> getAllUsers(Connection connection) {
        List<User> usuarios = new ArrayList<>();

        String sql = "SELECT id_usuario, user_name, nombre, apellido, email FROM usuarios";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int id_usuario = rs.getInt("id_usuario");
                String username = rs.getString("user_name");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String email = rs.getString("email");

                User user = new User(id_usuario, username, nombre, apellido, email, connection);
                usuarios.add(user);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
        }

        return usuarios;
    }


    public static Integer getIdUsuarioByNombre(Connection connection, String username) {
        String sql = "SELECT id_usuario FROM usuarios WHERE user_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_usuario");
            } else {
                System.out.println("⚠️ No se encontró ningún usuario con ese nombre.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID del usuario: " + e.getMessage());
            return null;
        }
    }






}


