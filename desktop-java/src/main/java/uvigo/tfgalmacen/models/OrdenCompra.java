package uvigo.tfgalmacen.models;

import uvigo.tfgalmacen.almacenManagement.Palet;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class OrdenCompra {

    private static final Logger LOGGER = Logger.getLogger(OrdenCompra.class.getName());

    private final ArrayList<Palet> lista_palets_oc;
    private final ArrayList<Proveedor> lista_proveedores_oc;

    private String CODIGO_OC = "";

    public OrdenCompra(ArrayList<Palet> lista_palets_oc, ArrayList<Proveedor> lista_proveedores_oc) {
        this.lista_palets_oc = lista_palets_oc;
        this.lista_proveedores_oc = lista_proveedores_oc;
    }


    /**
     * Crea la cabecera de una orden de compra y devuelve su codigo_referencia.
     * También te deja el id_oc por si lo necesitas para insertar los detalles.
     */
    public void crearCodigoOrdenCompra(Connection conn, String observaciones) throws SQLException {
        String sql = "{ CALL crear_orden_compra(?, ?, ?) }";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, observaciones);
            cs.registerOutParameter(2, Types.INTEGER);   // p_id_oc
            cs.registerOutParameter(3, Types.VARCHAR);   // p_codigo_referencia

            cs.execute();

            int idOc = cs.getInt(2);
            this.CODIGO_OC = cs.getString(3);

        }
    }


    public String getCODIGO_OC() {
        return CODIGO_OC;
    }

    public void setCODIGO_OC(String CODIGO_OC) {
        this.CODIGO_OC = CODIGO_OC;
    }

    /**
     * Inserta las líneas de detalle de una OC ya creada, identificada por su codigo_referencia.
     *
     * @param conn conexión JDBC abierta
     * @return número de filas insertadas en detalle_orden_compra
     * @throws SQLException si falla cualquier paso (FK, integridad, etc.)
     */
    public void insertarDetalleOrdenCompraPorCodigo(Connection conn) throws SQLException {

        String codigoReferencia = this.CODIGO_OC;
        ArrayList<Palet> palets = this.lista_palets_oc;
        ArrayList<Proveedor> proveedores = this.lista_proveedores_oc;


        if (palets == null || proveedores == null || palets.size() != proveedores.size()) {
            throw new IllegalArgumentException("Las listas palets y proveedores deben existir y tener el mismo tamaño.");
        }

        final String SQL_ID_OC = "SELECT id_oc FROM orden_compra WHERE codigo_referencia = ?";
        final String SQL_ID_PRODUCTO = "SELECT id_producto FROM productos WHERE identificador_producto = ?";
        final String SQL_INSERT_DETALLE =
                "INSERT INTO detalle_orden_compra " +
                        "(id_oc, id_proveedor, id_producto, cantidad, estanteria, balda, posicion, delante) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (PreparedStatement psIdOc = conn.prepareStatement(SQL_ID_OC);
             PreparedStatement psIdProducto = conn.prepareStatement(SQL_ID_PRODUCTO);
             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT_DETALLE)) {

            // 1) Resolver id_oc
            Integer idOc = null;
            psIdOc.setString(1, codigoReferencia);
            try (ResultSet rs = psIdOc.executeQuery()) {
                if (rs.next()) idOc = rs.getInt(1);
            }
            if (idOc == null) {
                throw new SQLException("No existe orden_compra con codigo_referencia=" + codigoReferencia);
            }

            int total = 0;

            // 2) Preparar batch
            for (int i = 0; i < palets.size(); i++) {
                var palet = palets.get(i);
                var proveedor = proveedores.get(i);

                // 2.1) Resolver id_producto por identificador de negocio
                Integer idProducto = null;
                psIdProducto.setString(1, palet.getIdProducto()); // p.ej. "TRUPOSEPT FP"
                try (ResultSet rs = psIdProducto.executeQuery()) {
                    if (rs.next()) idProducto = rs.getInt(1);
                }
                if (idProducto == null) {
                    throw new SQLException("Producto no encontrado en 'productos' para identificador: " + palet.getIdProducto());
                }

                // 2.2) id_proveedor desde tu objeto de dominio
                Integer idProveedor = proveedor.getIdProveedor();
                if (idProveedor == null) {
                    throw new SQLException("Proveedor sin id_proveedor para la posición " + i);
                }

                // IMPORTANTE: existe una FK (id_proveedor, id_producto) -> proveedor_producto.
                // Si esa relación no existe, fallará aquí al ejecutar (bueno para integridad).

                // 2.3) Completar sentencia
                psInsert.setInt(1, idOc);
                psInsert.setInt(2, idProveedor);
                psInsert.setInt(3, idProducto);
                psInsert.setInt(4, palet.getCantidadProducto());

                // Ubicación tentativa (pueden ser null/0 según tu modelo)
                // Si tus getters devuelven String, parsea a Integer; ajusta según tu clase Palet.
                psInsert.setObject(5, palet.getEstanteria(), Types.INTEGER);
                psInsert.setObject(6, palet.getBalda(), Types.INTEGER);
                psInsert.setObject(7, palet.getPosicion(), Types.INTEGER);
                psInsert.setObject(8, palet.isDelante(), Types.BOOLEAN);

                psInsert.addBatch();
                total++;
            }

            // 3) Ejecutar batch
            psInsert.executeBatch();
            conn.commit();


        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    // Getters/Setters si los necesitas…
    public ArrayList<Palet> getLista_palets_oc() {
        return lista_palets_oc;
    }

    public ArrayList<Proveedor> getLista_proveedores_oc() {
        return lista_proveedores_oc;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("OrdenCompra: ").append(this.CODIGO_OC).append(" {\n");
        for (int i = 0; i < lista_palets_oc.size(); i++) {
            out.append("\t").append(lista_proveedores_oc.get(i)).append("\n\t\t ").append(lista_palets_oc.get(i)).append("\n");
        }
        out.append("}");
        return out.toString();
    }

}
