/*
package uvigo.tfgalmacen.database;

public class DataConfig {

    // Datos de conexión
     static final String URL = "jdbc:mysql://localhost:3306/tfg_almacenDB"; // URL de tu base de datos
     static final String USER = "root"; // Tu usuario de MySQL
     static final String PASSWORD = "Amaster123*"; // Tu contraseña de MySQL
}
*/

package uvigo.tfgalmacen.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DataConfig {

    static String URL;
    static String USER;
    static String PASSWORD;

    static {
        try {
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);

            URL = properties.getProperty("url");
            USER = properties.getProperty("user");
            PASSWORD = properties.getProperty("password");


        } catch (IOException e) {
            System.err.println("Error al cargar las propiedades de configuración: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}