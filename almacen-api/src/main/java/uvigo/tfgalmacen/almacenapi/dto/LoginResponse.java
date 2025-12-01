package uvigo.tfgalmacen.almacenapi.dto;

import uvigo.tfgalmacen.almacenapi.model.Usuario;

public class LoginResponse {

    private boolean success;
    private String message;
    private String userName;
    private String nombre;

    public static LoginResponse ok(Usuario u) {
        LoginResponse r = new LoginResponse();
        r.success = true;
        r.message = "Login correcto";
        r.userName = u.getUserName();
        r.nombre = u.getNombre();
        return r;
    }

    public static LoginResponse fail(String msg) {
        LoginResponse r = new LoginResponse();
        r.success = false;
        r.message = msg;
        return r;
    }

    // getters & setters


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
