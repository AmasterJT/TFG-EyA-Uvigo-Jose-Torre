package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

public class windowAjustesCrearUsuariosController {

    private static final Logger LOGGER = Logger.getLogger(windowAjustesCrearUsuariosController.class.getName());

    @FXML
    private Button ExitButton;

    @FXML
    private TextField apellido1_text;
    @FXML
    private TextField apellido2_text;
    @FXML
    private Button cambiar_password_btn; // (si quieres, puedes usarlo para mostrar/ocultar las PasswordField)
    @FXML
    private PasswordField confirm_crear_contrasena_text;
    @FXML
    private PasswordField crear_contrasen_text;
    @FXML
    private TextField email_text;
    @FXML
    private Button guardar_cambios_btn;
    @FXML
    private TextField nombre_text;
    @FXML
    private ComboBox<String> roles_comboBox;   // <- tipo correcto
    @FXML
    private TextField username_text;
    @FXML
    private HBox windowBar;

    @FXML
    public void initialize() {
        // Cargar roles
        cargarRoles();

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
        attachClearErrorOnTyping(crear_contrasen_text, confirm_crear_contrasena_text);
    }

    // ---------------------------------------------------------------------
    // Acciones
    // ---------------------------------------------------------------------

    private void onGuardarUsuario() {
        // Validar
        if (!validarFormulario()) return;

        String rolNombre = roles_comboBox.getSelectionModel().getSelectedItem();
        int idRol = RolePermissionDAO.getRoleIdByName(Main.connection, rolNombre);

        // Construir objeto User SIN contraseña
        User nuevo = new User(username_text.getText(),
                nombre_text.getText(),
                apellido1_text.getText(),
                apellido2_text.getText(),
                email_text.getText(),
                idRol);


        if (idRol <= 0) {
            showWarn("Rol no válido", "Selecciona un rol válido.");
            markError(roles_comboBox);
            return;
        }
        nuevo.setIdRol(idRol);

        // Password en variable (NO meterla en el objeto User)
        String rawPassword = crear_contrasen_text.getText();

        try {
            // Ajusta el nombre del método según tu DAO real.

            System.out.println(nuevo);
            System.out.println(rawPassword);
            System.out.println(idRol);
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
        clearErrors(crear_contrasen_text, confirm_crear_contrasena_text);
        clearErrors(roles_comboBox);

        if (isBlank(username_text)) {
            ok = false;
            markError(username_text);
        }
        if (isBlank(nombre_text)) {
            ok = false;
            markError(nombre_text);
        }
        if (isBlank(apellido1_text)) {
            ok = false;
            markError(apellido1_text);
        }
        if (isBlank(apellido2_text)) {
            ok = false;
            markError(apellido2_text);
        }
        if (isBlank(email_text) || !emailValido(s(email_text))) {
            ok = false;
            markError(email_text);
        }

        String pass1 = crear_contrasen_text.getText();
        String pass2 = confirm_crear_contrasena_text.getText();
        if (pass1 == null || pass1.isBlank() || pass2 == null || pass2.isBlank()) {
            ok = false;
            markError(crear_contrasen_text);
            markError(confirm_crear_contrasena_text);
        } else if (!Objects.equals(pass1, pass2)) {
            ok = false;
            markError(crear_contrasen_text);
            markError(confirm_crear_contrasena_text);
            showWarn("Contraseñas", "Las contraseñas no coinciden.");
        }

        String rol = roles_comboBox.getSelectionModel().getSelectedItem();
        if (rol == null || rol.isBlank()) {
            ok = false;
            markError(roles_comboBox);
        }

        if (!ok) showWarn("Campos requeridos", "Revisa los campos marcados en rojo.");
        return ok;
    }

    private boolean emailValido(String email) {
        // Validación simple; ajusta si necesitas algo más estricto
        return email.contains("@") && email.contains(".");
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
        crear_contrasen_text.clear();
        confirm_crear_contrasena_text.clear();

        clearErrors(username_text, nombre_text, apellido1_text, apellido2_text, email_text);
        clearErrors(crear_contrasen_text, confirm_crear_contrasena_text);
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
            c.getStyleClass().remove("textfield-error");
            c.getStyleClass().remove("label-error");
        }
    }

    private void markError(Control c) {
        if (c == null) return;
        if (!c.getStyleClass().contains("textfield-error")) {
            c.getStyleClass().add("textfield-error");
        }
    }

    private boolean isBlank(TextInputControl t) {
        return t == null || t.getText() == null || t.getText().isBlank();
    }

    private String s(TextInputControl t) {
        return t == null ? "" : t.getText().trim();
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
