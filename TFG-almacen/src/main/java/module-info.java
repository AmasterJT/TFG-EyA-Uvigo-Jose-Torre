module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}