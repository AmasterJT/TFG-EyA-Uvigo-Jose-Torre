package uvigo.tfgalmacen.almacenManagement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
}
