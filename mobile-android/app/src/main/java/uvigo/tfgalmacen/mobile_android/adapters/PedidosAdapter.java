package uvigo.tfgalmacen.mobile_android.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.ArrayAdapter;

import java.util.List;

import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.models.itemPedidos;

public class PedidosAdapter extends ArrayAdapter<itemPedidos> {

    private LayoutInflater inflater;

    public PedidosAdapter(@NonNull Context context, List<itemPedidos> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_pedidos, parent, false);
        }

        itemPedidos item = getItem(position);

        ImageView img = convertView.findViewById(R.id.listImage);
        TextView name = convertView.findViewById(R.id.listName);
        TextView time = convertView.findViewById(R.id.listTime);

        if (item != null) {
            img.setImageResource(item.getImageRes());
            name.setText(item.getName());
            time.setText(item.getTime());
        }

        return convertView;
    }
}
