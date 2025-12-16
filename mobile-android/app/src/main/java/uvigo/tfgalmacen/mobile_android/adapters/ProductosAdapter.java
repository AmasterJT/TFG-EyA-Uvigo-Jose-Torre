package uvigo.tfgalmacen.mobile_android.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.models.itemProductos;

public class ProductosAdapter extends ArrayAdapter<itemProductos> {

    private LayoutInflater inflater;

    public ProductosAdapter(@NonNull Context context, List<itemProductos> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
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

        if (item != null) {
            name.setText(item.getName());
            cantidad.setText(item.getTime());
        }

        return convertView;
    }
}
