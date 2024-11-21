module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasypt;
    requires mysql.connector.j; // Dependencia de MySQL

    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}
