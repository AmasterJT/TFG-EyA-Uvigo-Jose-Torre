package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.SHAKE_DURATION;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.shake;

public class windowAjustesEditarUsuariosController {

    private static final Logger LOGGER = Logger.getLogger(windowAjustesEditarUsuariosController.class.getName());

    @FXML
    private Button ExitButton;
    @FXML
    private Button refreshUsers_btn; // nuevo


    @FXML
    private TextField nombreEdit_text;
    @FXML
    private TextField apellido1Edit_text;
    @FXML
    private TextField apellido2Edit_text;
    @FXML
    private TextField emailEdit_text;

    @FXML
    private Button guardar_cambios_btn1;

    @FXML
    private ComboBox<String> rolesEdit_comboBox;
    @FXML
    private ComboBox<String> usernameEdit_comboBox;

    @FXML
    private HBox windowBar;

    // Caché local de usuarios
    private final Map<String, User> cacheUsuarios = new LinkedHashMap<>();

    // Placeholders
    private static final String PLACEHOLDER_USUARIO = "Selecciona un usuario";
    private static final String PLACEHOLDER_ROL = "Selecciona un rol";

    @FXML
    public void initialize() {
        // Configurar ComboBoxes con placeholders
        usernameEdit_comboBox.setPromptText(PLACEHOLDER_USUARIO);
        rolesEdit_comboBox.setPromptText(PLACEHOLDER_ROL);

        // Email no editable
        emailEdit_text.setEditable(false);

        // Cargar datos
        cargarRoles();
        cargarUsernamesYCache();

        // Vaciar campos iniciales
        limpiarCampos();

        // Listener: al cambiar usuario seleccionado, rellenar campos
        usernameEdit_comboBox.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null && !nv.equals(PLACEHOLDER_USUARIO)) cargarDatosUsuarioDesdeCache(nv);
        });

        // Botón guardar
        guardar_cambios_btn1.setOnAction(_ -> onGuardarCambios());

        // Botón cerrar
        ExitButton.setOnMouseClicked(_ -> cerrarVentana());

        refreshUsers_btn.setOnMouseClicked(_ -> onRefrescarClick());
    }

    // ---------------- Carga inicial ----------------

    private void cargarRoles() {
        try {
            List<String> roles = RolePermissionDAO.getAllRoleNames(Main.connection);
            rolesEdit_comboBox.getItems().setAll(roles);
            rolesEdit_comboBox.getSelectionModel().clearSelection();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando roles", e);
            alertError("Error", "No fue posible cargar la lista de roles.");
        }
    }

    private void cargarUsernamesYCache() {
        try {
            List<User> users = UsuarioDAO.getAllUsers(Main.connection);
            cacheUsuarios.clear();

            for (User u : users) {
                cacheUsuarios.put(u.getUsername(), u);
            }

            // Agregar placeholder
            List<String> usernames = new ArrayList<>(cacheUsuarios.keySet());
            usernames.sort(String::compareToIgnoreCase);

            usernameEdit_comboBox.getItems().setAll(usernames);
            usernameEdit_comboBox.getSelectionModel().clearSelection();

            // Mostrar bonito en desplegable
            usernameEdit_comboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(String username) {
                    if (username == null || username.equals(PLACEHOLDER_USUARIO)) return "";
                    User u = cacheUsuarios.get(username);
                    if (u == null) return username;
                    String fullname = (nvl(u.getName()) + " " + nvl(u.getApellido1()) + " " + nvl(u.getApellido2())).trim();
                    return username;
                }

                @Override
                public String fromString(String s) {
                    return s;
                }
            });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando usuarios", e);
            alertError("Error", "No fue posible cargar la lista de usuarios.");
        }
    }

    // ---------------- Cargar datos al seleccionar ----------------

    private void cargarDatosUsuarioDesdeCache(String username) {
        if (username == null || username.isBlank()) {
            limpiarCampos();
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
            limpiarCampos();
            alertWarn("Aviso", "No se encontró el usuario seleccionado.");
            return;
        }

        // Actualizar los campos
        nombreEdit_text.setText(nvl(u.getName()));
        apellido1Edit_text.setText(nvl(u.getApellido1()));
        apellido2Edit_text.setText(nvl(u.getApellido2()));
        emailEdit_text.setText(nvl(u.getEmail()));

        // Actualizar el rol
        try {
            String rolNombre = RolePermissionDAO.getRoleNameById(Main.connection, u.getIdRol());
            if (rolNombre != null) {
                rolesEdit_comboBox.getSelectionModel().select(rolNombre);
            } else {
                rolesEdit_comboBox.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo obtener el rol del usuario " + username, e);
            rolesEdit_comboBox.getSelectionModel().clearSelection();
        }
    }

    // ---------------- Guardar cambios ----------------

    private void onGuardarCambios() {
        boolean ok = true;

        if (isBlank(usernameEdit_comboBox.getValue())) {
            ok = false;
            shake(usernameEdit_comboBox, SHAKE_DURATION);
        }
        if (isBlank(nombreEdit_text.getText())) {
            ok = false;
            shake(nombreEdit_text, SHAKE_DURATION);
        }
        if (isBlank(apellido1Edit_text.getText())) {
            ok = false;
            shake(apellido1Edit_text, SHAKE_DURATION);
        }
        if (isBlank(apellido2Edit_text.getText())) {
            ok = false;
            shake(apellido2Edit_text, SHAKE_DURATION);
        }
        if (isBlank(emailEdit_text.getText()) || !emailValido(emailEdit_text.getText())) {
            ok = false;
            shake(emailEdit_text, SHAKE_DURATION);
        }
        if (isBlank(rolesEdit_comboBox.getValue())) {
            ok = false;
            shake(rolesEdit_comboBox, SHAKE_DURATION);
        }

        if (!ok) {
            alertWarn("Campos requeridos", "Revisa los campos marcados.");
            return;
        }

        String username = usernameEdit_comboBox.getValue();
        String nombre = nombreEdit_text.getText().trim();
        String ap1 = apellido1Edit_text.getText().trim();
        String ap2 = apellido2Edit_text.getText().trim();
        String email = emailEdit_text.getText().trim();
        String rolName = rolesEdit_comboBox.getValue();

        try {
            int idRol = RolePermissionDAO.getRoleIdByName(Main.connection, rolName);
            if (idRol <= 0) {
                shake(rolesEdit_comboBox, SHAKE_DURATION);
                alertWarn("Rol no válido", "Selecciona un rol válido.");
                return;
            }

            boolean updated = UsuarioDAO.updateUserProfile(Main.connection, username, nombre, ap1, ap2, email, idRol);

            if (updated) {
                User cached = cacheUsuarios.get(username);
                if (cached != null) {
                    cached.setName(nombre);
                    cached.setApellido1(ap1);
                    cached.setApellido2(ap2);
                    cached.setEmail(email);
                    cached.setIdRol(idRol);
                }
                alertInfo("Guardado", "Los cambios se han guardado correctamente.");
            } else {
                alertWarn("Sin cambios", "No se aplicaron cambios (verifica la información).");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error guardando cambios del usuario", e);
            alertError("Error", "No fue posible guardar los cambios.");
        }
    }

    private void onRefrescarClick() {
        // Guarda la selección actual (si existe)
        String seleccionado = usernameEdit_comboBox.getValue();

        // Recarga usuarios y roles
        cargarUsernamesYCache();   // ya la tienes
        // Si quieres refrescar roles desde BD cada vez, descomenta:
        // cargarRoles();

        // Vuelve a seleccionar el usuario si sigue existiendo, si no, limpia
        if (seleccionado != null && cacheUsuarios.containsKey(seleccionado)) {
            usernameEdit_comboBox.getSelectionModel().select(seleccionado);
            cargarDatosUsuarioDesdeCache(seleccionado);
        } else {
            // Usuario ya no existe: limpiar UI
            usernameEdit_comboBox.getSelectionModel().clearSelection();
            limpiarCampos();
        }
    }

    // ---------------- Utilidades ----------------

    private void limpiarCampos() {
        nombreEdit_text.clear();
        apellido1Edit_text.clear();
        apellido2Edit_text.clear();
        emailEdit_text.clear();
        rolesEdit_comboBox.getSelectionModel().clearSelection();
        usernameEdit_comboBox.getSelectionModel().clearSelection();
    }

    private void cerrarVentana() {
        Stage s = (Stage) ExitButton.getScene().getWindow();
        s.close();
    }

    private void alertWarn(String t, String c) {
        Alert a = new Alert(Alert.AlertType.WARNING, c, ButtonType.OK);
        a.setTitle(t);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertError(String t, String c) {
        Alert a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK);
        a.setTitle(t);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertInfo(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK);
        a.setTitle(t);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private boolean emailValido(String e) {
        return e != null && e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
