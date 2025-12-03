package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class RolePermissionDAO {
    // Colores ANSI para consola
    private static final String RESET = "\033[0m";  // Resetea el color
    private static final String GREEN = "\033[32m"; // Verde
    private static final String RED = "\033[31m";   // Rojo

    // Consulta SQL para obtener el nombre del rol por ID
    private static final String SELECT_ROLE_NAME_BY_ID_SQL = "SELECT nombre_rol FROM roles WHERE id_rol = ?";

    // Método para obtener el nombre del rol según el id_rol
    public static String getRoleNameById(Connection connection, int id_rol) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLE_NAME_BY_ID_SQL)) {
            preparedStatement.setInt(1, id_rol);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("nombre_rol");
            } else {
                return null; // No se encontró ningún rol con ese ID
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el nombre del rol: " + e.getMessage());
            return null;
        }
    }


    /**
     * Obtiene el ID de un rol dado su nombre.
     *
     * @param connection conexión activa a la base de datos
     * @param roleName   nombre del rol a buscar
     * @return id del rol si existe, o -1 si no se encuentra
     */
    public static int getRoleIdByName(Connection connection, String roleName) {
        String sql = "SELECT id_rol FROM roles WHERE LOWER(nombre_rol) = LOWER(?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_rol");
                } else {
                    System.out.println("\033[33m⚠️ No se encontró el rol con nombre: " + roleName + "\033[0m");
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.err.println("\033[31m❌ Error al obtener el ID del rol: " + e.getMessage() + "\033[0m");
            return -1;
        }
    }


    /**
     * Obtiene todos los nombres de los roles disponibles en la base de datos.
     *
     * @param connection conexión activa a la base de datos.
     * @return una lista con los nombres de todos los roles (ordenados por ID ascendente).
     */
    public static List<String> getAllRoleNames(Connection connection) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT nombre_rol FROM roles ORDER BY id_rol ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("nombre_rol"));
            }

            System.out.println(GREEN + "✅ Roles cargados correctamente (" + roles.size() + " encontrados)" + RESET);

        } catch (SQLException e) {
            System.err.println(RED + "❌ Error al obtener los nombres de los roles: " + e.getMessage() + RESET);
        }

        return roles;
    }

}
