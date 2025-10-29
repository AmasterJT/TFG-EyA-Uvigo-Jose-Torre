package uvigo.tfgalmacen;

import uvigo.tfgalmacen.almacenManagement.Palet;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrdenCompra {

    private static final Logger LOGGER = Logger.getLogger(OrdenCompra.class.getName());

    private final ArrayList<Palet> lista_palets_oc;
    private final ArrayList<Proveedor> lista_proveedores_oc;

    public OrdenCompra(ArrayList<Palet> lista_palets_oc, ArrayList<Proveedor> lista_proveedores_oc) {
        this.lista_palets_oc = lista_palets_oc;
        this.lista_proveedores_oc = lista_proveedores_oc;
    }

    // Getters/Setters si los necesitas…
    public ArrayList<Palet> getLista_palets_oc() {
        return lista_palets_oc;
    }

    public ArrayList<Proveedor> getLista_proveedores_oc() {
        return lista_proveedores_oc;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("OrdenCompra{\n");
        for (int i = 0; i < lista_palets_oc.size(); i++) {
            out.append("\t").append(lista_proveedores_oc.get(i)).append("\n\t\t ").append(lista_palets_oc.get(i)).append("\n");
        }
        out.append("}");
        return out.toString();
    }

}
