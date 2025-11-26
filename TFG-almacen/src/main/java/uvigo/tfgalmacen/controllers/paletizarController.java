package uvigo.tfgalmacen.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.*;
import uvigo.tfgalmacen.database.*;
import uvigo.tfgalmacen.gs1.GS1Utils;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.windowComponentAndFuncionalty;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.database.ClientesDAO.getClienteById;
import static uvigo.tfgalmacen.database.DataConfig.COMPANY_GS1_CODE;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.getProductosPorCodigoReferencia;
import static uvigo.tfgalmacen.database.PedidoDAO.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.limpiarGridPane;
import static uvigo.tfgalmacen.RutasFicheros.ITEM_PALET_FINAL_FXML;


import static uvigo.tfgalmacen.database.PaletSalidaDAO.LineaPaletSalida;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_warning;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_error;

public class paletizarController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(paletizarController.class.getName());

    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }

    private int paletsListosCount = 0;
    int idCliente;
    int idUsuario;
    String username;
    String codigo_referencia_pedido;


    @FXML
    private ComboBox<String> combo_pedido_primera_hora;

    @FXML
    private ComboBox<String> combo_pedido_segunda_hora;

    @FXML
    private ComboBox<String> combo_usuario;

    @FXML
    private Label cantidad_total_palet;

    @FXML
    private Label productos_palet_label;

    @FXML
    private Label cliente_label;

    @FXML
    private Label email_label;

    @FXML
    private Label telefono_label;

    @FXML
    private GridPane grid_en_curso_primera_hora;

    @FXML
    private GridPane grid_en_curso_segunda_hora;

    @FXML
    private GridPane grid_palets_Listos;

    @FXML
    private ScrollPane paletsListosScroll;

    @FXML
    private GridPane grid_productos_en_palet;

    @FXML
    private ScrollPane pedidosEnCursoPrimeraHoraScroll;

    @FXML
    private ScrollPane pedidosEnCursoSegundaHoraScroll;

    @FXML
    private ScrollPane productos_en_palet_scroll;

    @FXML
    private Button crear_palet_salida_btn;


    public static final List<ItemPaletizarController> allItemControllers = new ArrayList<>();

    private List<ProductoPedido> productos_del_pedido;

    private final Map<String, User> cacheUsuarios = new LinkedHashMap<>();

    private Pedido pedido_para_detallar;

    private final int COLUMS = 1;

    private static final String PLACEHOLDER_USUARIO = "Selecciona un usuario";


    private final Map<ItemPaletizarController, AnchorPane> nodoPorItem = new HashMap<>();


    // Item -> grid de origen (primera / segunda hora)
    private final Map<ItemPaletizarController, GridPane> gridOrigenPorItem = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allItemControllers.clear();
        configurarScrollsYGrids();

        combo_usuario.setPromptText(PLACEHOLDER_USUARIO);
        cargarUsernamesYCache();

        combo_usuario.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null && !nv.equals(PLACEHOLDER_USUARIO)) {
                limpiarGridsPedidosOrigen();

                cargarDatosUsuarioDesdeCache(nv);
            }
        });

        productos_en_palet_scroll.setFitToWidth(true);
        grid_productos_en_palet.prefWidthProperty().bind(productos_en_palet_scroll.widthProperty());

        if (crear_palet_salida_btn != null) {
            crear_palet_salida_btn.setOnAction(_ -> crearPaletSalida());
        }


    }

    private void agregarPaletListoAlGrid(PaletSalidaDAO.PaletSalidaResumen data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ITEM_PALET_FINAL_FXML));
            AnchorPane itemRoot = loader.load();

            // Si tu item tiene controller, puedes pasarle los datos
            try {
                Object ctrl = loader.getController();
                if (ctrl instanceof ItemPaletFinalController c) {
                    c.setData(
                            codigo_referencia_pedido,
                            data.sscc(),
                            Objects.requireNonNull(getClienteById(Main.connection, idCliente)).getNombre(),
                            data.cantidadTotal(),
                            data.numeroProductos(),
                            data.fechaCreacion()
                    );
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "No se pudo inicializar ItemPaletFinalController con datos del palet_salida", e);
            }

            int column = paletsListosCount;
            int row = 0;

            grid_palets_Listos.add(itemRoot, column, row);
            GridPane.setMargin(itemRoot, new Insets(5));
            paletsListosCount++;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al agregar item de palet listo al grid_palets_Listos", e);
        }
    }

    private void configurarScrollsYGrids() {

        if (pedidosEnCursoPrimeraHoraScroll != null) {
            pedidosEnCursoPrimeraHoraScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pedidosEnCursoPrimeraHoraScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            if (grid_en_curso_primera_hora != null) {
                grid_en_curso_primera_hora.prefWidthProperty().bind(
                        pedidosEnCursoPrimeraHoraScroll.widthProperty().subtract(20)
                );
            }
        }

        if (pedidosEnCursoSegundaHoraScroll != null) {
            pedidosEnCursoSegundaHoraScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pedidosEnCursoSegundaHoraScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            if (grid_en_curso_segunda_hora != null) {
                grid_en_curso_segunda_hora.prefWidthProperty().bind(
                        pedidosEnCursoSegundaHoraScroll.widthProperty().subtract(20)
                );
            }
        }

        if (productos_en_palet_scroll != null) {
            productos_en_palet_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            productos_en_palet_scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Muy importante: que se ajuste al ancho, pero NO a la altura
            productos_en_palet_scroll.setFitToWidth(true);
            productos_en_palet_scroll.setFitToHeight(false);

            if (grid_productos_en_palet != null) {
                grid_productos_en_palet.prefWidthProperty().bind(
                        productos_en_palet_scroll.widthProperty().subtract(20)
                );
            }
        }
    }


    public void setData(Pedido pedido_para_detallar) {
        this.pedido_para_detallar = pedido_para_detallar;


        productos_del_pedido = getProductosPorCodigoReferencia(
                Main.connection,
                pedido_para_detallar.getCodigo_referencia()
        );


        LOGGER.info("Renderizando productos del pedido: " + pedido_para_detallar.getCodigo_referencia());
        renderizarProductos(productos_del_pedido, grid_en_curso_primera_hora);

    }


    private void renderizarProductos(List<ProductoPedido> productos_del_pedido, GridPane grid) {
        limpiarGridPane(grid);
        int column = 0, row = 1;

        try {
            for (ProductoPedido producto_del_pedido : productos_del_pedido) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ITEM_PALETIZAR_FXML));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemPaletizarController itemController = fxmlLoader.getController();
                itemController.setData(producto_del_pedido);
                itemController.setPaletizarController(this);

                // Registrar nodo y grid de origen
                nodoPorItem.put(itemController, anchorPane);
                anchorPane.setUserData(itemController);
                gridOrigenPorItem.putIfAbsent(itemController, grid);
                anchorPane.getProperties().put("controller", itemController);
                allItemControllers.add(itemController);

                if (column == COLUMS) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }

            LOGGER.fine("Productos renderizados correctamente: " + productos_del_pedido.size());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar itemDetallesPedido.fxml o al renderizar productos.", e);
        }
    }

    public void moverItemAProductosEnPalet(ItemPaletizarController item) {
        AnchorPane nodo = nodoPorItem.get(item);
        if (nodo == null) {
            LOGGER.warning("moverItemAProductosEnPalet: nodo null para item.");
            return;
        }

        // Detectar grid de origen (por si no estaba guardado)
        GridPane origen = gridOrigenPorItem.get(item);
        if (origen == null) {
            if (grid_en_curso_primera_hora.getChildren().contains(nodo)) {
                origen = grid_en_curso_primera_hora;
            } else if (grid_en_curso_segunda_hora.getChildren().contains(nodo)) {
                origen = grid_en_curso_segunda_hora;
            }
            if (origen != null) {
                gridOrigenPorItem.put(item, origen);
            }
        }

        if (origen != null) {
            origen.getChildren().remove(nodo);
            compactarGrid(origen);
        }

        int row = grid_productos_en_palet.getChildren().size();
        grid_productos_en_palet.add(nodo, 0, row);
        GridPane.setMargin(nodo, new Insets(10));

        // Adaptar visualmente como "en palet"
        item.mostrarComoProductoEnPalet(true);

        // Scroll del palet hacia arriba
        Platform.runLater(() -> scrollTopSmooth(productos_en_palet_scroll));

        updateInfoPalet();

        LOGGER.info("Item movido a grid_productos_en_palet.");
    }

    public void devolverItemAOrigen(ItemPaletizarController item) {
        AnchorPane nodo = nodoPorItem.get(item);
        if (nodo == null) {
            LOGGER.warning("devolverItemAOrigen: nodo null para item.");
            return;
        }

        GridPane origen = gridOrigenPorItem.get(item);
        if (origen == null) {
            LOGGER.warning("devolverItemAOrigen: grid de origen null para item.");
            return;
        }

        // Quitar del grid de palet
        grid_productos_en_palet.getChildren().remove(nodo);
        compactarGrid(grid_productos_en_palet);

        // Añadir de nuevo al grid original
        int row = origen.getChildren().size();
        origen.add(nodo, 0, row);
        GridPane.setMargin(nodo, new Insets(10));

        // Volver a mostrar los botones y la región
        item.mostrarComoProductoEnPalet(false);

        // Scroll arriba del grid origen
        Platform.runLater(() -> {
            if (origen == grid_en_curso_primera_hora) {
                pedidosEnCursoPrimeraHoraScroll.setVvalue(0);
            } else if (origen == grid_en_curso_segunda_hora) {
                pedidosEnCursoSegundaHoraScroll.setVvalue(0);
            }
        });

        updateInfoPalet();

        LOGGER.info("Item devuelto al grid de origen.");
    }

    private void compactarGrid(GridPane grid) {
        List<Node> nodos = new ArrayList<>(grid.getChildren());
        grid.getChildren().clear();

        int row = 0;
        int col = 0;
        for (Node n : nodos) {
            grid.add(n, col, row++);
            GridPane.setMargin(n, new Insets(10));
        }
    }

    private void cargarUsernamesYCache() {
        try {
            // 1) Obtener IDs de usuarios que tienen pedidos
            List<Integer> usuariosConPedidos = getUsuariosConPedidos(Main.connection);

            if (usuariosConPedidos.isEmpty()) {
                LOGGER.warning("No hay usuarios con pedidos asignados.");
                combo_usuario.getItems().clear();
                combo_usuario.setDisable(true);
                combo_usuario.setPromptText("No hay usuarios con pedidos");
                return;
            }

            // 2) Obtener TODOS los usuarios
            List<User> users = UsuarioDAO.getAllUsers(Main.connection);

            cacheUsuarios.clear();
            List<String> usernamesFiltrados = new ArrayList<>();

            // 3) Filtrar SOLO los usuarios que están en la lista de pedidos
            for (User u : users) {
                if (usuariosConPedidos.contains(u.getId_usuario())) {
                    cacheUsuarios.put(u.getUsername(), u);
                    usernamesFiltrados.add(u.getUsername());
                }
            }

            // 4) Ordenar alfabéticamente
            usernamesFiltrados.sort(String::compareToIgnoreCase);

            combo_usuario.getItems().setAll(usernamesFiltrados);
            combo_usuario.getSelectionModel().clearSelection();
            combo_usuario.setDisable(false);

            combo_usuario.setPromptText(PLACEHOLDER_USUARIO);

            combo_usuario.setConverter(new StringConverter<>() {
                @Override
                public String toString(String username) {
                    if (username == null) return "";
                    User u = cacheUsuarios.get(username);
                    if (u == null) return username;

                    return username;  // lo que muestras en el desplegable
                }

                @Override
                public String fromString(String s) {
                    return s;
                }
            });

            LOGGER.info("Usuarios cargados en ComboBox: " + usernamesFiltrados.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando usuarios filtrados", e);
        }
    }

    private void cargarDatosUsuarioDesdeCache(String username) {
        if (username == null || username.isBlank()) {
            return;
        }

        User u = cacheUsuarios.get(username);
        if (u == null) {
            try {
                u = UsuarioDAO.getUserByUsername(Main.connection, username);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fallo al cargar usuario desde BD: " + username, e);
            }
        }
        if (u == null) {
            LOGGER.warning("Usuario no encontrado: " + username);
            return;
        }

        idUsuario = u.getId_usuario();
        username = u.getUsername();
        String finalUsername = username;
        LOGGER.fine(() -> "Cargando pedidos asignados al usuario id=" + idUsuario + " (" + finalUsername + ")");

        try {
            // Pedidos de primera y segunda hora
            List<String> pedidosPrimera = getCodigosPedidoPorUsuarioYHora(
                    Main.connection, idUsuario, "primera_hora");
            List<String> pedidosSegunda = getCodigosPedidoPorUsuarioYHora(
                    Main.connection, idUsuario, "segunda_hora");

            // Rellenar combos
            combo_pedido_primera_hora.getItems().setAll(pedidosPrimera);
            combo_pedido_segunda_hora.getItems().setAll(pedidosSegunda);

            combo_pedido_primera_hora.getSelectionModel().clearSelection();
            combo_pedido_segunda_hora.getSelectionModel().clearSelection();

            combo_pedido_primera_hora.setPromptText(
                    pedidosPrimera.isEmpty() ? "Sin pedidos (1ª hora)" : "Pedidos 1ª hora");
            combo_pedido_segunda_hora.setPromptText(
                    pedidosSegunda.isEmpty() ? "Sin pedidos (2ª hora)" : "Pedidos 2ª hora");

            combo_pedido_primera_hora.setDisable(pedidosPrimera.isEmpty());
            combo_pedido_segunda_hora.setDisable(pedidosSegunda.isEmpty());

            String finalUsername1 = username;
            LOGGER.info(() -> String.format(
                    "Pedidos para usuario %s -> 1ª hora: %d, 2ª hora: %d",
                    finalUsername1, pedidosPrimera.size(), pedidosSegunda.size()));

            // Configurar listeners para renderizar productos al seleccionar pedido
            configurarListenersPedidos();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando pedidos del usuario " + username, e);
        }


        // Al cambiar de usuario, limpiamos los datos de cliente hasta que haya pedido
        actualizarDatosCliente(null);
    }

    private void configurarListenersPedidos() {
        combo_pedido_primera_hora.setOnAction(_ -> {
            String codigo = combo_pedido_primera_hora.getValue();
            if (codigo == null || codigo.isBlank()) return;

            try {
                Pedido p = getPedidoPorCodigo(Main.connection, codigo);

                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                System.out.println(p);

                if (p == null) {
                    LOGGER.warning("No se encontró pedido con código: " + codigo);
                    actualizarDatosCliente(null);
                    return;
                }

                // 🔹 Actualizar datos del cliente
                actualizarDatosCliente(p);

                codigo_referencia_pedido = p.getCodigo_referencia();

                List<ProductoPedido> productos2 = getProductosPorCodigoReferencia(
                        Main.connection,
                        p.getCodigo_referencia()
                );

                List<ProductoPedido> productos = getProductosPorCodigoReferencia(
                        Main.connection,
                        p.getCodigo_referencia()
                );
                productos.clear();
                System.out.println("WWWWWWWWWWW:  " + productos.size());


                for (ProductoPedido ped : productos2) {
                    System.out.println("ggggggggggggg");
                    if (!ped.isComplete) {
                        System.out.println("kkkkkkkkkkkkkkkk");
                        productos.add(ped);
                    }
                }

                System.out.println("ttttttttttttttttttttt:  " + productos.size());

                LOGGER.info("Renderizando productos (1ª hora) del pedido: " + p.getCodigo_referencia());
                renderizarProductos(productos, grid_en_curso_primera_hora);

                cargarPaletsSalidaParaPedido(p);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error cargando productos del pedido (1ª hora) " + codigo, e);
            }
        });

        combo_pedido_segunda_hora.setOnAction(_ -> {
            String codigo = combo_pedido_segunda_hora.getValue();
            if (codigo == null || codigo.isBlank()) return;

            try {
                Pedido p = getPedidoPorCodigo(Main.connection, codigo);
                if (p == null) {
                    LOGGER.warning("No se encontró pedido con código: " + codigo);
                    actualizarDatosCliente(null);
                    return;
                }

                // 🔹 Actualizar datos del cliente
                actualizarDatosCliente(p);

                List<ProductoPedido> productos2 = getProductosPorCodigoReferencia(
                        Main.connection,
                        p.getCodigo_referencia()
                );

                List<ProductoPedido> productos = getProductosPorCodigoReferencia(
                        Main.connection,
                        p.getCodigo_referencia()
                );
                productos.clear();
                System.out.println("WWWWWWWWWWW:  " + productos.size());


                for (ProductoPedido ped : productos2) {
                    System.out.println("ggggggggggggg");
                    if (!ped.isComplete) {
                        System.out.println("kkkkkkkkkkkkkkkk");
                        productos.add(ped);
                    }
                }


                LOGGER.info("Renderizando productos (2ª hora) del pedido: " + p.getCodigo_referencia());
                renderizarProductos(productos, grid_en_curso_segunda_hora);
                cargarPaletsSalidaParaPedido(p);


            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error cargando productos del pedido (2ª hora) " + codigo, e);
            }
        });
    }

    public void scrollTopSmooth(ScrollPane scroll) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(scroll.vvalueProperty(), 0, Interpolator.EASE_BOTH)
                )
        );
        timeline.play();
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    private void actualizarDatosCliente(Pedido pedido) {
        if (pedido == null) {
            cliente_label.setText("-");
            email_label.setText("-");
            telefono_label.setText("-");
            return;
        }

        idCliente = pedido.getId_cliente(); // ajusta al nombre real del getter


        Cliente cliente = getClienteById(Main.connection, idCliente);

        System.out.println(cliente);

        if (cliente == null) {
            LOGGER.warning("No se encontró cliente para id_cliente=" + idCliente);
            cliente_label.setText("-");
            email_label.setText("-");
            telefono_label.setText("-");
            return;
        }

        cliente_label.setText(
                cliente.getNombre() != null ? cliente.getNombre() : "-"
        );
        email_label.setText(
                cliente.getEmail() != null ? cliente.getEmail() : "-"
        );
        telefono_label.setText(
                cliente.getTelefono() != null ? cliente.getTelefono() : "-"
        );

        LOGGER.fine(() -> String.format(
                "Datos cliente cargados -> nombre=%s, email=%s, telefono=%s",
                cliente_label.getText(), email_label.getText(), telefono_label.getText()
        ));
    }

    private void updateInfoPalet() {

        int totalCantidad = 0;
        Set<String> productosUnicos = new HashSet<>();

        for (Node node : grid_productos_en_palet.getChildren()) {
            if (node instanceof AnchorPane anchor) {

                ItemPaletizarController ctrl =
                        (ItemPaletizarController) anchor.getProperties().get("controller");

                if (ctrl != null) {
                    try {
                        int cantidad = Integer.parseInt(ctrl.getCantidad());
                        totalCantidad += cantidad;

                        productosUnicos.add(ctrl.getProductoId());

                    } catch (Exception e) {
                        LOGGER.warning("No se pudo leer cantidad/producto de un item en el palet.");
                    }
                }
            }
        }

        cantidad_total_palet.setText(String.valueOf(totalCantidad));
        productos_palet_label.setText(String.valueOf(productosUnicos.size()));
    }

    private String generarSSCC() {
        StringBuilder sb = new StringBuilder(18);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 18; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }

    private void crearPaletSalida() {
        try {
            // 1) Determinar qué pedido está activo (1ª hora o 2ª)
            String codigoPedido = combo_pedido_primera_hora.getValue();
            if (codigoPedido == null || codigoPedido.isBlank()) {
                codigoPedido = combo_pedido_segunda_hora.getValue();
            }

            if (codigoPedido == null || codigoPedido.isBlank()) {
                LOGGER.warning("No hay pedido seleccionado para crear el palet de salida.");
                return;
            }

            Integer idPedido = getIdPedidoPorCodigo(Main.connection, codigoPedido);
            if (idPedido == null) {
                LOGGER.severe("No se pudo obtener id_pedido para el código: " + codigoPedido);
                return;
            }

            // 2) Construir la lista de líneas del palet a partir del grid_productos_en_palet
            List<LineaPaletSalida> lineas = new ArrayList<>();

            for (Node node : grid_productos_en_palet.getChildren()) {
                if (node instanceof AnchorPane anchor) {
                    Object ctrlObj = anchor.getProperties().get("controller");
                    if (ctrlObj instanceof ItemPaletizarController ctrl) {
                        try {
                            int cajas = Integer.parseInt(ctrl.getCantidad());
                            int idProducto = ctrl.getIdProductoBDD();      // id_producto (INT, FK a productos)
                            int idDetalle = ctrl.getIdDetalleBDD();        // id_detalle en detalles_pedido

                            lineas.add(new LineaPaletSalida(idProducto, cajas, idDetalle));

                            DetallesPedidoDAO.setDetallePaletizado(Main.connection, idDetalle);
                        } catch (NumberFormatException e) {
                            LOGGER.warning("Cantidad inválida en item paletizado, se omite.");
                        }
                    }
                }
            }

            if (lineas.isEmpty()) {
                LOGGER.warning("No hay productos en el grid de palet para crear el palet_salida.");
                return;
            }

            // 3) Generar SSCC (ajusta a tu lógica real / empresa)
            //   Ejemplo usando tu GS1Utils si lo tienes:

            System.out.println(COMPANY_GS1_CODE);
            String sscc = GS1Utils.generateSSCC(3, COMPANY_GS1_CODE, System.currentTimeMillis() % 100000000L);
            //String sscc = System.currentTimeMillis();  // placeholder simple

            // 4) Llamar al DAO para crear palet_salida + detalles + marcar paletizado
            int idPaletSalida = PaletSalidaDAO.crearPaletSalidaConDetalles(
                    Main.connection,
                    sscc,
                    idPedido,
                    lineas
            );

            String finalCodigoPedido = codigoPedido;
            LOGGER.info(() -> "Palet de salida creado correctamente con id=" + idPaletSalida +
                    " para pedido " + finalCodigoPedido);

            // Aquí puedes:
            // - Limpiar grid_productos_en_palet
            // - Actualizar labels de cantidad_total_palet / productos_palet_label
            grid_productos_en_palet.getChildren().clear();
            cantidad_total_palet.setText("0");
            productos_palet_label.setText("0");

            boolean isComplete = isPedidoCompletamentePaletizado(Main.connection, idPedido);
            System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFF isComplete: " + isComplete);
            if (isComplete) {
                marcarPedidoCompletadoSinUsuarioPorCodigo(Main.connection, codigoPedido);

                Platform.runLater(() -> {
                    String usernameActual = combo_usuario.getValue();
                    if (usernameActual != null && !usernameActual.isBlank()) {
                        cargarDatosUsuarioDesdeCache(usernameActual);
                    }


                });
            }

            String ref = combo_pedido_primera_hora.getValue();
            String ref2 = combo_pedido_segunda_hora.getValue();
            Pedido pActual = getPedidoPorCodigo(Main.connection, ref);

            if (pActual == null) {
                pActual = getPedidoPorCodigo(Main.connection, ref2);
            }

            if (pActual != null) {
                cargarPaletsSalidaParaPedido(pActual);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear el palet de salida desde la UI", e);
            e.printStackTrace();
        }
    }

    private void cargarPaletsSalidaParaPedido(Pedido pedido) {
        if (pedido == null) {
            return;
        }

        // Limpiar grid y contador
        grid_palets_Listos.getChildren().clear();
        grid_palets_Listos.getColumnConstraints().clear();
        grid_palets_Listos.getRowConstraints().clear();
        paletsListosCount = 0;

        int idPedido = pedido.getId_pedido();
        LOGGER.info(() -> "Cargando palets_salida para pedido id=" + idPedido
                + " (" + pedido.getCodigo_referencia() + ")");

        List<PaletSalidaDAO.PaletSalidaResumen> paletsSalida =
                PaletSalidaDAO.getPaletsSalidaPorPedido(Main.connection, idPedido);

        if (paletsSalida.isEmpty()) {
            LOGGER.fine("Este pedido no tiene palets_salida aún.");
            return;
        }

        for (PaletSalidaDAO.PaletSalidaResumen ps : paletsSalida) {
            agregarPaletListoAlGrid(ps);
        }
    }

    private void limpiarGridsPedidosOrigen() {
        // Limpia los productos del pedido en curso (1ª y 2ª hora)
        limpiarGridPane(grid_en_curso_primera_hora);
        limpiarGridPane(grid_en_curso_segunda_hora);

        // Opcional: también vaciar el grid de productos en palet y palets listos
        // limpiarGridPane(grid_productos_en_palet);
        // limpiarGridPane(grid_palets_Listos);
        // paletsListosCount = 0;   // si usas este contador
    }

    public void refrescarGridDeOrigenTrasSplit(ItemPaletizarController itemCtrl) {
        // 1) Localizar de qué grid viene este item
        GridPane origen = localizarGridDeItem(itemCtrl);
        if (origen == null) {
            LOGGER.warning("No se encontró grid de origen para el item tras split.");
            return;
        }

        // 2) Averiguar qué pedido está seleccionado según el grid
        String codigo = null;
        if (origen == grid_en_curso_primera_hora) {
            codigo = combo_pedido_primera_hora.getValue();
        } else if (origen == grid_en_curso_segunda_hora) {
            codigo = combo_pedido_segunda_hora.getValue();
        }

        if (codigo == null || codigo.isBlank()) {
            LOGGER.warning("No hay pedido seleccionado para refrescar el grid de origen.");
            return;
        }

        try {
            Pedido p = getPedidoPorCodigo(Main.connection, codigo);
            if (p == null) {
                LOGGER.warning("Pedido no encontrado al refrescar grid: " + codigo);
                return;
            }

            List<ProductoPedido> productos = getProductosPorCodigoReferencia(
                    Main.connection,
                    p.getCodigo_referencia()
            );

            LOGGER.info("Refrescando grid de origen tras split. Pedido: " + p.getCodigo_referencia());
            renderizarProductos(productos, origen);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error refrescando grid tras split", e);
        }
    }

    private GridPane localizarGridDeItem(ItemPaletizarController ctrl) {
        // Buscar en grid de 1ª hora
        for (var node : grid_en_curso_primera_hora.getChildren()) {
            Object ud = node.getUserData();
            if (ud == ctrl) {
                return grid_en_curso_primera_hora;
            }
        }

        // Buscar en grid de 2ª hora
        for (var node : grid_en_curso_segunda_hora.getChildren()) {
            Object ud = node.getUserData();
            if (ud == ctrl) {
                return grid_en_curso_segunda_hora;
            }
        }

        return null;
    }
}
