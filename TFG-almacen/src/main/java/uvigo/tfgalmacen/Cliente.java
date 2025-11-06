package uvigo.tfgalmacen;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Representa un cliente en el sistema.
 * Corresponde a la tabla 'clientes' en la base de datos.
 */
public class Cliente {

    private int id_cliente;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Timestamp fecha_registro;
    private Timestamp ultima_actualizacion;

    // =======================
    // Constructores
    // =======================

    public Cliente() {
    }

    public Cliente(int id_cliente, String nombre, String direccion, String telefono,
                   String email, Timestamp fecha_registro, Timestamp ultima_actualizacion) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.fecha_registro = fecha_registro;
        this.ultima_actualizacion = ultima_actualizacion;
    }

    // =======================
    // Getters y Setters
    // =======================

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
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

    public Timestamp getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Timestamp fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public Timestamp getUltima_actualizacion() {
        return ultima_actualizacion;
    }

    public void setUltima_actualizacion(Timestamp ultima_actualizacion) {
        this.ultima_actualizacion = ultima_actualizacion;
    }

    // =======================
    // Métodos utilitarios
    // =======================

    @Override
    public String toString() {
        return "Cliente{" +
                "id_cliente=" + id_cliente +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", fecha_registro=" + fecha_registro +
                ", ultima_actualizacion=" + ultima_actualizacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return id_cliente == cliente.id_cliente && Objects.equals(email, cliente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_cliente, email);
    }
}
