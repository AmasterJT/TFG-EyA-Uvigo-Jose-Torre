package uvigo.tfgalmacen.mobile_android.models;

import android.widget.Toast;

import uvigo.tfgalmacen.mobile_android.activities.MainActivity;

public class itemPedidos {

    private String name;
    private String time;
    private int imageRes;

    private int id_pedido;

    public itemPedidos(String name, String time, int imageRes, int id_pedido) {
        this.name = name;
        this.time = time;
        this.imageRes = imageRes;
        this.id_pedido = id_pedido;

    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getId_pedido() {
        return id_pedido;
    }
}
