package uvigo.tfgalmacen.almacenManagement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;

public class ExtractXMLinfo {

    public static ArrayList<Palet> extraerInfoAlamcen_XML(String archivoXML) {
        ArrayList<Palet> PaletsTotales = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document archivoXml = db.parse(archivoXML);

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

                        PaletsTotales.add(new Palet(
                                palet_individual.getAttribute("alto"),
                                palet_individual.getAttribute("ancho"),
                                palet_individual.getAttribute("cantidadProducto"),
                                palet_individual.getAttribute("delante"),
                                palet_individual.getAttribute("idPalet"),
                                palet_individual.getAttribute("idProducto"),
                                palet_individual.getAttribute("largo"),
                                palet_individual.getAttribute("posicion"),
                                estanteria, balda
                        ));
                    }
                }
            }

        } catch (Exception ex) {
            String causa = ex.getMessage();
            System.out.println("Error: " + causa);
            System.exit(0);
        }

        return PaletsTotales;
    }

    public static ArrayList<Producto> extraerInfoProductos_XML(String archivoXML) {
        ArrayList<Producto> TodosProductos = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document archivoXml = db.parse(archivoXML);

            Element nodoRaiz = archivoXml.getDocumentElement();
            NodeList NodoProductos = nodoRaiz.getElementsByTagName("producto");

            for (int i = 0; i < NodoProductos.getLength(); i++) {
                Element producto = (Element) NodoProductos.item(i);
                TodosProductos.add(new Producto(
                        producto.getAttribute("idProducto"),
                        producto.getAttribute("idTipo")
                ));
            }

        } catch (Exception ex) {
            String causa = ex.getMessage();
            System.out.println("Error: " + causa);
            System.exit(0);
        }

        return TodosProductos;
    }

    public static ArrayList<Tipo> extraerInfoTipo_XML(String archivoXML) {
        ArrayList<Tipo> TodosTipos = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document archivoXml = db.parse(archivoXML);

            Element nodoRaiz = archivoXml.getDocumentElement();
            NodeList NodoTipo = nodoRaiz.getElementsByTagName("tipo");

            for (int i = 0; i < NodoTipo.getLength(); i++) {
                Element tipo = (Element) NodoTipo.item(i);
                TodosTipos.add(new Tipo(
                        tipo.getAttribute("color"),
                        tipo.getAttribute("idTipo")
                ));
            }

        } catch (Exception ex) {
            String causa = ex.getMessage();
            System.out.println("Error: " + causa);
            System.exit(0);
        }

        return TodosTipos;
    }
}
