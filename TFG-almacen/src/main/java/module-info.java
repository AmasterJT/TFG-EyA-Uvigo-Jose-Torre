module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql; // Dependencia de MySQL

    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}
