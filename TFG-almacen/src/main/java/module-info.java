module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jdi;
    requires java.desktop;
    requires java.management;
    requires java.sql;


    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}