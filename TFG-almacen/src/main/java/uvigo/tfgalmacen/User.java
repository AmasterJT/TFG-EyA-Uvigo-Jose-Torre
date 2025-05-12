package uvigo.tfgalmacen;


import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

import java.sql.Connection;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class User {
    public String name;
    public String role;

    public User(String name, String password, Connection conection) {
        this.name = name;
        int idRol = UsuarioDAO.getUserRole(conection, name, password);
        this.role = RolePermissionDAO.getRoleNameById(conection, idRol);
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

    @Override
    public String toString() {
        return CYAN + "Nombre: " + RESET + name + CYAN + ", Rol: " + RESET + role;
    }
}
