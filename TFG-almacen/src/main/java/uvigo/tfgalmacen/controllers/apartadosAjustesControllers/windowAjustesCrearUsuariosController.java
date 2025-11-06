package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.text.Normalizer;
import java.util.List;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.SHAKE_DURATION;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.shake;

public class windowAjustesCrearUsuariosController {

    private static final Logger LOGGER = Logger.getLogger(windowAjustesCrearUsuariosController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }


    private static final String PF_NORMAL = "create-user-password-field-normal";
    private static final String PF_ERROR = "create-user-password-field-error";

    private static final String EMAIL_DOMAIN = "almacen.com";

    @FXML
    private Button ExitButton;
    @FXML
    private TextField apellido1_text;
    @FXML
    private TextField apellido2_text;
    @FXML
    private Button cambiar_password_btn;
    @FXML
    private PasswordField confirm_crear_contrasena_text;
    @FXML
    private PasswordField crear_contrasena_text;
    @FXML
    private TextField email_text;
    @FXML
    private Button guardar_cambios_btn;
    @FXML
    private TextField nombre_text;
    @FXML
    private ComboBox<String> roles_comboBox;
    @FXML
    private TextField username_text;
    @FXML
    private HBox windowBar;

    @FXML
    public void initialize() {
        // Cargar roles
        cargarRoles();

        // Email solo lectura (lo generamos nosotros)
        if (email_text != null) {
            email_text.setEditable(false);
            email_text.setFocusTraversable(false);
        }

        // Generación automática del email cuando cambien nombre o primer apellido
        if (nombre_text != null) {
            nombre_text.textProperty().addListener((obs, ov, nv) -> generarEmailSiProcede());
        }
        if (apellido1_text != null) {
            apellido1_text.textProperty().addListener((obs, ov, nv) -> generarEmailSiProcede());
        }

        // Botón cerrar ventana
        if (ExitButton != null) {
            ExitButton.setOnMouseClicked(_ -> cerrarVentana());
        }

        // Guardar usuario
        if (guardar_cambios_btn != null) {
            guardar_cambios_btn.setOnAction(_ -> onGuardarUsuario());
        }

        // Limpia estilos de error al escribir
        attachClearErrorOnTyping(username_text, nombre_text, apellido1_text, apellido2_text, email_text);
        attachClearErrorOnTyping(crear_contrasena_text, confirm_crear_contrasena_text);

        crear_contrasena_text.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) setErrorState(crear_contrasena_text, false);
        });
        crear_contrasena_text.setOnMouseClicked(_ -> setErrorState(crear_contrasena_text, false));

        confirm_crear_contrasena_text.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) setErrorState(confirm_crear_contrasena_text, false);
        });
        confirm_crear_contrasena_text.setOnMouseClicked(_ -> setErrorState(confirm_crear_contrasena_text, false));

        // ENTER en username/password dispara login con micro-hover en botón
        EventHandler<KeyEvent> onEnterPressed = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                guardar_cambios_btn.getStyleClass().add("hover-simulated");
                guardar_cambios_btn.requestFocus();
                guardar_cambios_btn.fire();

                PauseTransition delay = new PauseTransition(Duration.millis(150));
                delay.setOnFinished(e -> guardar_cambios_btn.getStyleClass().remove("hover-simulated"));
                delay.play();
            }
        };
        username_text.setOnKeyPressed(onEnterPressed);
        nombre_text.setOnKeyPressed(onEnterPressed);
        apellido1_text.setOnKeyPressed(onEnterPressed);
        apellido2_text.setOnKeyPressed(onEnterPressed);
        crear_contrasena_text.setOnKeyPressed(onEnterPressed);
        confirm_crear_contrasena_text.setOnKeyPressed(onEnterPressed);

        limpiarFormulario();
    }

    // ---------------------------------------------------------------------
    // Generación automática de email
    // ---------------------------------------------------------------------
    private void generarEmailSiProcede() {
        String nombre = safeText(nombre_text);
        String apellido1 = safeText(apellido1_text);

        if (!nombre.isBlank() && !apellido1.isBlank()) {
            String primerNombre = extraerPrimerNombre(nombre);
            String userPart = normalizarAlfanumerico(primerNombre + apellido1).toLowerCase();
            if (!userPart.isBlank()) {
                String email = userPart + "@" + EMAIL_DOMAIN;
                email_text.setText(email);
                // Si había error marcado por estar vacío, lo limpiamos
                clearErrors(email_text);
            }
        } else {
            // Si falta alguno, limpiamos el email (opcional)
            email_text.clear();
        }
    }

    private String extraerPrimerNombre(String nombreCompleto) {
        String[] partes = nombreCompleto.trim().split("\\s+");
        return partes.length > 0 ? partes[0] : nombreCompleto.trim();
    }

    private String normalizarAlfanumerico(String s) {
        // Quitar acentos y caracteres no ASCII, y dejar solo [a-zA-Z0-9]
        String sinAcentos = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinAcentos.replaceAll("[^A-Za-z0-9]", "");
    }

    private String safeText(TextInputControl t) {
        return (t == null || t.getText() == null) ? "" : t.getText().trim();
    }

    // ---------------------------------------------------------------------
    // Acciones
    // ---------------------------------------------------------------------
    private void onGuardarUsuario() {
        // Validar
        if (!validarFormulario()) return;
        if (UsuarioDAO.userExists(Main.connection, username_text.getText())) return;
        if (UsuarioDAO.emailExists(Main.connection, email_text.getText())) return;

        //¿que pasa con el email si dos usuaios de llaman igual?
        String rolNombre = roles_comboBox.getSelectionModel().getSelectedItem();
        int idRol = RolePermissionDAO.getRoleIdByName(Main.connection, rolNombre);

        // Construir objeto User SIN contraseña
        User nuevo = new User(
                username_text.getText(),
                nombre_text.getText(),
                apellido1_text.getText(),
                apellido2_text.getText(),
                email_text.getText(),
                idRol
        );

        if (idRol <= 0) {
            showWarn("Rol no válido", "Selecciona un rol válido.");
            setErrorState(roles_comboBox, true);
            return;
        }
        nuevo.setIdRol(idRol);

        // Password (NO en el objeto)
        String rawPassword = crear_contrasena_text.getText();

        try {
            boolean ok = UsuarioDAO.createUser(Main.connection, nuevo, rawPassword, idRol);

            if (ok) {
                showInfo("Usuario creado", "El usuario se ha creado correctamente.");
                limpiarFormulario();
            } else {
                showError("No se pudo crear el usuario", "Revisa la información e inténtalo de nuevo.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error insertando usuario", e);
            showError("Error inesperado", "No se pudo crear el usuario.\n" + e.getMessage());
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) ExitButton.getScene().getWindow();
        stage.close();
    }

    // ---------------------------------------------------------------------
    // Carga de datos
    // ---------------------------------------------------------------------
    private void cargarRoles() {
        try {
            List<String> roles = RolePermissionDAO.getAllRoleNames(Main.connection);
            roles_comboBox.getItems().setAll(roles);
            if (!roles.isEmpty()) roles_comboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando roles", e);
            showError("Error cargando roles", "No fue posible cargar la lista de roles.");
        }
    }

    // ---------------------------------------------------------------------
    // Validaciones
    // ---------------------------------------------------------------------
    private boolean validarFormulario() {
        boolean ok = true;
        clearErrors(username_text, nombre_text, apellido1_text, apellido2_text, email_text);
        clearErrors(crear_contrasena_text, confirm_crear_contrasena_text);
        clearErrors(roles_comboBox);

        if (isBlank(username_text)) {
            ok = false;
            setErrorState(username_text, true);
        }
        if (isBlank(nombre_text)) {
            ok = false;
            setErrorState(nombre_text, true);
        }
        if (isBlank(apellido1_text)) {
            ok = false;
            setErrorState(apellido1_text, true);
        }
        if (isBlank(apellido2_text)) {
            ok = false;
            setErrorState(apellido2_text, true);
        }

        // email debe estar generado
        if (isBlank(email_text) || !emailValido(email_text.getText())) {
            ok = false;
            setErrorState(email_text, true);
        }

        String pass1 = crear_contrasena_text.getText();
        String pass2 = confirm_crear_contrasena_text.getText();
        if (pass1 == null || pass1.isBlank() || pass2 == null || pass2.isBlank()) {
            ok = false;
            setErrorState(crear_contrasena_text, true);
            setErrorState(confirm_crear_contrasena_text, true);
        } else if (!Objects.equals(pass1, pass2)) {
            ok = false;
            setErrorState(crear_contrasena_text, true);
            setErrorState(confirm_crear_contrasena_text, true);
            showWarn("Contraseñas", "Las contraseñas no coinciden.");
        }

        String rol = roles_comboBox.getSelectionModel().getSelectedItem();
        if (rol == null || rol.isBlank()) {
            ok = false;
            setErrorState(roles_comboBox, true);
        }

        if (!ok) showWarn("Campos requeridos", "Revisa los campos marcados en rojo.");
        return ok;
    }

    private boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // ---------------------------------------------------------------------
    // Utilidades UI
    // ---------------------------------------------------------------------
    private void limpiarFormulario() {
        username_text.clear();
        nombre_text.clear();
        apellido1_text.clear();
        apellido2_text.clear();
        email_text.clear();
        crear_contrasena_text.clear();
        confirm_crear_contrasena_text.clear();

        clearErrors(username_text, nombre_text, apellido1_text, apellido2_text, email_text);
        clearErrors(crear_contrasena_text, confirm_crear_contrasena_text);
        clearErrors(roles_comboBox);

        if (!roles_comboBox.getItems().isEmpty()) {
            roles_comboBox.getSelectionModel().selectFirst();
        }
    }

    private void attachClearErrorOnTyping(TextInputControl... fields) {
        for (TextInputControl f : fields) {
            if (f == null) continue;
            f.textProperty().addListener((obs, ov, nv) -> clearErrors(f));
        }
    }

    private void clearErrors(Control... controls) {
        for (Control c : controls) {
            if (c == null) continue;
            c.getStyleClass().remove(PF_ERROR);
        }
    }

    private void setErrorState(Control c, boolean error) {
        if (c == null) return;
        c.getStyleClass().removeAll(PF_NORMAL, PF_ERROR);
        c.getStyleClass().add(error ? PF_ERROR : PF_NORMAL);
        if (error) shake(c, SHAKE_DURATION);
    }

    private boolean isBlank(TextInputControl t) {
        return t == null || t.getText() == null || t.getText().isBlank();
    }

    private void showWarn(String title, String content) {
        Alert a = new Alert(Alert.AlertType.WARNING, content, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showError(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
