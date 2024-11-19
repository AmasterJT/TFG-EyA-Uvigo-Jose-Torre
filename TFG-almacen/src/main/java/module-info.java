module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}