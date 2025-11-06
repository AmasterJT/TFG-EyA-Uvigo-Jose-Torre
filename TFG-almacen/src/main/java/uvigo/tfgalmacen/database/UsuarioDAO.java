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
    private static final String INSERT_USER_SQL = "INSERT INTO usuarios (user_name, nombre, apellido1, apellido2, email, contraseña, id_rol, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    // 1) Obtener todos los usernames (activos o todos, como prefieras)
    private static final String SQL_ALL_USERNAMES = """
                SELECT user_name
                FROM usuarios
                ORDER BY user_name
            """;

    public static List<String> getAllUsernames(Connection c) {
        List<String> res = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SQL_ALL_USERNAMES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) res.add(rs.getString(1));
        } catch (SQLException e) {
            System.err.println("getAllUsernames: " + e.getMessage());
        }
        return res;
    }

    // 2) Obtener un usuario por username
    private static final String SQL_USER_BY_USERNAME = """
                SELECT u.user_name, u.nombre, u.apellido1, u.apellido2, u.email, u.id_rol
                FROM usuarios u
                WHERE u.user_name = ?
                LIMIT 1
            """;

    public static User getUserByUsername(Connection c, String username) {
        try (PreparedStatement ps = c.prepareStatement(SQL_USER_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(
                            rs.getString("user_name"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2"),
                            rs.getString("email"),
                            rs.getInt("id_rol")
                    );
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("getUserByUsername: " + e.getMessage());
        }
        return null;
    }

    // 3) Actualizar perfil de usuario (sin cambiar password desde aquí)
    private static final String SQL_UPDATE_USER_PROFILE = """
                UPDATE usuarios
                SET nombre = ?,
                    apellido1 = ?,
                    apellido2 = ?,
                    email = ?,
                    id_rol = ?
                WHERE user_name = ?
            """;

    public static boolean updateUserProfile(Connection c,
                                            String username,
                                            String nombre,
                                            String apellido1,
                                            String apellido2,
                                            String email,
                                            int idRol) {
        try (PreparedStatement ps = c.prepareStatement(SQL_UPDATE_USER_PROFILE)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido1);
            ps.setString(3, apellido2);
            ps.setString(4, email);
            ps.setInt(5, idRol);
            ps.setString(6, username);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateUserProfile: " + e.getMessage());
            return false;
        }
    }

    // (Opcional) Cambiar contraseña si más adelante lo necesitas
    private static final String SQL_UPDATE_PASSWORD = """
                UPDATE usuarios SET contraseña = ? WHERE user_name = ?
            """;

    public static boolean updatePassword(Connection c, String username, String hashPassword) {
        try (PreparedStatement ps = c.prepareStatement(SQL_UPDATE_PASSWORD)) {
            ps.setString(1, hashPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updatePassword: " + e.getMessage());
            return false;
        }
    }

    public static void createUser(Connection connection, String user_name, String nombre, String apellido1, String apellido2, String email, String password, int id_rol) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, user_name);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellido1);
            preparedStatement.setString(4, apellido2);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, password);
            preparedStatement.setInt(7, id_rol);
            preparedStatement.setInt(8, 0);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Usuario creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean createUser(Connection connection, User nuevo_usuario, String password, int id_rol) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, nuevo_usuario.getUsername());
            preparedStatement.setString(2, nuevo_usuario.getName());
            preparedStatement.setString(3, nuevo_usuario.getApellido1());
            preparedStatement.setString(4, nuevo_usuario.getApellido2());
            preparedStatement.setString(5, nuevo_usuario.getEmail());
            preparedStatement.setString(6, password);
            preparedStatement.setInt(7, id_rol);
            preparedStatement.setInt(8, 0);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
    private static final String GET_NAME_BY_ID_SQL = "SELECT nombre, apellido1 FROM usuarios WHERE id_usuario = ?";

    public static String getNombreUsuarioById(Connection connection, int id_usuario) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_NAME_BY_ID_SQL)) {
            preparedStatement.setInt(1, id_usuario);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido1");
                return nombre + " " + apellido;
            } else {
                return null; // Usuario no encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Error en la base de datos
        }
    }


    private static final String GET_USERNAME_BY_ID_SQL = "SELECT user_name FROM usuarios WHERE id_usuario = ?";

    public static String getUsernameById(Connection connection, int id_usuario) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USERNAME_BY_ID_SQL)) {
            preparedStatement.setInt(1, id_usuario);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("user_name");
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

        String sql = "SELECT id_usuario, user_name, nombre, apellido1, apellido2, email, id_rol FROM usuarios";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int id_usuario = rs.getInt("id_usuario");
                String username = rs.getString("user_name");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String email = rs.getString("email");
                int rol = rs.getInt("id_rol");

                User user = new User(id_usuario, username, nombre, apellido1, apellido2, email, rol);
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


    /**
     * Comprueba si un usuario ya existe en la base de datos por su nombre de usuario.
     *
     * @param connection conexión activa a la base de datos.
     * @param username   nombre de usuario que se desea comprobar.
     * @return true si el usuario existe, false en caso contrario o si ocurre un error.
     */
    public static boolean userExists(Connection connection, String username) {
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE LOWER(user_name) = LOWER(?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error comprobando existencia del usuario: " + e.getMessage());
        }

        return false;
    }


    public static boolean emailExists(Connection connection, String email) {
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE LOWER(email) = LOWER(?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error comprobando existencia del email: " + e.getMessage());
        }

        return false;
    }


    // === 3) Eliminar usuario por username ===
    public static boolean deleteUserByUsername(Connection connection, String username) {
        final String SQL = "DELETE FROM usuarios WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando usuario '" + username + "': " + e.getMessage());
            return false;
        }
    }


    public static List<UserLite> getAllUsersExcept(Connection conn, int excludeUserId) {
        final String SQL = "SELECT id_usuario, user_name, nombre, apellido1, apellido2 " +
                "FROM usuarios WHERE id_usuario <> ?";
        List<UserLite> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new UserLite(
                            rs.getInt("id_usuario"),
                            rs.getString("user_name"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2")
                    ));
                }
            }
        } catch (SQLException e) {
            java.util.logging.Logger.getLogger(UsuarioDAO.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error en getAllUsersExcept", e);
        }
        return lista;
    }

    public static class UserLite {
        public final int id;
        public final String username;
        public final String nombre;
        public final String ap1;
        public final String ap2;

        public UserLite(int id, String username, String nombre, String ap1, String ap2) {
            this.id = id;
            this.username = username;
            this.nombre = nombre;
            this.ap1 = ap1;
            this.ap2 = ap2;
        }

        @Override
        public String toString() {
            return username + " - " + nombre + " " + ap1;
        }
    }


    // En UsuarioDAO
    public static boolean deleteUserByUsernameThrows(Connection conn, String username) throws SQLException {
        final String SQL = "DELETE FROM usuarios WHERE user_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, username);
            int n = ps.executeUpdate();
            return n > 0;
        }
        // No catch aquí: dejamos que la SQLException suba al caller
    }

}


