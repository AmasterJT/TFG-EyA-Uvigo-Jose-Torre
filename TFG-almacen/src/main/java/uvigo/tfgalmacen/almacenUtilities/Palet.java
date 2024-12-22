package uvigo.tfgalmacen.almacenUtilities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Box;


public class Palet {

    // Carcteristicas del Palet
    private static final double PALET_ANCHO = 1.2, PALET_PROFUNDO = 1.2, PALET_ALTO = 0.2;
    private int alto, ancho, cantidadProducto, idPalet, largo, posicion, estanteria, balda;
    private String idProducto, idTipo = "NO SE ASIGNO TIPO";
    private boolean delante;

    public int ANCHO_BALDA = Almacen.ANCHO_BALDA, SEPARACION_ENTRE_BALDAS = Almacen.SEPARACION_ENTRE_BALDAS, BORDE = Almacen.BORDE, ALTO_BALDA = Almacen.ALTO_BALDA;

    // estas variables guardan la posicion del plaet y el producto qque son objetos graficos de tipo Box en javaFX
    private Box paletBox, productBox;


    private Color ColorProducto = Color.RED;

    private final Color ColorPalet = Color.LIGHTGRAY;

    public static int TotalPalets = 0;

    // =========================CONSTRUCTORES===============================
    public Palet(String alto, String ancho, String cantidadProducto,
                 String delante, String idPalet, String idProducto,
                 String largo, String posicion, int estanteria, int balda) {
        this.alto = Integer.parseInt(alto);
        this.ancho = Integer.parseInt(ancho);
        this.cantidadProducto = Integer.parseInt(cantidadProducto);
        this.delante = !delante.equals("false");

        this.idPalet = Integer.parseInt(idPalet);
        this.idProducto = idProducto;
        this.largo = Integer.parseInt(largo);
        this.posicion = Integer.parseInt(posicion);

        this.estanteria = estanteria;
        this.balda = balda;

        TotalPalets++;
    }

    public Palet() {
        this.alto = this.ancho = this.cantidadProducto = this.idPalet = this.largo = this.posicion = this.posicion = this.estanteria = this.balda = 0;
        this.delante = false;
        this.idProducto = "";
        TotalPalets++;
    }
    //======================================================================

    //=======================GETTERS Y SETTERS =============================
    public Box getProductBox() {
        return productBox;
    }

    public void setProductBox(Box productBox) {
        this.productBox = productBox;
    }

    public Box getPaletBox() {
        return paletBox;
    }

    public void setPaletBox(Box paletBox) {
        this.paletBox = paletBox;
    }

    public double getPALET_ANCHO() {
        return PALET_ANCHO;
    }

    public double getPALET_PROFUNDO() {
        return PALET_PROFUNDO;
    }

    public double getPALET_ALTO() {
        return PALET_ALTO;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public int getIdPalet() {
        return idPalet;
    }

    public void setIdPalet(int idPalet) {
        this.idPalet = idPalet;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public int getLargo() {
        return largo;
    }

    public void setLargo(int largo) {
        this.largo = largo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public boolean isDelante() {
        return delante;
    }

    public void setDelante(boolean delante) {
        this.delante = delante;
    }

    public int getEstanteria() {
        return estanteria;
    }

    public void setEstanteria(int estanteria) {
        this.estanteria = estanteria;
    }

    public int getBalda() {
        return balda;
    }

    public void setBalda(int balda) {
        this.balda = balda;
    }

    public Color getColorProducto() {
        return ColorProducto;
    }

    public void setColorProducto(Color colorProducto) {
        ColorProducto = colorProducto;
    }

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    //==========================================================================

    //==================================METODOS=================================
    public Box CreaPalet() {
        // funcion para crear el elemento grafico de JavaFX que representa al palet

        int desplazamiento = 0;
        if (estanteria >= 3)
            desplazamiento = -3400;

        int Posdelante = 1500;
        if (delante) {
            if (estanteria%2 == 1) {
                Posdelante = 0;
            }
        } else {
            if (estanteria%2 == 0) {
                Posdelante = 0;
            }
        }

        //Gaurdamos la posicion del Box que representa el Producto del palet
        int X = ((int) (-PALET_PROFUNDO * 1000 - BORDE)) * (posicion - 1) - BORDE;
        int Y = -2 * ANCHO_BALDA - SEPARACION_ENTRE_BALDAS - (2 * (int) (PALET_ANCHO * 1000) + 2 * SEPARACION_ENTRE_BALDAS - 100) * (3 - estanteria - 1) + desplazamiento - Posdelante - BORDE;
        int Z = SEPARACION_ENTRE_BALDAS * (balda - 1) + ALTO_BALDA;

        return MisElementoGraficos.CreaParalelepipedo((int) (PALET_ANCHO * 1000),
                (int) (PALET_ALTO * 1000),
                (int) (PALET_PROFUNDO * 1000),
                X, Y, Z,
                ColorPalet);
    }

    public Box CreaProducto() {

        // funcion para crear el elemento grafico de JavaFX que representa al
        // producto sobre el palet

        int desplazamiento = 0;
        if (estanteria >= 3)
            desplazamiento = -3400;

        int Posdelante = 1500;
        if (delante) {
            if (estanteria%2 == 1) {
                Posdelante = 0;
            }
        } else {
            if (estanteria%2 == 0) {
                Posdelante = 0;
            }
        }

        //Gaurdamos la posicion del Box que representa el Producto del palet
        int X = ((int) (-PALET_PROFUNDO * 1000 - BORDE)) * (posicion - 1) - BORDE - (int) (PALET_PROFUNDO * 1000 - largo) / 2;
        int Y = -2 * ANCHO_BALDA - SEPARACION_ENTRE_BALDAS - (2 * (int) (PALET_ANCHO * 1000) + 2 * SEPARACION_ENTRE_BALDAS - 100) * (3 - estanteria - 1) + desplazamiento - Posdelante - BORDE - (int) (PALET_ANCHO * 1000 - ancho) / 2;
        int Z = SEPARACION_ENTRE_BALDAS * (balda - 1) + ALTO_BALDA + (int) (PALET_ALTO * 1000);

        //Devolvemos el Box creado con las coordenadas especificas
        return MisElementoGraficos.CreaParalelepipedo(
                ancho,
                alto,
                largo,
                X, Y, Z,
                ColorProducto);
    }

    @Override
    public String toString() {
        return Colores.CYAN + "Palet: id:" + this.getIdPalet() +
                " alto:" + this.getAlto() +
                " ancho:" + this.getAncho() +
                " largo:" + this.getLargo() +
                " CantidadProducto:" + this.getCantidadProducto() +
                " IdProducto:" + this.getIdProducto() +
                " (" + this.getEstanteria() + ";" + this.getBalda() + "; " + this.getPosicion() + "; " + this.isDelante() + ")"
                + Colores.RESET_COLOR;
    }


}
