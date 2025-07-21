package uvigo.tfgalmacen;


import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

import java.sql.Connection;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class User {
    public int id_usuario;
    public String username;
    public String name;
    public String apellido;
    public String email;
    public String role;
    public int idRol;

    public User(String name, String password, Connection conection) {
        this.name = name;
        this.idRol = UsuarioDAO.getUserRole(conection, name, password);
        this.role = RolePermissionDAO.getRoleNameById(conection, idRol);
    }

    public User(int id_usuario, String username, String nombre, String apellido, String email, Connection conection) {
        this.id_usuario = id_usuario;
        this.username = username;
        this.name = nombre;
        this.apellido = apellido;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getApellido(){
        return this.apellido;
    }

    @Override
    public String toString() {
        return CYAN + "Nombre: " + RESET + name + CYAN + ", Rol: " + RESET + role;
    }
}
