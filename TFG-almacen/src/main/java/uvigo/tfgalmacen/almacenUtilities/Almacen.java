package uvigo.tfgalmacen.almacenUtilities;

import java.util.ArrayList;

public class Almacen {
    public static int NUM_ESTANTERIAS = 4, NUM_BALDAS_PER_ESTANTERIA = 8;
    public static double SEPARACION = 0.3;
    public static int ANCHO_BALDA =3600, SEPARACION_ENTRE_BALDAS = 2000, BORDE = 300, ALTO_BALDA = 100;

    public String archivoXML;

    public ArrayList<Palet> TodosPalets = null;
    public ArrayList<Producto> TodosProductos = null;
    public ArrayList<Tipo> TodosTipos = null;


    public Almacen(String archivoXML) {
        this.archivoXML = archivoXML;
    }

    public void GenerarAlmacen() {
        try {
            TodosPalets = ExtractXMLinfo.extraerInfoAlamcen_XML(archivoXML);
            TodosProductos = ExtractXMLinfo.extraerInfoProductos_XML(archivoXML);
            TodosTipos = ExtractXMLinfo.extraerInfoTipo_XML(archivoXML);
            setInformationToPaletProductType(); // colocamos el color correspondiente a cada palet
        } catch (Exception ex) {
            String causa = ex.getMessage(); // Obtiene la causa
            System.out.println("Error: " + causa); // y la visualiza en pantalla
            System.exit(0); // Finaliza la ejecución del programa
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
                            //añadimos la informacion al palet
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
