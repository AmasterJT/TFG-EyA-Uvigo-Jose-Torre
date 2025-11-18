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


}
