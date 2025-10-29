package uvigo.tfgalmacen;

import uvigo.tfgalmacen.almacenManagement.Palet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
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
    public void crearOrdenCompra(Connection conn, String observaciones) throws SQLException {
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


    // Getters/Setters si los necesitas…
    public ArrayList<Palet> getLista_palets_oc() {
        return lista_palets_oc;
    }

    public ArrayList<Proveedor> getLista_proveedores_oc() {
        return lista_proveedores_oc;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("OrdenCompra: ").append(this.CODIGO_OC).append("{\n");
        for (int i = 0; i < lista_palets_oc.size(); i++) {
            out.append("\t").append(lista_proveedores_oc.get(i)).append("\n\t\t ").append(lista_palets_oc.get(i)).append("\n");
        }
        out.append("}");
        return out.toString();
    }

}
