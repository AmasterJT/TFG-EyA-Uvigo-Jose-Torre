module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
    requires java.sql;


    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}