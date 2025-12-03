package uvigo.tfgalmacen.almacenapi.dto;

public class UserIdResponse {
    private Integer idUsuario;

    public UserIdResponse(Integer id) {
        this.idUsuario = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}