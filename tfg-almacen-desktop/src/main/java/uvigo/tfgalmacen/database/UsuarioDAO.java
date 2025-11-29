package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.utils.PasswordUtils.verifyPassword;

public class UsuarioDAO {


    private static final Logger LOGGER = Logger.getLogger(UsuarioDAO.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }


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
                    return new User(
                            rs.getString("user_name"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2"),
                            rs.getString("email"),
                            rs.getInt("id_rol")
                    );
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

    // Eliminar un usuario

    private static final String CHECK_USER_SQL =
            "SELECT contraseña FROM usuarios WHERE nombre = ?";

    public static boolean SQLcheckUser(Connection connection, String username, String password) {

        if (connection == null) return false;

        try (PreparedStatement ps = connection.prepareStatement(CHECK_USER_SQL)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            // 1️⃣ Comprobar si el usuario existe
            if (!rs.next()) {
                System.out.println("❌ Usuario no encontrado: " + username);
                return false;
            }

            // 2️⃣ Obtener hash almacenado
            String hash = rs.getString("contraseña");

            System.out.println("🔐 Hash almacenado: " + hash);
            if (hash == null) {
                System.out.println("❌ El usuario no tiene contraseña registrada.");
                return false;
            }

            hash = hash.trim(); // evita errores por espacios

            // 3️⃣ Verificar contraseña usando Argon2
            boolean ok = PasswordUtils.verifyPassword(password, hash);

            System.out.println(ok
                    ? "✅ Login correcto"
                    : "❌ Contraseña incorrecta");

            return ok;

        } catch (Exception e) {
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


    private static final String UPDATE_PASSWORD_SQL =
            "UPDATE usuarios SET contraseña = ? WHERE id_usuario = ?";

    /**
     * Actualiza la contraseña de un usuario.
     *
     * @param connection conexión activa a la base de datos
     * @param idUsuario  id del usuario al que se le cambia la contraseña
     * @param nuevaPass  nueva contraseña en texto plano (o ya hasheada, según tu diseño)
     * @return true si se actualizó exactamente 1 fila; false en caso contrario
     */
    public static boolean actualizarContrasena(Connection connection, int idUsuario, String nuevaPass) {
        if (connection == null) {
            LOGGER.severe("Conexión nula en actualizarContrasena");
            return false;
        }
        if (nuevaPass == null || nuevaPass.isBlank()) {
            LOGGER.warning("Contraseña vacía o nula en actualizarContrasena (id_usuario=" + idUsuario + ")");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_PASSWORD_SQL)) {
            ps.setString(1, nuevaPass);
            ps.setInt(2, idUsuario);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                LOGGER.info("Contraseña actualizada correctamente para id_usuario=" + idUsuario);
                return true;
            } else if (rows == 0) {
                LOGGER.warning("No se encontró usuario con id_usuario=" + idUsuario + ". No se actualizó contraseña.");
                return false;
            } else {
                LOGGER.warning("Se actualizaron " + rows + " filas al cambiar contraseña de id_usuario=" + idUsuario);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la contraseña para id_usuario=" + idUsuario, e);
            return false;
        }
    }

}


