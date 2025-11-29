module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires javafx.graphics;
    requires java.logging;
    requires javafx.base;
    requires itextpdf;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires de.mkammerer.argon2.nolibs;

    exports uvigo.tfgalmacen;
    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen.controllers.apartadosAjustesControllers;
    opens uvigo.tfgalmacen.controllers.apartadosAjustesControllers to javafx.fxml;
    exports uvigo.tfgalmacen.almacenManagement;
    opens uvigo.tfgalmacen.almacenManagement to javafx.fxml;
    exports uvigo.tfgalmacen.controllers;
    opens uvigo.tfgalmacen.controllers to javafx.fxml;
    exports uvigo.tfgalmacen.utils;
    opens uvigo.tfgalmacen.utils to javafx.fxml;
    exports uvigo.tfgalmacen.gs1;
    opens uvigo.tfgalmacen.gs1 to javafx.fxml;

}
