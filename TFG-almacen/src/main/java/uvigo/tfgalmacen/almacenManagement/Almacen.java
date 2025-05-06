package uvigo.tfgalmacen.almacenManagement;

import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.database.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.io.FileWriter;
import java.io.PrintWriter;


public class Almacen {
    public static int NUM_ESTANTERIAS = 4, NUM_BALDAS_PER_ESTANTERIA = 8;
    public static double SEPARACION = 0.3;
    public static int ANCHO_BALDA =3600, SEPARACION_ENTRE_BALDAS = 2000, BORDE = 300, ALTO_BALDA = 100;

    public String archivoXML;

    public static ArrayList<Palet> TodosPalets = null;
    public static ArrayList<Producto> TodosProductos = null;
    public static ArrayList<Tipo> TodosTipos = null;


    public Almacen(String archivoXML) {
        this.archivoXML = archivoXML;
    }

    public Almacen() {
    }

    public void GenerarAlmacen() {

        Connection conexion = null;
        try {

            if (Main.connection != null) {
                conexion = Main.connection;
            } else {
                conexion = DatabaseConnection.connect();
            }

            if (conexion != null) {

                System.out.println("✅ ALMACEN GENERADO CORRECTAMENTE.");

                //extraerDatosDeXML(conexion); // Carga los datos desde XML
                TodosTipos = obtenerTiposDesdeBD(conexion);         // también si usas DB para todo
                TodosProductos = obtenerProductosDesdeBD(conexion); // deberías implementar estas
                TodosPalets = obtenerPaletsDesdeBD(conexion);
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }

        setInformationToPaletProductType(); // colocamos el color correspondiente a cada palet


    }

    public void extraerDatosDeXML(Connection conn) throws SQLException {

        insertarTiposDesdeXML(conn);
        insertarProductosDesdeXML(conn);
        insertarPaletsDesdeXML(conn); // OPCIONAL: solo si quieres cargar desde XML cada vez

    }

    public void insertarPaletsDesdeXML(Connection conn) throws SQLException {
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

    public void insertarProductosDesdeXML(Connection conn) throws SQLException {
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

    public void insertarTiposDesdeXML(Connection conn) throws SQLException {
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
                    if (producto.getIdProducto().equals(palets.getLast().getIdProducto())) {
                        palets.getLast().setProducto(producto);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error leyendo palets: " + e.getMessage());
        }

        return palets;
    }

    public ArrayList<Producto> obtenerProductosDesdeBD(Connection conn) {
        ArrayList<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                productos.add(new Producto(
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
            System.err.println("❌ Error leyendo productos: " + e.getMessage());
        }

        return productos;
    }

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
            System.err.println("❌ Error leyendo tipos: " + e.getMessage());
        }



        return tipos;
    }


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




    private void setInformationToPaletProductType() {

        for (Palet palet : TodosPalets) {
            for (Producto producto : TodosProductos) {

                if (producto.getIdProducto().equals(palet.getIdProducto())) {
                    String idTipoProducto = producto.getIdTipo();


                    //añadimos la informacion del palet al producto correspondiente
                    producto.setNumPalets(producto.getNumPalets()+1);
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



    public void MostrarAlmacen(){
        System.out.println(toString());
    }

    public void MostrarPalets() {
        for (Palet palet : TodosPalets) {
            if (palet != null) {
                System.out.println(palet);
            }
        }
    }

    public void MostrarProductos() {
        for (Producto producto : TodosProductos) {
            if (producto != null) {
                System.out.println(producto);
            }
        }
    }

    public void MostrarTipos() {
        for (Tipo tipo : TodosTipos) {
            if (tipo != null) {
                System.out.println(tipo);
            }
        }
    }

    public void MostrarColoresPalet() {
        for (Palet palet : TodosPalets) {
            if (palet != null) {
                System.out.println(palet.getColorProducto());
            }
        }
    }

    public Boolean ExistePalet(int estanteria, int balda, int posicion, Boolean delante) {

        for (Palet palet : TodosPalets) {
            if (palet.getEstanteria() == estanteria && palet.getBalda() == balda && palet.getPosicion() == posicion && palet.isDelante() == delante) {
                return true;
            }
        }

        return false;
    }

    public Palet getPalet(int estanteria, int balda, int posicion, Boolean delante) {

        for (Palet palet : TodosPalets) {
            if (palet.getEstanteria() == estanteria && palet.getBalda() == balda && palet.getPosicion() == posicion && palet.isDelante() == delante) {
                return palet;
            }
        }

        return null;
    }




    @Override
    public String toString(){

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
