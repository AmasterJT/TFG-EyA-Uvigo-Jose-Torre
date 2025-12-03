package uvigo.tfgalmacen.mobile_android;

public class LoginRequest {
    private String userName;
        private String password;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public static class LoginResponse {
        private boolean ok;
        private String token;
        private String message;
        private String nombre;

        private String apellido1;
        private int id_usuario;

        public boolean isOk() {
            return ok;
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


        public int getId_usuario() {
            return id_usuario;
        }

        public void setId_usuario(int id_usuario) {
            this.id_usuario = id_usuario;
        }

        public String getApellido1() {
            return apellido1;
        }

        public void setApellido1(String apellido1) {
            this.apellido1 = apellido1;
        }
    }
}
