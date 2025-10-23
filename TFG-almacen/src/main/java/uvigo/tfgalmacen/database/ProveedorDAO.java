package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    private static final String CYAN = "\033[36m";
    private static final String RESET = "\033[0m";

    // =========================================================
    // CONSULTA: OBTENER TODOS LOS NOMBRES DE PROVEEDORES
    // =========================================================
    private static final String SELECT_ALL_NOMBRES_SQL =
            "SELECT nombre FROM proveedores ORDER BY nombre ASC";

    /**
     * Devuelve una lista con los nombres de todos los proveedores registrados.
     *
     * @param connection conexión activa a la base de datos
     * @return lista de nombres de proveedores (vacía si no hay resultados)
     */
    public static List<String> getAllProveedorNames(Connection connection) {
        List<String> nombres = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_NOMBRES_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                nombres.add(resultSet.getString("nombre"));
            }

            System.out.println(CYAN + "Proveedores cargados: " + RESET + nombres.size());

        } catch (SQLException e) {
            System.err.println("Error al obtener los nombres de los proveedores:");
            e.printStackTrace();
        }

        return nombres;
    }

    // =========================================================
    // (Opcional) CRUD básico para proveedores
    // =========================================================

    private static final String INSERT_PROVEEDOR_SQL =
            "INSERT INTO proveedores (nombre, direccion, telefono, email, nif_cif, contacto) VALUES (?, ?, ?, ?, ?, ?)";

    public static void createProveedor(Connection connection, String nombre, String direccion,
                                       String telefono, String email, String nif_cif, String contacto) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_PROVEEDOR_SQL)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setString(3, telefono);
            ps.setString(4, email);
            ps.setString(5, nif_cif);
            ps.setString(6, contacto);

            int result = ps.executeUpdate();
            if (result > 0) {
                System.out.println("Proveedor creado exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al crear proveedor:");
            e.printStackTrace();
        }
    }

    private static final String SELECT_ALL_PROVEEDORES_SQL = "SELECT * FROM proveedores";

    public static void readProveedores(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PROVEEDORES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_proveedor");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                String email = rs.getString("email");
                String contacto = rs.getString("contacto");

                System.out.println(CYAN + "ID: " + RESET + id +
                        ", " + CYAN + "Nombre: " + RESET + nombre +
                        ", " + CYAN + "Teléfono: " + RESET + telefono +
                        ", " + CYAN + "Email: " + RESET + email +
                        ", " + CYAN + "Contacto: " + RESET + contacto);
            }

        } catch (SQLException e) {
            System.err.println("Error al leer los proveedores:");
            e.printStackTrace();
        }
    }

    private static final String DELETE_PROVEEDOR_SQL = "DELETE FROM proveedores WHERE id_proveedor = ?";

    public static void deleteProveedor(Connection connection, int idProveedor) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_PROVEEDOR_SQL)) {
            ps.setInt(1, idProveedor);

            int result = ps.executeUpdate();
            if (result > 0) {
                System.out.println("Proveedor eliminado correctamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor:");
            e.printStackTrace();
        }
    }

    private static final String UPDATE_PROVEEDOR_SQL =
            "UPDATE proveedores SET nombre = ?, direccion = ?, telefono = ?, email = ?, nif_cif = ?, contacto = ? WHERE id_proveedor = ?";

    public static void updateProveedor(Connection connection, int idProveedor, String nombre, String direccion,
                                       String telefono, String email, String nif_cif, String contacto) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_PROVEEDOR_SQL)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setString(3, telefono);
            ps.setString(4, email);
            ps.setString(5, nif_cif);
            ps.setString(6, contacto);
            ps.setInt(7, idProveedor);

            int result = ps.executeUpdate();
            if (result > 0) {
                System.out.println("Proveedor actualizado correctamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor:");
            e.printStackTrace();
        }
    }
}
