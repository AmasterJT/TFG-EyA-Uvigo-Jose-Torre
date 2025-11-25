package uvigo.tfgalmacen.database;

import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaletDAO {

    private static final Logger LOGGER = Logger.getLogger(PaletDAO.class.getName());

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

    private static final String SQL_IS_POSICION_LIBRE = """
                SELECT COUNT(*) 
                FROM palets 
                WHERE estanteria = ? 
                  AND balda = ? 
                  AND posicion = ? 
                  AND delante = ?
            """;

    public static boolean isUbicacionLibre(
            Connection conn,
            int estanteria,
            int balda,
            int posicion,
            boolean delante
    ) throws SQLException {


        try (PreparedStatement stmt = conn.prepareStatement(SQL_IS_POSICION_LIBRE)) {
            stmt.setInt(1, estanteria);
            stmt.setInt(2, balda);
            stmt.setInt(3, posicion);
            stmt.setBoolean(4, delante);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;   // true si NO hay palet → lugar libre
                }
            }
        }

        return false; // Si algo falla, lo tratamos como ocupado por seguridad
    }


    private static final String SQL_GET_ID_BY_IDENT =
            "SELECT id_palet FROM palets WHERE identificador = ?";

    private static final String SQL_COUNT_OCCUPANCY_EXCLUDING_ID =
            "SELECT COUNT(*) FROM palets " +
                    "WHERE estanteria = ? AND balda = ? AND posicion = ? AND delante = ? " +
                    "AND id_palet <> ?";

    private static final String SQL_UPDATE_POSITION =
            "UPDATE palets SET estanteria = ?, balda = ?, posicion = ?, delante = ? " +
                    "WHERE id_palet = ?";

    /**
     * Mueve el palet identificado a una nueva ubicación si no está ocupada por otro palet.
     *
     * @param conn          conexión JDBC abierta (no nula)
     * @param identificador identificador único del palet (columna palets.identificador)
     * @param estanteria    nueva estantería destino
     * @param balda         nueva balda destino
     * @param posicion      nueva posición destino
     * @param delante       nueva bandera "delante" destino
     * @return true si se actualizó la ubicación; false si la ubicación está ocupada, el palet no existe,
     * o no se pudo actualizar.
     */
    public static boolean updateUbicacionSiLibre(Connection conn,
                                                 String identificador,
                                                 int estanteria,
                                                 int balda,
                                                 int posicion,
                                                 boolean delante) {
        if (conn == null) {
            LOGGER.severe("Conexión nula en updateUbicacionSiLibre");
            return false;
        }
        if (identificador == null || identificador.isBlank()) {
            LOGGER.warning("Identificador vacío/nulo en updateUbicacionSiLibre");
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1) Obtener id del palet
            Integer idPalet = null;
            try (PreparedStatement ps = conn.prepareStatement(SQL_GET_ID_BY_IDENT)) {
                ps.setString(1, identificador);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idPalet = rs.getInt(1);
                    }
                }
            }
            if (idPalet == null) {
                LOGGER.warning("No existe palet con identificador=" + identificador);
                conn.rollback();
                return false;
            }

            // 2) Verificar ocupación en destino (excluyendo el mismo palet)
            int ocupados = 0;
            try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_OCCUPANCY_EXCLUDING_ID)) {
                ps.setInt(1, estanteria);
                ps.setInt(2, balda);
                ps.setInt(3, posicion);
                ps.setBoolean(4, delante);
                ps.setInt(5, idPalet);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) ocupados = rs.getInt(1);
                }
            }

            if (ocupados > 0) {
                Integer finalIdPalet = idPalet;
                LOGGER.info(() -> String.format(
                        "Ubicación ocupada (est:%d, bal:%d, pos:%d, delante:%s). No se mueve el palet %s (id=%d).",
                        estanteria, balda, posicion, delante, identificador, finalIdPalet
                ));
                conn.rollback();
                return false;
            }

            // 3) Actualizar posición
            int updated;
            try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_POSITION)) {
                ps.setInt(1, estanteria);
                ps.setInt(2, balda);
                ps.setInt(3, posicion);
                ps.setBoolean(4, delante);
                ps.setInt(5, idPalet);
                updated = ps.executeUpdate();
            }

            if (updated == 1) {
                conn.commit();
                Integer finalIdPalet1 = idPalet;
                LOGGER.info(() -> String.format(
                        "Palet %s (id=%d) movido a est:%d, bal:%d, pos:%d, delante:%s",
                        identificador, finalIdPalet1, estanteria, balda, posicion, delante
                ));
                return true;
            } else {
                conn.rollback();
                Integer finalIdPalet2 = idPalet;
                LOGGER.warning(() -> String.format(
                        "No se actualizó la ubicación del palet %s (id=%d).",
                        identificador, finalIdPalet2
                ));
                return false;
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            LOGGER.log(Level.SEVERE, "Error moviendo palet identificador=" + identificador, e);
            return false;
        } finally {
            try {
                conn.setAutoCommit(oldAutoCommit);
            } catch (SQLException ignore) {
            }
        }
    }


    private static final String DELETE_PALET_BY_IDENTIFICADOR_SQL = "DELETE FROM palets WHERE identificador = ?";

    public static void deletePaletByIdentificador(Connection cn, int identificadorPalet) throws SQLException {
        if (cn == null) {
            throw new SQLException("Conexión nula en deletePedidoById");
        }
        try (PreparedStatement ps = cn.prepareStatement(DELETE_PALET_BY_IDENTIFICADOR_SQL)) {
            ps.setInt(1, identificadorPalet);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                LOGGER.fine(() -> "Palet eliminado. identificador=" + identificadorPalet);
            } else {
                LOGGER.warning(() -> "No existe palet con identificador=" + identificadorPalet);
            }
        }
    }


    // ---------------------------------------------------------
    // 1) ¿Está libre el identificador de palet?
    // ---------------------------------------------------------
    private static final String SQL_EXISTE_IDENTIFICADOR =
            "SELECT 1 FROM palets WHERE identificador = ? LIMIT 1";

    /**
     * Devuelve true si el identificador NO está usado por ningún palet.
     *
     * @param connection conexión activa a la BDD
     * @param id         identificador propuesto (numérico) que se almacenará como String
     */
    public static boolean iSidPaletvalido(Connection connection, int id) {
        if (connection == null) {
            LOGGER.severe("Conexión nula en iSidPaletvalido()");
            return false;
        }

        String identificador = String.valueOf(id);

        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTE_IDENTIFICADOR)) {
            ps.setString(1, identificador);

            try (ResultSet rs = ps.executeQuery()) {
                boolean yaExiste = rs.next();
                if (yaExiste) {
                    LOGGER.fine(() -> "Identificador de palet ya usado: " + identificador);
                    return false; // NO es válido (ya ocupado)
                } else {
                    LOGGER.fine(() -> "Identificador de palet libre: " + identificador);
                    return true;  // Sí es válido
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error comprobando identificador de palet=" + identificador, e);
            // En caso de error, mejor devolver false (no lo usamos)
            return false;
        }
    }

    // ---------------------------------------------------------
    // 2) Insertar palet en la base de datos
    // ---------------------------------------------------------
    private static final String SQL_INSERT_PALET =
            "INSERT INTO palets " +
                    "(identificador, id_producto, alto, ancho, largo, cantidad_de_producto, " +
                    " estanteria, balda, posicion, delante) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Inserta un nuevo palet en la tabla palets.
     *
     * @param connection    conexión activa
     * @param identificador identificador único del palet (VARCHAR)
     * @param idProducto    identificador del producto (productos.identificador_producto)
     * @param alto          alto del palet
     * @param ancho         ancho del palet
     * @param largo         largo del palet
     * @param cantidad      cantidad_de_producto
     * @param estanteria    nº de estantería
     * @param balda         nº de balda
     * @param posicion      nº de posición
     * @param delante       true si va delante, false si va detrás
     * @return true si se insertó una fila
     * @throws SQLException si algo va mal
     */
    public static boolean insertarPalet(
            Connection connection,
            String identificador,
            String idProducto,
            int alto,
            int ancho,
            int largo,
            int cantidad,
            int estanteria,
            int balda,
            int posicion,
            boolean delante
    ) throws SQLException {

        if (connection == null) {
            throw new SQLException("Conexión nula en insertarPalet()");
        }

        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT_PALET)) {
            ps.setString(1, identificador);
            ps.setString(2, idProducto);
            ps.setInt(3, alto);
            ps.setInt(4, ancho);
            ps.setInt(5, largo);
            ps.setInt(6, cantidad);
            ps.setInt(7, estanteria);
            ps.setInt(8, balda);
            ps.setInt(9, posicion);
            ps.setBoolean(10, delante);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                LOGGER.info(() -> String.format(
                        "Palet insertado: ident=%s, prod=%s, est=%d, bal=%d, pos=%d, delante=%s, cant=%d",
                        identificador, idProducto, estanteria, balda, posicion, delante, cantidad
                ));
                return true;
            } else {
                LOGGER.warning("insertarPalet() no afectó exactamente a 1 fila.");
                return false;
            }
        }
    }

}
