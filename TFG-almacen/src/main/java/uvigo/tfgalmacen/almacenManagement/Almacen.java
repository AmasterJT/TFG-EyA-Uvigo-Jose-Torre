package uvigo.tfgalmacen.almacenManagement;

import uvigo.tfgalmacen.Cliente;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.controllers.loginController;
import uvigo.tfgalmacen.database.DatabaseConnection;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.*;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.ClientesDAO.getAllClientes;


/**
 * Clase que representa un almacén y gestiona la carga y manipulación de palets, productos y tipos.
 * Proporciona métodos para obtener datos desde la base de datos, relacionar información entre objetos,
 * y mostrar datos en consola.
 */
public class Almacen {

    private static final Logger LOGGER = Logger.getLogger(Almacen.class.getName());


    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }


    /**
     * Número de estanterías en el almacén.
     */
    public static int NUM_ESTANTERIAS = 4;

    /**
     * Número de baldas por estantería.
     */
    public static int NUM_BALDAS_PER_ESTANTERIA = 8;

    /**
     * Separación entre elementos del almacén (en metros).
     */
    public static double SEPARACION = 0.3;

    /**
     * Ancho de cada balda (en milímetros).
     */
    public static int ANCHO_BALDA = 3600;

    /**
     * Separación entre baldas (en milímetros).
     */
    public static int SEPARACION_ENTRE_BALDAS = 2000;

    /**
     * Borde de seguridad o margen (en milímetros).
     */
    public static int BORDE = 300;

    /**
     * Altura de cada balda (en milímetros).
     */
    public static int ALTO_BALDA = 100;

    /**
     * Ruta del archivo XML que contiene datos para el almacén.
     */
    public String archivoXML;

    /**
     * Lista estática que almacena todos los palets cargados.
     */
    public static ArrayList<Palet> TodosPalets = null;

    /**
     * Lista estática que almacena todos los productos cargados.
     */
    public static ArrayList<Producto> TodosProductos = null;

    /**
     * Lista estática que almacena todos los tipos cargados.
     */
    public static ArrayList<Tipo> TodosTipos = null;

    /**
     * Lista estática que almacena todos los proveedores cargados.
     */
    public static ArrayList<Proveedor> TodosProveedores = null;


    public static ArrayList<Cliente> TodosClientes = null;


    public Almacen(String archivoXML) {
        this.archivoXML = archivoXML;
    }

    public Almacen() {
    }


    /**
     * Genera el almacén cargando datos desde la base de datos o XML (según implementación).
     * Se conecta a la base de datos, obtiene tipos, productos y palets,
     * y establece las relaciones entre ellos.
     */
    public void GenerarAlmacen() {

        Connection conexion = null;
        try {

            if (Main.connection != null) {
                conexion = Main.connection;
            } else {
                conexion = DatabaseConnection.connect();
            }

            if (conexion != null) {

                LOGGER.fine("ALMACEN GENERADO CORRECTAMENTE.");
                // Se comenta la carga desde XML pero se carga desde base de datos:
                // ExtractXMLinfo.extraerDatosDeXML(conexion, archivoXML);

                TodosTipos = obtenerTiposDesdeBD(conexion);         // también si usas DB para todo
                TodosProductos = obtenerProductosDesdeBD(conexion); // deberías implementar estas
                TodosPalets = obtenerPaletsDesdeBD(conexion);
                TodosProveedores = obtenerProveedoresDesdeBD(conexion);
                TodosClientes = getAllClientes(conexion);
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }

        setInformationToPaletProductType(); // colocamos el color correspondiente a cada palet


    }

    public ArrayList<Proveedor> obtenerProveedoresDesdeBD(Connection conn) {
        ArrayList<Proveedor> proveedores = new ArrayList<>();
        String query = "SELECT id_proveedor, nombre, direccion, telefono, email, nif_cif, " +
                "contacto, fecha_registro, ultima_actualizacion " +
                "FROM proveedores ORDER BY nombre ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Proveedor prov = new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("nif_cif"),
                        rs.getString("contacto"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getTimestamp("ultima_actualizacion")
                );
                proveedores.add(prov);
            }

        } catch (Exception e) {
            LOGGER.warning("Error leyendo proveedores: " + e.getMessage());
        }

        return proveedores;
    }

    /**
     * Obtiene todos los palets almacenados en la base de datos.
     * Además, asigna a cada palet su producto correspondiente.
     *
     * @param conn Conexión activa a la base de datos.
     * @return Lista con todos los palets obtenidos.
     */
    public ArrayList<Palet> obtenerPaletsDesdeBD(Connection conn) {
        ArrayList<Palet> palets = new ArrayList<>();
        String query = "SELECT * FROM palets";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                palets.add(new Palet(
                        String.valueOf(rs.getInt("alto")),
                        String.valueOf(rs.getInt("ancho")),
                        String.valueOf(rs.getInt("largo")),

                        rs.getString("id_producto"),
                        String.valueOf(rs.getInt("cantidad_de_producto")),

                        rs.getString("identificador"),

                        rs.getInt("estanteria"),
                        rs.getInt("balda"),
                        String.valueOf(rs.getInt("posicion")),
                        String.valueOf(rs.getBoolean("delante"))
                ));


                for (Producto producto : TodosProductos) {
                    if (producto.getIdentificadorProducto().equals(palets.getLast().getIdProducto())) {
                        palets.getLast().setProducto(producto);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.warning("Error leyendo palets: " + e.getMessage());
        }

        return palets;
    }

    /**
     * Obtiene todos los productos almacenados en la base de datos.
     * Además, asigna a cada producto su tipo correspondiente.
     *
     * @param conn Conexión activa a la base de datos.
     * @return Lista con todos los productos obtenidos.
     */
    public ArrayList<Producto> obtenerProductosDesdeBD(Connection conn) {
        ArrayList<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                productos.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("identificador_producto"),
                        rs.getString("tipo_producto")
                ));

                for (Tipo tipo : TodosTipos) {

                    if (tipo.getIdTipo().equals(productos.getLast().getIdTipo())) {
                        productos.getLast().setTipo(tipo);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.warning("Error leyendo productos: " + e.getMessage());
        }

        return productos;
    }

    /**
     * Obtiene todos los tipos almacenados en la base de datos.
     *
     * @param conn Conexión activa a la base de datos.
     * @return Lista con todos los tipos obtenidos.
     */
    public ArrayList<Tipo> obtenerTiposDesdeBD(Connection conn) {
        ArrayList<Tipo> tipos = new ArrayList<>();
        String query = "SELECT * FROM tipos";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tipos.add(new Tipo(
                        rs.getString("color"),
                        rs.getString("id_tipo")
                ));
            }

        } catch (Exception e) {
            LOGGER.warning("Error leyendo tipos: " + e.getMessage());
        }

        return tipos;
    }


    /**
     * Establece la información y relaciones entre palets, productos y tipos.
     * Actualiza colores, cantidades y números de palets asociados para cada objeto.
     */
    private void setInformationToPaletProductType() {

        for (Palet palet : TodosPalets) {
            for (Producto producto : TodosProductos) {

                if (producto.getIdentificadorProducto().equals(palet.getIdProducto())) {
                    String idTipoProducto = producto.getIdTipo();


                    //añadimos la informacion del palet al producto correspondiente
                    producto.setNumPalets(producto.getNumPalets() + 1);
                    producto.setCantidadDeProducto(producto.getCantidadDeProducto() + palet.getCantidadProducto());

                    for (Tipo tipo : TodosTipos) {
                        if (idTipoProducto.equals(tipo.getIdTipo())) {
                            //añadimos la informacion al palet;
                            palet.setColorProducto(tipo.color_javaFx());
                            palet.setIdTipo(tipo.getIdTipo());

                            //añadimos la iformacion al producto
                            tipo.setNumPalets(tipo.getNumPalets() + 1);
                            tipo.setCantidadDeTipo(tipo.getCantidadDeTipo() + palet.getCantidadProducto());
                            break; // Termina el bucle una vez que se encuentra el tipo de producto
                        }
                    }
                }
            }
        }
    }


    /**
     * Muestra por consola la información completa del almacén (palets, productos y tipos).
     */
    public void MostrarAlmacen() {
        System.out.println(toString());
    }

    /**
     * Muestra por consola la información de todos los palets.
     */
    public void MostrarPalets() {
        for (Palet palet : TodosPalets) {
            if (palet != null) {
                System.out.println(palet);
            }
        }
    }

    /**
     * Muestra por consola la información de todos los productos.
     */
    public void MostrarProductos() {
        for (Producto producto : TodosProductos) {
            if (producto != null) {
                System.out.println(producto);
            }
        }
    }

    /**
     * Muestra por consola la información de todos los tipos.
     */
    public void MostrarTipos() {
        for (Tipo tipo : TodosTipos) {
            if (tipo != null) {
                System.out.println(tipo);
            }
        }
    }

    /**
     * Muestra por consola los colores asignados a cada palet.
     */
    public void MostrarColoresPalet() {
        for (Palet palet : TodosPalets) {
            if (palet != null) {
                System.out.println(palet.getColorProducto());
            }
        }
    }

    /**
     * Comprueba si existe un palet en la posición indicada (estantería, balda, posición, delante).
     *
     * @param estanteria Número de la estantería.
     * @param balda      Número de la balda.
     * @param posicion   Posición dentro de la balda.
     * @param delante    Indica si el palet está delante o no.
     * @return true si existe un palet en esa posición, false en caso contrario.
     */
    public Boolean ExistePalet(int estanteria, int balda, int posicion, Boolean delante) {

        for (Palet palet : TodosPalets) {
            if (palet.getEstanteria() == estanteria && palet.getBalda() == balda && palet.getPosicion() == posicion && palet.isDelante() == delante) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtiene el palet ubicado en la posición indicada (estantería, balda, posición, delante).
     *
     * @param estanteria Número de la estantería.
     * @param balda      Número de la balda.
     * @param posicion   Posición dentro de la balda.
     * @param delante    Indica si el palet está delante o no.
     * @return El objeto Palet si existe, o null si no se encuentra.
     */
    public Palet getPalet(int estanteria, int balda, int posicion, Boolean delante) {

        for (Palet palet : TodosPalets) {
            if (palet.getEstanteria() == estanteria && palet.getBalda() == balda && palet.getPosicion() == posicion && palet.isDelante() == delante) {
                return palet;
            }
        }

        return null;
    }


    /**
     * Devuelve una representación en texto con la información completa del almacén,
     * incluyendo palets, productos y tipos.
     *
     * @return String con toda la información concatenada.
     */
    @Override
    public String toString() {

        StringBuilder textoBuilder = new StringBuilder();

        for (Palet palet : TodosPalets) {
            if (palet != null) {
                textoBuilder.append(palet.toString()).append("\n");
            }
        }

        for (Producto producto : TodosProductos) {
            if (producto != null) {
                textoBuilder.append(producto.toString()).append("\n");
            }
        }

        for (Tipo tipo : TodosTipos) {
            if (tipo != null) {
                textoBuilder.append(tipo.toString()).append("\n");
            }
        }

        return textoBuilder.toString();
    }

}
