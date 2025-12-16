package uvigo.tfgalmacen.mobile_android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.adapters.PedidosAdapter;
import uvigo.tfgalmacen.mobile_android.adapters.ProductosAdapter;
import uvigo.tfgalmacen.mobile_android.models.itemPedidos;
import uvigo.tfgalmacen.mobile_android.models.itemProductos;

public class PedidosActivity extends AppCompatActivity  {

    TextView codigo_referencia_text;
    ListView lista_productos_pendientes;
    ListView lista_productos_en_palet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Toast.makeText(this, "PedidosActivity abierta", Toast.LENGTH_SHORT).show();

        codigo_referencia_text = findViewById(R.id.codigo_referencia_text);
        lista_productos_pendientes = findViewById(R.id.lista_productos_pendientes);
        lista_productos_en_palet = findViewById(R.id.lista_productos_en_palet);


        if (codigo_referencia_text == null) {
            Toast.makeText(this, "NO encuentro codigo_referencia_text en el layout", Toast.LENGTH_LONG).show();
            return;
        }

        String pedido = getIntent().getStringExtra("pedido_name");
        codigo_referencia_text.setText(pedido != null ? pedido : "(sin pedido_name)");


        ArrayList<itemProductos> items = new ArrayList<>();
        items.add(new itemProductos("Producto A", "11"));
        items.add(new itemProductos("Producto B", "222"));
        items.add(new itemProductos("Producto C", "3333"));
        ProductosAdapter adapter = new ProductosAdapter(this, items);
        lista_productos_pendientes.setAdapter(adapter);



        ArrayList<itemProductos> itemsB = new ArrayList<>();
        itemsB.add(new itemProductos("Producto D", "444"));
        itemsB.add(new itemProductos("Producto E", "5555"));
        itemsB.add(new itemProductos("Producto F", "6666"));
        ProductosAdapter adapterB = new ProductosAdapter(this, itemsB);
        lista_productos_en_palet.setAdapter(adapterB);

    }
}