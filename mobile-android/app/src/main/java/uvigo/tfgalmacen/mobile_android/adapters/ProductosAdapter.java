package uvigo.tfgalmacen.mobile_android.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.models.itemProductos;

public class ProductosAdapter extends ArrayAdapter<itemProductos> {

    private final LayoutInflater inflater;
    private final OnProductoActionListener listener;

    public ProductosAdapter(@NonNull Context context, List<itemProductos> items, OnProductoActionListener listener) {
        super(context, 0, items);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_productos_pendientes, parent, false);
        }

        itemProductos item = getItem(position);

        TextView name = convertView.findViewById(R.id.ProductName);
        TextView cantidad = convertView.findViewById(R.id.Cantidad);
        ImageButton btn = convertView.findViewById(R.id.imageButton); // tu id

        if (item != null) {
            name.setText(item.getName());
            cantidad.setText(item.getCantidad());

            btn.setOnClickListener(v -> {
                if (listener != null) listener.onCambiarEstadoClick(item);
            });
        }

        return convertView;
    }
}

