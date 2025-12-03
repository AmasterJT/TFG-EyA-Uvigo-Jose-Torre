package uvigo.tfgalmacen.almacenapi.dto;

import uvigo.tfgalmacen.almacenapi.model.Usuario;

public class LoginResponse {

    private boolean success;
    private String message;
    private String userName;
    private String nombre;
    private String apellido1;
    private int id;

    public static LoginResponse ok(Usuario u) {
        LoginResponse r = new LoginResponse();
        r.success = true;
        r.message = "Login correcto";
        r.userName = u.getUserName();
        r.nombre = u.getNombre();
        r.apellido1 = u.getApellido1();
        r.id = u.getId();
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

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
