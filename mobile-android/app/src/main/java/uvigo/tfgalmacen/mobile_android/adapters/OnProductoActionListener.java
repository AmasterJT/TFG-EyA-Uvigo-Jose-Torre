package uvigo.tfgalmacen.mobile_android.adapters;

import uvigo.tfgalmacen.mobile_android.models.itemProductos;

public interface OnProductoActionListener {
    void onCambiarEstadoClick(itemProductos item, android.widget.ImageButton button);
}