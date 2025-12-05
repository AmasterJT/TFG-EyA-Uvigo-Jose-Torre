package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.models.Transportista;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransportistaDAO {

    private static final Logger LOGGER = Logger.getLogger(TransportistaDAO.class.getName());

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

    // ==========================
    // SQLs
    // ==========================

    private static final String INSERT_TRANSPORTISTA_SQL = """
            INSERT INTO transportistas
            (nombre_empresa, nombre_conductor, telefono, email, matricula,
             tipo_transporte, direccion, nif_cif, notas)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT id_transportista, nombre_empresa, nombre_conductor,
                   telefono, email, matricula, tipo_transporte,
                   direccion, nif_cif, notas, fecha_registro
            FROM transportistas
            ORDER BY nombre_empresa
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT id_transportista, nombre_empresa, nombre_conductor,
                   telefono, email, matricula, tipo_transporte,
                   direccion, nif_cif, notas, fecha_registro
            FROM transportistas
            WHERE id_transportista = ?
            """;

    private static final String SELECT_BY_NOMBRE_LIKE_SQL = """
            SELECT id_transportista, nombre_empresa, nombre_conductor,
                   telefono, email, matricula, tipo_transporte,
                   direccion, nif_cif, notas, fecha_registro
            FROM transportistas
            WHERE nombre_empresa LIKE ?
            ORDER BY nombre_empresa
            """;

    private static final String UPDATE_TRANSPORTISTA_SQL = """
            UPDATE transportistas
            SET nombre_empresa   = ?,
                nombre_conductor = ?,
                telefono         = ?,
                email            = ?,
                matricula        = ?,
                tipo_transporte  = ?,
                direccion        = ?,
                nif_cif          = ?,
                notas            = ?
            WHERE id_transportista = ?
            """;

    private static final String DELETE_TRANSPORTISTA_SQL = """
            DELETE FROM transportistas
            WHERE id_transportista = ?
            """;

    // ==========================
    // Métodos públicos
    // ==========================

    /**
     * Inserta un nuevo transportista en la BD.
     *
     * @param conn          conexión abierta
     * @param transportista objeto con los datos a insertar
     * @return id generado (id_transportista) o -1 si falla
     */
    public static int insertarTransportista(Connection conn, Transportista transportista) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en insertarTransportista()");
            return -1;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_TRANSPORTISTA_SQL,
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, transportista.getNombreEmpresa());
            ps.setString(2, transportista.getNombreConductor());
            ps.setString(3, transportista.getTelefono());
            ps.setString(4, transportista.getEmail());
            ps.setString(5, transportista.getMatricula());
            ps.setString(6, transportista.getTipoTransporte());
            ps.setString(7, transportista.getDireccion());
            ps.setString(8, transportista.getNifCif());
            ps.setString(9, transportista.getNotas());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                LOGGER.warning("No se insertó ningún transportista (0 filas afectadas).");
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    LOGGER.fine("✅ Transportista insertado con id=" + idGenerado);
                    return idGenerado;
                }
            }

            LOGGER.warning("No se pudo obtener el id generado del transportista.");
            return -1;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error insertando transportista", e);
            return -1;
        }
    }

    /**
     * Obtiene la lista completa de transportistas.
     */
    public static List<Transportista> getTodos(Connection conn) {
        List<Transportista> lista = new ArrayList<>();
        if (conn == null) {
            LOGGER.severe("Conexión nula en getTodos()");
            return lista;
        }

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToTransportista(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo todos los transportistas", e);
        }
        return lista;
    }

    /**
     * Obtiene un transportista por su id.
     */
    public static Transportista getById(Connection conn, int idTransportista) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en getById()");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, idTransportista);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransportista(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo transportista por id=" + idTransportista, e);
        }
        return null;
    }

    /**
     * Busca transportistas cuyo nombre_empresa contenga el texto dado (LIKE %nombre%).
     */
    public static List<Transportista> buscarPorNombre(Connection conn, String fragmentoNombre) {
        List<Transportista> lista = new ArrayList<>();
        if (conn == null) {
            LOGGER.severe("Conexión nula en buscarPorNombre()");
            return lista;
        }

        String pattern = (fragmentoNombre == null || fragmentoNombre.isBlank())
                ? "%"
                : "%" + fragmentoNombre + "%";

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_NOMBRE_LIKE_SQL)) {
            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToTransportista(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error buscando transportistas por nombre (pattern=" + pattern + ")", e);
        }
        return lista;
    }

    /**
     * Actualiza un transportista existente.
     *
     * @return true si se ha actualizado al menos una fila.
     */
    public static boolean actualizar(Connection conn, Transportista t) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en actualizar()");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(UPDATE_TRANSPORTISTA_SQL)) {
            ps.setString(1, t.getNombreEmpresa());
            ps.setString(2, t.getNombreConductor());
            ps.setString(3, t.getTelefono());
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getMatricula());
            ps.setString(6, t.getTipoTransporte());
            ps.setString(7, t.getDireccion());
            ps.setString(8, t.getNifCif());
            ps.setString(9, t.getNotas());
            ps.setInt(10, t.getIdTransportista());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                LOGGER.fine("✅ Transportista actualizado (id=" + t.getIdTransportista() + ")");
                return true;
            } else {
                LOGGER.warning("No se encontró transportista para actualizar (id=" + t.getIdTransportista() + ")");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error actualizando transportista (id=" + t.getIdTransportista() + ")", e);
            return false;
        }
    }

    /**
     * Elimina un transportista por id.
     *
     * @return true si se eliminó al menos una fila.
     */
    public static boolean eliminar(Connection conn, int idTransportista) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en eliminar()");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(DELETE_TRANSPORTISTA_SQL)) {
            ps.setInt(1, idTransportista);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                LOGGER.fine("✅ Transportista eliminado (id=" + idTransportista + ")");
                return true;
            } else {
                LOGGER.warning("No se encontró transportista para eliminar (id=" + idTransportista + ")");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error eliminando transportista (id=" + idTransportista + ")", e);
            return false;
        }
    }

    // ==========================
    // Helper privado
    // ==========================

    private static Transportista mapRowToTransportista(ResultSet rs) throws SQLException {
        Transportista t = new Transportista();

        t.setIdTransportista(rs.getInt("id_transportista"));
        t.setNombreEmpresa(rs.getString("nombre_empresa"));
        t.setNombreConductor(rs.getString("nombre_conductor"));
        t.setTelefono(rs.getString("telefono"));
        t.setEmail(rs.getString("email"));
        t.setMatricula(rs.getString("matricula"));
        t.setTipoTransporte(rs.getString("tipo_transporte"));
        t.setDireccion(rs.getString("direccion"));
        t.setNifCif(rs.getString("nif_cif"));
        t.setNotas(rs.getString("notas"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            LocalDateTime ldt = ts.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            t.setFechaRegistro(ldt);
        }

        return t;
    }
}
