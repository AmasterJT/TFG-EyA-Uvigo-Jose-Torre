package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TableLister {

    private static List<String> tableExcludeList = List.of(
            "usuarios",
            "rol_permiso",
            "proveedor_producto",
            "permisos_usuarios",
            "estanterias",
            "movimientos",
            "roles"
    );

    // Función que devuelve una lista de nombres de tablas
    public static List<String> getTables(Connection connection, String databaseName) throws SQLException {
        // Lista para almacenar los nombres de las tablas
        List<String> tableNames = new ArrayList<>();

        // Consulta para obtener los nombres de las tablas
        String query = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = '" + databaseName + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Procesar los resultados y agregar los nombres de las tablas a la lista
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");

            if (tableName != null && tableExcludeList.contains(tableName)) continue;
            tableNames.add(tableName);
        }

        return tableNames;
    }


    public static void listTables(Connection connection, String databaseName) throws SQLException {


        // Consulta para obtener los nombres de las tablas
        String query = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = '" + databaseName + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        System.out.println("Tablas en la base de datos:");
        System.out.println("-\uD83D\uDCC2️" + databaseName);

        // Procesar los resultados
        String separador = "\t -─── ";
        separador = "\t └─ ";
        while (resultSet.next()) {

            String tableName = separador + "\uD83D\uDCDD\u200B " + resultSet.getString("TABLE_NAME");
            System.out.println(tableName);

            // separador = "\t └───";
        }

    }

}