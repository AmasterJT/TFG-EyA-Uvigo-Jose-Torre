package uvigo.tfgalmacen.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DataConfig {

    static String URL;
    static String USER;
    static String PASSWORD;
    static String DATABASE_NAME;
    static String DRIVER;

    static {
        try {
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);

            URL = properties.getProperty("url");
            USER = properties.getProperty("user");
            PASSWORD = properties.getProperty("password");
            DATABASE_NAME = properties.getProperty("database_name");
            DRIVER = properties.getProperty("driver");

            URL = URL + DATABASE_NAME;


        } catch (IOException e) {
            System.err.println("Error al cargar las propiedades de configuración: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}