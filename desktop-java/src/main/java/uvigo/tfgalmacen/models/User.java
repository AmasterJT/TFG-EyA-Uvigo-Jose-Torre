package uvigo.tfgalmacen.models;


import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

import java.sql.Connection;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class User {
    private int id_usuario;
    private String username;
    private String name;
    private String apellido1;
    private String apellido2;
    private String email;
    private String role;
    private int idRol;

    public User() {

    }

    public User(String name, String password, Connection conection) {
        this.name = name;
        this.idRol = UsuarioDAO.getUserRole(conection, name, password);
        this.role = RolePermissionDAO.getRoleNameById(conection, idRol);
    }

    public User(String username, String nombre, String apellido1, String apellido2, String email, int idRol) {
        this.username = username;
        this.name = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.idRol = idRol;
    }

    public User(int id_usuario, String username, String nombre, String apellido1, String apellido2, String email, int idRol) {
        this.id_usuario = id_usuario;
        this.username = username;
        this.name = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.idRol = idRol;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getFullName() {
        return name + " " + apellido1 + " " + apellido2;
    }

    @Override
    public String toString() {
        return CYAN + "Nombre: " + RESET + name + CYAN + ", Rol: " + RESET + role;
    }
}
