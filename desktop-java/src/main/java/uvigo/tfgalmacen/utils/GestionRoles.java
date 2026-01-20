package uvigo.tfgalmacen.utils;

import javafx.scene.control.Button;
import uvigo.tfgalmacen.models.User;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GestionRoles {

    private static final Logger LOGGER = Logger.getLogger(GestionRoles.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) h.setLevel(Level.ALL);
    }


    private GestionRoles() {
    }

    public enum Role {
        SYSADMIN("SysAdmin"),
        GESTOR_ALMACEN("Gestor Almacén"),
        OPERARIO("Operario"),
        ADMINISTRACION("Administración");

        public final String dbName;

        Role(String dbName) {
            this.dbName = dbName;
        }

        public static Role fromDb(String dbRole) {
            if (dbRole == null) return OPERARIO; // fallback seguro
            String r = dbRole.trim();
            for (Role role : values()) {
                if (role.dbName.equalsIgnoreCase(r)) return role;
            }
            return OPERARIO;
        }
    }

    /**
     * Agrupa todos los botones que quieres gestionar por rol.
     */
    public static final class Ui {
        // Navegación
        public Button almacenButton;
        public Button inventarioButton;
        public Button pedidosButton;
        public Button paletizarButton;
        public Button envioButton;
        public Button calendarioButton;

        // Acciones/ajustes
        public Button orden_compra_btn;
        public Button export_data_btn;

        public Button movimiento_btn;
        public Button actualizar_palet_btn;

        public Button ajustes_crear_usuario_btn;
        public Button ajustes_editar_usuario_btn;
        public Button ajustes_eliminar_usuario_btn;

        public Button ajustes_crear_pedido_btn;
        public Button ajustes_editar_pedido_btn;
        public Button ajustes_eliminar_pedido_btn;

        public Button ajustes_crear_producto_btn;
        public Button ajustes_crear_tipo_btn;

        // Siempre disponibles (opcional: inclúyelos si también quieres forzar su estado)
        public Button MenuButton;
        public Button cerrarSesionBtn;
        public Button minimizeButton;
        public Button ExitButton;
        public Button esconder_ajustes_btn;
    }

    /**
     * Aplica permisos (disable/enable) a la UI según el rol del usuario.
     */
    public static void aplicarPermisos(User currentUser, Ui ui) {
        Role role = Role.fromDb(currentUser != null ? currentUser.getRole() : null);

        // 1) Por defecto: deshabilita TODO lo sensible
        setDisabled(true,
                ui.almacenButton, ui.inventarioButton, ui.pedidosButton, ui.paletizarButton, ui.envioButton, ui.calendarioButton,
                ui.orden_compra_btn, ui.export_data_btn,
                ui.movimiento_btn, ui.actualizar_palet_btn,
                ui.ajustes_crear_usuario_btn, ui.ajustes_editar_usuario_btn, ui.ajustes_eliminar_usuario_btn,
                ui.ajustes_crear_pedido_btn, ui.ajustes_editar_pedido_btn, ui.ajustes_eliminar_pedido_btn,
                ui.ajustes_crear_producto_btn, ui.ajustes_crear_tipo_btn
        );

        // 2) Menú/sesión/salir siempre disponibles
        setDisabled(false, ui.MenuButton, ui.cerrarSesionBtn, ui.minimizeButton, ui.ExitButton, ui.esconder_ajustes_btn);

        // 3) Habilitar según rol
        switch (role) {
            case SYSADMIN -> {
                setDisabled(false,
                        ui.almacenButton, ui.inventarioButton, ui.pedidosButton, ui.paletizarButton, ui.envioButton, ui.calendarioButton,
                        ui.orden_compra_btn, ui.export_data_btn,
                        ui.movimiento_btn, ui.actualizar_palet_btn,
                        ui.ajustes_crear_usuario_btn, ui.ajustes_editar_usuario_btn, ui.ajustes_eliminar_usuario_btn,
                        ui.ajustes_crear_pedido_btn, ui.ajustes_editar_pedido_btn, ui.ajustes_eliminar_pedido_btn,
                        ui.ajustes_crear_producto_btn, ui.ajustes_crear_tipo_btn
                );
            }

            case GESTOR_ALMACEN -> {
                setDisabled(false,
                        ui.almacenButton, ui.inventarioButton, ui.pedidosButton, ui.paletizarButton, ui.envioButton, ui.calendarioButton,
                        ui.movimiento_btn, ui.actualizar_palet_btn,
                        ui.ajustes_crear_pedido_btn, ui.ajustes_editar_pedido_btn, ui.ajustes_eliminar_pedido_btn,
                        ui.ajustes_crear_producto_btn, ui.ajustes_crear_tipo_btn,
                        ui.orden_compra_btn
                );
                // Si también puede exportar, descomenta:
                // setDisabled(false, ui.export_data_btn);
            }

            case OPERARIO -> {
                setDisabled(false,
                        ui.almacenButton, ui.inventarioButton, ui.paletizarButton, ui.movimiento_btn, ui.envioButton, ui.calendarioButton
                );
                // Si quieres que pueda ver pedidos (solo consulta), descomenta:
                // setDisabled(false, ui.pedidosButton);
            }

            case ADMINISTRACION -> {
                setDisabled(false,
                        ui.pedidosButton, ui.calendarioButton,
                        ui.orden_compra_btn, ui.export_data_btn,
                        ui.ajustes_crear_pedido_btn, ui.ajustes_editar_pedido_btn, ui.ajustes_eliminar_pedido_btn,
                        ui.ajustes_crear_usuario_btn, ui.ajustes_editar_usuario_btn, ui.ajustes_eliminar_usuario_btn
                );
            }
        }

        LOGGER.info("Permisos aplicados para rol: " + role.dbName);
    }

    private static void setDisabled(boolean disabled, Button... buttons) {
        if (buttons == null) return;
        for (Button b : buttons) {
            if (b != null) b.setDisable(disabled);
        }
    }
}
