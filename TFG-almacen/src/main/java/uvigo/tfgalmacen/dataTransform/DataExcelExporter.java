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
import uvigo.tfgalmacen.database.DatabaseConnection;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static uvigo.tfgalmacen.utils.TerminalColors.*;
import static uvigo.tfgalmacen.database.TableLister.getTables;

public class DataExcelExporter {

    // Extensión de los archivos de salida
    static String FILE_EXTENTION = ".xml";
    // Carpeta donde se guardarán los archivos exportados
    static String OUTPUT_DIRECTORY = "output_files/";

    /**
     * Exporta una tabla específica de la base de datos a un archivo XML.
     * @param connection Conexión a la base de datos.
     * @param tableName Nombre de la tabla a exportar.
     */
    public static void exportTableToXML(Connection connection, String tableName) {
        verificarYCrearDirectorio();

        String outputPath = OUTPUT_DIRECTORY + tableName + FILE_EXTENTION;

        try {
            System.out.print("⏳ Iniciando conversión...");

            // Consulta para obtener todos los datos de la tabla
            String query = "SELECT * FROM " + tableName;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Creación del documento XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Nodo raíz del XML
            Element root = document.createElement("TableData");
            root.setAttribute("name", tableName);
            document.appendChild(root);

            // Obtener metadatos de la tabla
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterar sobre los resultados y crear nodos XML
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
            System.out.println("✅ Datos exportados a " + VERDE + outputPath + RESET);
        } catch (SQLException | ParserConfigurationException | TransformerException | IOException e) {
            System.err.println("❌ Error exportando datos a XML: " + e.getMessage());
        }
    }

    /**
     * Exporta todas las tablas de la base de datos a archivos XML individuales.
     * @param connection Conexión a la base de datos.
     */
    public static void exportDatabaseTablesToXML(Connection connection) throws SQLException {
        System.out.println("⏳ Iniciando conversión...");
        List<String> tablesNames = getTables(connection, DatabaseConnection.DATABASE_NAME);

        for (String tableName : tablesNames) {
            exportTableToXML(connection, tableName);
        }

        System.out.println("✅ " + VERDE + "Conversión finalizada con éxito" + RESET);
    }

    /**
     * Combina todos los archivos XML generados en un solo archivo XML.
     * @param connection Conexión a la base de datos.
     */
    public static void exportDatabaseToXML(Connection connection) {
        verificarYCrearDirectorio();
        try {
            System.out.println("⏳ Iniciando conversión...");
            List<String> tablesNames = getTables(connection, DatabaseConnection.DATABASE_NAME);
            String outputFile = OUTPUT_DIRECTORY + DatabaseConnection.DATABASE_NAME + FILE_EXTENTION;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document outputDoc = builder.newDocument();

            // Nodo raíz del XML
            Element root = outputDoc.createElement(DatabaseConnection.DATABASE_NAME);
            outputDoc.appendChild(root);

            for (String tableName : tablesNames) {
                String inputFilePath = OUTPUT_DIRECTORY + tableName + FILE_EXTENTION;
                File file = new File(inputFilePath);

                if (!file.exists()) {
                    System.out.println("⚠️ " + NARANJA + "El archivo " + inputFilePath + " no existe. Se omitirá." + RESET);
                    continue;
                }

                System.out.print("⏳ Procesando el archivo " + tableName + FILE_EXTENTION + " ...");

                // Crear un elemento para cada tabla dentro del XML
                Element tableElement = outputDoc.createElement(tableName);

                // Procesar cada archivo de la tabla
                Document inputDoc = builder.parse(file);
                NodeList nodes = inputDoc.getDocumentElement().getChildNodes();

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Node importedNode = outputDoc.importNode(node, true);
                        tableElement.appendChild(importedNode);
                    }
                }

                // Agregar la tabla al nodo raíz
                root.appendChild(tableElement);

                System.out.println("✔️");
            }

            // Guardar el XML en el archivo de salida
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(outputDoc);
            StreamResult result = new StreamResult(new File(outputFile));

            transformer.transform(source, result);
            System.out.println("✅ Archivo XML combinado guardado como: " + VERDE + outputFile + RESET);
        } catch (Exception e) {
            System.err.println("❌ Error exportando datos a XML: " + e.getMessage());
        }

        System.out.println("✅ " + VERDE + "Conversión finalizada con éxito" + RESET);

    }


    // Función para verificar y crear el directorio si no existe
    public static void verificarYCrearDirectorio() {
        File directorio = new File(OUTPUT_DIRECTORY);

        // Si el directorio no existe, lo creamos
        if (!directorio.exists()) {
            boolean creado = directorio.mkdirs(); // Crea el directorio y sus subdirectorios si es necesario
            if (creado) {
                System.out.println("✅ Directorio creado: " + OUTPUT_DIRECTORY);
            } else {
                System.err.println("❌ Error al crear el directorio: " + OUTPUT_DIRECTORY);
            }
        } else {
            System.out.println("✔️ El directorio ya existe: " + OUTPUT_DIRECTORY);
        }
    }



}
