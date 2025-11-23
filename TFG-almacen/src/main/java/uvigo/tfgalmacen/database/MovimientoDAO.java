package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MovimientoDAO {

    public static final List<String> OPCIONES_DE_MOVIMIENTO = List.of("Cambio de locacion del palet", "Registrar nuevo Palet", "Eliminar Palet");


}