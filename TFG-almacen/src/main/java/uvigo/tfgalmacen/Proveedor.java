package uvigo.tfgalmacen;

import java.sql.Timestamp;

/**
 * Clase que representa un proveedor del sistema.
 * Cada instancia contiene los datos almacenados en la tabla 'proveedores'.
 */
public class Proveedor {

    // ============================
    // ATRIBUTOS (coinciden con la BD)
    // ============================

    private int idProveedor;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String nifCif;
    private String contacto;
    private Timestamp fechaRegistro;
    private Timestamp ultimaActualizacion;

    // ============================
    // CONSTRUCTORES
    // ============================

    /**
     * Constructor completo, ideal para crear el objeto a partir de la base de datos.
     */
    public Proveedor(int idProveedor, String nombre, String direccion, String telefono,
                     String email, String nifCif, String contacto,
                     Timestamp fechaRegistro, Timestamp ultimaActualizacion) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.nifCif = nifCif;
        this.contacto = contacto;
        this.fechaRegistro = fechaRegistro;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    /**
     * Constructor alternativo, útil cuando se crea un nuevo proveedor antes de insertar en la BD.
     */
    public Proveedor(String nombre, String direccion, String telefono, String email, String nifCif, String contacto) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.nifCif = nifCif;
        this.contacto = contacto;
    }

    // ============================
    // GETTERS Y SETTERS
    // ============================

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getNifCif() {
        return nifCif;
    }

    public void setNifCif(String nifCif) {
        this.nifCif = nifCif;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Timestamp getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Timestamp ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // ============================
    // MÉTODOS AUXILIARES
    // ============================

    @Override
    public String toString() {
        return "Proveedor {" +
                "ID=" + idProveedor +
                ", Nombre='" + nombre + '\'' +
                ", Dirección='" + direccion + '\'' +
                ", Teléfono='" + telefono + '\'' +
                ", Email='" + email + '\'' +
                ", NIF/CIF='" + nifCif + '\'' +
                ", Contacto='" + contacto + '\'' +
                ", Fecha registro=" + fechaRegistro +
                ", Última actualización=" + ultimaActualizacion +
                '}';
    }
}