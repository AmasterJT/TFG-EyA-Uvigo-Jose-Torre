package uvigo.tfgalmacen.almacenManagement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase ExtractXMLinfo
 *
 * Esta clase se encarga de leer y extraer información estructurada desde un archivo XML
 * para construir listas de objetos del tipo Palet, Producto y Tipo.
 *
 * Los datos extraídos se utilizan, por ejemplo, para cargar el contenido de un almacén
 * en una base de datos.
 */
public class ExtractXMLinfo {

    public String archivoXML;


    /**
     * Método extraerInfoAlamcen_XML
     *
     * @param archivoXML Ruta del archivo XML que contiene la información del almacén
     * @return Lista de objetos Palet con todos los palets leídos del XML
     *
     * Este método extrae información de palets ubicada en una estructura jerárquica XML:
     * <almacen>
     *   <estanteria>
     *     <balda>
     *       <palet alto="" ancho="" largo="" idProducto="" cantidadProducto="" idPalet=""
     *              posicion="" delante="" />
     *     </balda>
     *   </estanteria>
     * </almacen>
     */
    public static ArrayList<Palet> extraerInfoAlamcen_XML(String archivoXML) {
        ArrayList<Palet> PaletsTotales = new ArrayList<>();

        try {
            // Crear el parser XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document archivoXml = db.parse(archivoXML);

            // Obtener nodo raíz y recorrer las estanterías
            Element nodoRaiz = archivoXml.getDocumentElement();
            NodeList estanterias = nodoRaiz.getElementsByTagName("estanteria");

            for (int i = 0; i < estanterias.getLength(); i++) {
                Element estanteria_individual = (Element) estanterias.item(i);
                NodeList baldas = estanteria_individual.getElementsByTagName("balda");

                for (int j = 0; j < baldas.getLength(); j++) {
                    Element balda_individual = (Element) baldas.item(j);
                    NodeList palets = balda_individual.getElementsByTagName("palet");

                    for (int k = 0; k < palets.getLength(); k++) {
                        Element palet_individual = (Element) palets.item(k);

                        int estanteria = i + 1;
                        int balda = j + 1;

                        // Crear objeto Palet con los atributos del XML y posición
                        PaletsTotales.add(new Palet(
                                palet_individual.getAttribute("alto"),
                                palet_individual.getAttribute("ancho"),
                                palet_individual.getAttribute("largo"),
                                palet_individual.getAttribute("idProducto"),
                                palet_individual.getAttribute("cantidadProducto"),
                                palet_individual.getAttribute("idPalet"),
                                estanteria, balda,
                                palet_individual.getAttribute("posicion"),
                                palet_individual.getAttribute("delante")
                        ));
                    }
                }
            }

        } catch (Exception ex) {
            // En caso de error, se imprime el mensaje y se detiene la ejecución
            String causa = ex.getMessage();
            System.out.println("Error: " + causa);
            System.exit(0);
        }

        return PaletsTotales;
    }

    /**
     * Método extraerInfoProductos_XML
     *
     * @param archivoXML Ruta del archivo XML que contiene los productos
     * @return Lista de objetos Producto con todos los productos leídos
     *
     * Este método extrae productos definidos directamente en el XML con estructura como:
     * <almacen>
     *   <producto idProducto="P001" idTipo="T01"/>
     * </almacen>
     */
    public static ArrayList<Producto> extraerInfoProductos_XML(String archivoXML) {
        ArrayList<Producto> TodosProductos = new ArrayList<>();

        try {
            // Preparar el parser XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document archivoXml = db.parse(archivoXML);

            // Buscar nodos <producto>
            Element nodoRaiz = archivoXml.getDocumentElement();
            NodeList NodoProductos = nodoRaiz.getElementsByTagName("producto");

            for (int i = 0; i < NodoProductos.getLength(); i++) {
                Element producto = (Element) NodoProductos.item(i);
                // Crear y añadir el objeto Producto
                TodosProductos.add(new Producto(
                        producto.getAttribute("idProducto"),
                        producto.getAttribute("idTipo")
                ));
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }

        return TodosProductos;
    }

    /**
     * Método extraerInfoTipo_XML
     *
     * @param archivoXML Ruta del archivo XML que contiene los tipos de producto
     * @return Lista de objetos Tipo con todos los tipos leídos
     *
     * Este método extrae los tipos de producto desde nodos como:
     * <almacen>
     *   <tipo idTipo="T01" color="#FF0000"/>
     * </almacen>
     */
    public static ArrayList<Tipo> extraerInfoTipo_XML(String archivoXML) {
        ArrayList<Tipo> TodosTipos = new ArrayList<>();

        try {
            // Preparar el parser XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document archivoXml = db.parse(archivoXML);

            // Buscar nodos <tipo>
            Element nodoRaiz = archivoXml.getDocumentElement();
            NodeList NodoTipo = nodoRaiz.getElementsByTagName("tipo");

            for (int i = 0; i < NodoTipo.getLength(); i++) {
                Element tipo = (Element) NodoTipo.item(i);
                // Crear y añadir el objeto Tipo
                TodosTipos.add(new Tipo(
                        tipo.getAttribute("color"),
                        tipo.getAttribute("idTipo")
                ));
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }

        return TodosTipos;
    }










    /**
     * Exporta una lista de objetos Palet a un archivo de texto con instrucciones SQL INSERT.
     * Cada palet genera una instrucción SQL para insertar sus datos en la tabla "palets".
     *
     * @param palets      Lista de objetos Palet que se desean exportar.
     * @param nombreArchivo Nombre del archivo donde se guardarán las instrucciones SQL.
     */
    public void exportarPaletsASQL(ArrayList<Palet> palets, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (Palet palet : palets) {
                String insert = String.format(
                        "INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) " +
                                "VALUES ('%s', '%s', %s, %s, %s, %s, %d, %d, %s, %s);",
                        palet.getIdPalet(),
                        palet.getIdProducto(),
                        palet.getAlto(),
                        palet.getAncho(),
                        palet.getLargo(),
                        palet.getCantidadProducto(),
                        palet.getEstanteria(),
                        palet.getBalda(),
                        palet.getPosicion(),
                        String.valueOf(palet.isDelante())
                );
                writer.println(insert);
            }
            System.out.println("✅ Instrucciones SQL exportadas a " + nombreArchivo);
        } catch (Exception e) {
            System.err.println("❌ Error al exportar SQL: " + e.getMessage());
        }
    }


    /**
     * Exporta una lista de objetos Tipo a un archivo de texto con instrucciones SQL INSERT.
     * Cada tipo genera una instrucción SQL para insertar sus datos en la tabla "tipos".
     *
     * @param tipos        Lista de objetos Tipo que se desean exportar.
     * @param nombreArchivo Nombre del archivo donde se guardarán las instrucciones SQL.
     */
    public void exportarTiposASQL(ArrayList<Tipo> tipos, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (Tipo tipo : tipos) {
                String insert = String.format(
                        "INSERT INTO tipos (id_tipo, color) VALUES ('%s', '%s');",
                        tipo.getIdTipo(),
                        tipo.getColor()
                );
                writer.println(insert);
            }
            System.out.println("✅ Instrucciones SQL de tipos exportadas a " + nombreArchivo);
        } catch (Exception e) {
            System.err.println("❌ Error al exportar SQL de tipos: " + e.getMessage());
        }
    }

    /**
     * Exporta una lista de objetos Producto a un archivo de texto con instrucciones SQL INSERT.
     * Cada producto genera una instrucción SQL para insertar sus datos en la tabla "productos".
     *
     * @param productos    Lista de objetos Producto que se desean exportar.
     * @param nombreArchivo Nombre del archivo donde se guardarán las instrucciones SQL.
     */
    public void exportarProductosASQL(ArrayList<Producto> productos, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (Producto producto : productos) {
                String insert = String.format(
                        "INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('%s', '%s');",
                        producto.getIdProducto(),
                        producto.getIdTipo()
                );
                writer.println(insert);
            }
            System.out.println("✅ Instrucciones SQL de productos exportadas a " + nombreArchivo);
        } catch (Exception e) {
            System.err.println("❌ Error al exportar SQL de productos: " + e.getMessage());
        }
    }




    /**
     * Extrae datos de un archivo XML e inserta la información en la base de datos.
     * Llama a métodos específicos para insertar tipos, productos y palets desde el XML.
     *
     * @param conn       Conexión activa a la base de datos donde se insertarán los datos.
     * @param archivoXML Ruta del archivo XML desde donde se extraerán los datos.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public static void extraerDatosDeXML(Connection conn, String archivoXML) throws SQLException {

        insertarTiposDesdeXML(conn, archivoXML);
        insertarProductosDesdeXML(conn, archivoXML);
        insertarPaletsDesdeXML(conn, archivoXML); // OPCIONAL: solo si quieres cargar desde XML cada vez

    }


    /**
     * Inserta registros de tipos extraídos de un archivo XML en la tabla "tipos" de la base de datos.
     * Además, exporta las instrucciones SQL generadas a un archivo "tipos_inserts.sql".
     *
     * @param conn       Conexión activa a la base de datos.
     * @param archivoXML Ruta del archivo XML con la información de tipos.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public static void insertarTiposDesdeXML(Connection conn, String archivoXML) throws SQLException {
        ArrayList<Tipo> tipos = ExtractXMLinfo.extraerInfoTipo_XML(archivoXML);

        String insertQuery = "INSERT INTO tipos (id_tipo, color) VALUES (?, ?)";


        PreparedStatement statement = conn.prepareStatement(insertQuery);

        for (Tipo tipo : tipos) {
            statement.setString(1, tipo.getIdTipo());
            statement.setString(2, tipo.getColor());

            statement.addBatch();
        }

        statement.executeBatch();
        System.out.println("✅ Tipos insertados correctamente en la base de datos.");


        exportarTiposASQL(tipos, "tipos_inserts.sql");
    }

    /**
     * Inserta registros de productos extraídos de un archivo XML en la tabla "productos" de la base de datos.
     * Los campos nombre, descripción y color están vacíos, pero se pueden completar si se dispone de datos.
     * También exporta las instrucciones SQL generadas a un archivo "productos_inserts.sql".
     *
     * @param conn       Conexión activa a la base de datos.
     * @param archivoXML Ruta del archivo XML con la información de productos.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public static void insertarProductosDesdeXML(Connection conn, String archivoXML) throws SQLException {
        ArrayList<Producto> productos = ExtractXMLinfo.extraerInfoProductos_XML(archivoXML);

        String insertQuery = "INSERT INTO productos (identificador_producto, tipo_producto, nombre_producto, descripcion, color) VALUES (?, ?, ?, ?, ?)";


        PreparedStatement statement = conn.prepareStatement(insertQuery);

        for (Producto producto : productos) {
            statement.setString(1, producto.getIdProducto()); // identificador_producto
            statement.setString(2, producto.getIdTipo());     // tipo_producto
            statement.setString(3, ""); // nombre_producto (rellena si tienes)
            statement.setString(4, ""); // descripcion
            statement.setString(5, ""); // color
            statement.addBatch();
        }

        statement.executeBatch();
        System.out.println("✅ Productos insertados correctamente.");
        exportarProductosASQL(productos, "productos_inserts.sql");
    }

    /**
     * Inserta registros de palets extraídos de un archivo XML en la tabla "palets" de la base de datos.
     * También exporta las instrucciones SQL generadas a un archivo "palets_inserts.sql".
     *
     * @param conn       Conexión activa a la base de datos.
     * @param archivoXML Ruta del archivo XML con la información de palets.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public static void insertarPaletsDesdeXML(Connection conn, String archivoXML) throws SQLException {
        ArrayList<Palet> palets = ExtractXMLinfo.extraerInfoAlamcen_XML(archivoXML);

        String insertQuery = "INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        //try (Connection conn = DatabaseConnection.connect()) {
        PreparedStatement statement = conn.prepareStatement(insertQuery);

        for (Palet palet : palets) {
            statement.setInt(1, palet.getIdPalet());
            statement.setString(2, palet.getIdProducto());
            statement.setInt(3, palet.getAlto());
            statement.setInt(4, palet.getAncho());
            statement.setInt(5, palet.getLargo());
            statement.setInt(6, palet.getCantidadProducto());
            statement.setInt(7, palet.getEstanteria());
            statement.setInt(8, palet.getBalda());
            statement.setInt(9, palet.getPosicion());
            statement.setBoolean(10, palet.isDelante());

            statement.addBatch();
        }

        statement.executeBatch();
        System.out.println("✅ Palets insertados correctamente en la base de datos.");


        exportarPaletsASQL(palets, "palets_inserts.sql");
    }

}
