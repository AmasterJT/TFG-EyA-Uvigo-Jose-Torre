package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class pedidosController {


    List<String> ESTADOS_DEL_PEDIDO = List.of("Pendiente", "En proceso", "Cancelado", "Completado");


    @FXML
    private ListView<?> pedidiosCanceladosList;

    @FXML
    private ScrollPane pedidiosCanceladosScroll;

    @FXML
    private ListView<?> pedidiosEnCursoList;

    @FXML
    private ScrollPane pedidiosEnCursoScroll;

    @FXML
    private ListView<?> pedidiosPendientesList;

    @FXML
    private ScrollPane pedidiosPendientesScroll;
}
