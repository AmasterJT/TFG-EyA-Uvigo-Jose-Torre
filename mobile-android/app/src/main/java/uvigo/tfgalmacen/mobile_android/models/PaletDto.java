package uvigo.tfgalmacen.mobile_android.models;

import com.google.gson.annotations.SerializedName;

public class PaletDto {

    @SerializedName("idPalet")
    private int idPalet;

    private int alto;
    private int ancho;

    @SerializedName("largo") // en JSON viene "largo"
    private int profundo;

    @SerializedName("cantidadDeProducto")
    private int cantidadDeProducto;

    public int getIdPalet() { return idPalet; }
    public int getAlto() { return alto; }
    public int getAncho() { return ancho; }
    public int getProfundo() { return profundo; }
    public int getCantidadDeProducto() { return cantidadDeProducto; }
}
