package uvigo.tfgalmacen.dataTransform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import uvigo.tfgalmacen.database.DatabaseConnection;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static uvigo.tfgalmacen.database.TableLister.getTables;

public class DataExporter {

    static String FILE_EXTENTION = ".xml";
    static String OUTPUT_DIRECTORY = "output_files/";

    // Colores ANSI para consola
    private static final String RESET = "\033[0m";  // Resetea el color
    private static final String GREEN = "\033[32m"; // Verde
    private static final String RED = "\033[31m";   // Rojo
    private static final String ORANGE = "\033[34m";  // Azul

    public static void exportTableToXML(Connection connection, String tableName) {

        String outputPath = OUTPUT_DIRECTORY + tableName + FILE_EXTENTION;

        try {
            System.out.print("⏳" + "Iniciando conversión...");

            // Ejecutar consulta para obtener todos los datos de la tabla
            String query = "SELECT * FROM " + tableName;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Crear documento XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Crear elemento raíz
            Element root = document.createElement("TableData");
            root.setAttribute("name", tableName);
            document.appendChild(root);

            // Procesar los resultados y generar nodos XML
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Element row = document.createElement("Row");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = resultSet.getString(i);

                    Element column = document.createElement(columnName);
                    column.appendChild(document.createTextNode(value != null ? value : ""));
                    row.appendChild(column);
                }
                root.appendChild(row);
            }

            // Guardar el XML en un archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new FileWriter(outputPath));

            transformer.transform(domSource, streamResult);

            System.out.println("✔️");
            System.out.println("✅ Datos exportados a " + GREEN + outputPath + RESET);
            System.out.println("✅ " + GREEN + "Conversión finalizada con exito" + RESET);

        } catch (SQLException | ParserConfigurationException | TransformerException | IOException e) {
            System.err.println("❌ Error exportando datos a XML: " + e.getMessage());
        }
    }


    public static void exportDatabaseTablesToXML(Connection connection) throws SQLException {
        System.out.println("⏳" + "Iniciando conversión...");
        List<String> tablesNames = getTables( connection, DatabaseConnection.DATABASE_NAME);

        for (String tableName : tablesNames) {

            String outputPath = OUTPUT_DIRECTORY + tableName + FILE_EXTENTION;
            System.out.print("\uD83D\uDD50 Creando el archivo " + tableName + FILE_EXTENTION + " ...");

            try {

                // Ejecutar consulta para obtener todos los datos de la tabla
                String query = "SELECT * FROM " + tableName;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Crear documento XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();

                // Crear elemento raíz
                Element root = document.createElement("TableData");
                root.setAttribute("name", tableName);
                document.appendChild(root);

                // Procesar los resultados y generar nodos XML
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Element row = document.createElement("Row");
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String value = resultSet.getString(i);

                        Element column = document.createElement(columnName);
                        column.appendChild(document.createTextNode(value != null ? value : ""));
                        row.appendChild(column);
                    }
                    root.appendChild(row);
                }

                System.out.println("✔️");

                // Guardar el XML en un archivo
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(new FileWriter(outputPath));

                transformer.transform(domSource, streamResult);

                System.out.println("✅ Datos exportados a " + GREEN + "'" + outputPath + "' " +  RESET);

            } catch (SQLException | ParserConfigurationException | TransformerException | IOException e) {
                System.err.println("❌ Error exportando datos a XML: " + e.getMessage());
            }

        }
        System.out.println("✅ " + GREEN + "Conversión finalizada con exito" + RESET);

    }


    public static void exportDatabaseToXML(Connection connection) {
        try {
            System.out.println("⏳ Iniciando conversión...");
            List<String> tablesNames = getTables(connection, DatabaseConnection.DATABASE_NAME);
            String DATABASE_XML_NAME_OUTPUTFILE = OUTPUT_DIRECTORY + DatabaseConnection.DATABASE_NAME + FILE_EXTENTION;

            // Crear un documento vacío para el XML combinado
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document outputDoc = builder.newDocument();

            // Crear una raíz común
            Element root = outputDoc.createElement(DatabaseConnection.DATABASE_NAME);
            outputDoc.appendChild(root);

            for (String tableName : tablesNames) {

                String current_inputFile = OUTPUT_DIRECTORY + tableName + FILE_EXTENTION;

                File file = new File(current_inputFile);
                if (!file.exists()) {
                    // Avisar que el archivo no existe
                    System.out.println("⚠️ " + ORANGE + "El archivo " + current_inputFile + " no existe. Se omitirá." + RESET);
                    continue; // Ignorar este archivo y pasar al siguiente
                }
                System.out.print("\uD83D\uDD50 Procesando el archivo " + tableName + FILE_EXTENTION + " ...");

                try {
                    Document inputDoc = builder.parse(file);

                    // Obtener el nodo raíz del archivo de entrada
                    NodeList nodes = inputDoc.getDocumentElement().getChildNodes();

                    // Copiar los nodos de entrada a la raíz del documento de salida
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            // Importar el nodo al documento de salida
                            Node importedNode = outputDoc.importNode(node, true);
                            root.appendChild(importedNode);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error procesando el archivo " + current_inputFile + ": " + e.getMessage());
                }

                System.out.println("✔️");
            }

            // Escribir el documento combinado en un archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(outputDoc);
            StreamResult result = new StreamResult(new File(DATABASE_XML_NAME_OUTPUTFILE));

            transformer.transform(source, result);
            System.out.println("✅ Archivo XML combinado guardado como: " + GREEN + DATABASE_XML_NAME_OUTPUTFILE + RESET);

        } catch (Exception e) {
            System.err.println("❌ Error exportando datos a XML: " + e.getMessage());
        }

        System.out.println("✅ " + GREEN + "Conversión finalizada con éxito" + RESET);
    }


}