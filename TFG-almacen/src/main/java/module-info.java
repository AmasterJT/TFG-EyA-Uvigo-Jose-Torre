module uvigo.tfgalmacen {
    requires javafx.controls;
    requires javafx.fxml;


    opens uvigo.tfgalmacen to javafx.fxml;
    exports uvigo.tfgalmacen;
}