module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires javafx.graphics;
    requires java.logging;


    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
    exports uvigo.tfgalmacen.almacenManagement;
    opens uvigo.tfgalmacen.almacenManagement to javafx.fxml;
    exports uvigo.tfgalmacen.controllers;
    opens uvigo.tfgalmacen.controllers to javafx.fxml;
    exports uvigo.tfgalmacen.utils;
    opens uvigo.tfgalmacen.utils to javafx.fxml;

}
