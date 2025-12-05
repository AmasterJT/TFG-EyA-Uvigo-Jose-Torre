package uvigo.tfgalmacen.mobile_android.models;

public class itemPedidos {

    private String name;
    private String time;
    private int imageRes;

    public itemPedidos(String name, String time, int imageRes) {
        this.name = name;
        this.time = time;
        this.imageRes = imageRes;
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
}
