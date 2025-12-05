package uvigo.tfgalmacen.mobile_android.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.adapters.PedidosAdapter;
import uvigo.tfgalmacen.mobile_android.models.itemPedidos;

public class MainActivity extends AppCompatActivity {

    private TextView nombreUsuarioText;
    ListView listViewPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // tu layout

        // Referencia al TextView
        nombreUsuarioText = findViewById(R.id.nombre_usuario_text);

        // Obtener datos del Intent
        String nombre = getIntent().getStringExtra("nombre_usuario");
        String apellido = getIntent().getStringExtra("apellido_usuario");

        // Mostrar el nombre completo
        if (nombre != null && apellido != null) {
            nombreUsuarioText.setText(nombre + " " + apellido);
        }


        // ListView
        listViewPedidos = findViewById(R.id.lista_pedidos);

        listViewPedidos.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, "Click en posición " + position, Toast.LENGTH_SHORT).show();
        });

        // Datos de prueba

        // Crear 3 items de ejemplo
        ArrayList<itemPedidos> items = new ArrayList<>();
        items.add(new itemPedidos("Pedido 1", "10:00", R.drawable.palet));
        items.add(new itemPedidos("Pedido 2", "11:30", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));
        items.add(new itemPedidos("Pedido 3", "12:45", R.drawable.palet));

        PedidosAdapter adapter = new PedidosAdapter(this, items);


        // Asignarlo al ListView
        listViewPedidos.setAdapter(adapter);
    }
}
