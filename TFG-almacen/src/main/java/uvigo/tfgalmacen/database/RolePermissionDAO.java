package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolePermissionDAO {
    // Colores ANSI para consola
    private static final String RESET = "\033[0m";  // Resetea el color
    private static final String GREEN = "\033[32m"; // Verde
    private static final String RED = "\033[31m";   // Rojo
    private static final String BLUE = "\033[34m";  // Azul

    // Consulta SQL para obtener los roles y permisos
    private static final String SELECT_ROLES_PERMISOS_SQL =
            "SELECT r.nombre_rol, p.permiso, rp.estado " +
                    "FROM Roles r " +
                    "JOIN RolPermiso rp ON r.id_rol = rp.id_rol " +
                    "JOIN PerimisosUsuarios p ON rp.id_permiso = p.id_permiso " +
                    "ORDER BY r.id_rol, p.id_permiso";

    // Método para asignar un color específico a cada rol
    private static String getRoleColor(String rol) {
        switch (rol.toLowerCase()) {
            case "sysadmin":
                return "\033[35m"; // Color púrpura
            case "gestor almacén":
                return "\033[36m"; // Color cian
            case "supervisor":
                return "\033[33m"; // Color amarillo
            case "operario":
                return "\033[32m"; // Color verde
            case "mantenimiento":
                return "\033[37m"; // Color blanco
            case "administración":
                return "\033[31m"; // Color rojo
            case "proveedor":
                return "\033[34m"; // Color azul
            case "cliente":
                return "\033[35m"; // Color rojo
            default:
                return RESET; // Resetea a color por defecto
        }
    }

    // Método para imprimir los roles y permisos
    public static void printRolesAndPermissions(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLES_PERMISOS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Encabezado de la tabla
            System.out.println("\n\n+-----------------------+-------------------------------------------+-----------+");
            System.out.println("| Nombre del Rol        | Permiso                                   | Estado    |");
            System.out.println("+-----------------------+-------------------------------------------+-----------+");

            // Iterar sobre los resultados y mostrar los datos
            while (resultSet.next()) {
                String nombreRol = resultSet.getString("nombre_rol");
                String permiso = resultSet.getString("permiso");
                String estado = resultSet.getString("estado");


                // Asignar color al rol y permiso
                String rolColor = getRoleColor(nombreRol);

                // Colorear el estado
                String estadoColor = switch (estado.toLowerCase()) {
                    case "activo" -> GREEN;
                    case "inactivo" -> RED;
                    case "ver" -> BLUE;
                    default -> RESET;
                };

                // Imprimir cada fila de la tabla con colores aplicados
                System.out.printf("| %-30s | %-50s | %-18s |\n",
                        rolColor + nombreRol + RESET,
                        rolColor + permiso + RESET,
                        estadoColor + estado + RESET);
            }

            // Final de la tabla
            System.out.println("+-----------------------+-------------------------------------------+-----------+");

        } catch (SQLException e) {
            System.err.println("Error al recuperar los roles y permisos: " + e.getMessage());
        }
    }
}
