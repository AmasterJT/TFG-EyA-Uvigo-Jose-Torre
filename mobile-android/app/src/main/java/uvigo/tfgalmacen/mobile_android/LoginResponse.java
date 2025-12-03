package uvigo.tfgalmacen.mobile_android;

public class LoginResponse {
    private boolean ok;
    private String token;
    private String message;
    private String nombre;

    private String apellido1;

    private int id;

    public boolean isOk() {
        return !ok;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getNombre() {
        return nombre;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
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
