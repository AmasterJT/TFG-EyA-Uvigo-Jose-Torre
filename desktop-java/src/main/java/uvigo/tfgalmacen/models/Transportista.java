package uvigo.tfgalmacen.models;

import java.time.LocalDateTime;

public class Transportista {
    private int idTransportista;
    private String nombreEmpresa;
    private String nombreConductor;
    private String telefono;
    private String email;
    private String matricula;
    private String tipoTransporte; // 'Camión', 'Furgoneta', etc.
    private String direccion;
    private String nifCif;
    private String notas;
    private LocalDateTime fechaRegistro;


    public Transportista(LocalDateTime fechaRegistro, String notas, String nifCif, String direccion, String tipoTransporte, String matricula, String email, String telefono, String nombreConductor, String nombreEmpresa, int idTransportista) {
        this.fechaRegistro = fechaRegistro;
        this.notas = notas;
        this.nifCif = nifCif;
        this.direccion = direccion;
        this.tipoTransporte = tipoTransporte;
        this.matricula = matricula;
        this.email = email;
        this.telefono = telefono;
        this.nombreConductor = nombreConductor;
        this.nombreEmpresa = nombreEmpresa;
        this.idTransportista = idTransportista;
    }


    public int getIdTransportista() {
        return idTransportista;
    }

    public void setIdTransportista(int idTransportista) {
        this.idTransportista = idTransportista;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipoTransporte() {
        return tipoTransporte;
    }

    public void setTipoTransporte(String tipoTransporte) {
        this.tipoTransporte = tipoTransporte;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNifCif() {
        return nifCif;
    }

    public void setNifCif(String nifCif) {
        this.nifCif = nifCif;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
