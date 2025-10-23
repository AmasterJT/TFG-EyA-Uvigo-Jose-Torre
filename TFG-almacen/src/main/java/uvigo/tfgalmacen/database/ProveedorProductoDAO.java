package uvigo.tfgalmacen.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorProductoDAO {

    public static List<String> obtenerIdentificadoresProductosPorProveedor(Connection conn, int idProveedor) throws SQLException {
        String sql = """
                    SELECT p.identificador_producto
                    FROM proveedor_producto pp
                    JOIN productos p ON p.id_producto = pp.id_producto
                    WHERE pp.id_proveedor = ?
                    ORDER BY p.identificador_producto
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> res = new ArrayList<>();
                while (rs.next()) res.add(rs.getString(1));
                return res;
            }
        }
    }
}
