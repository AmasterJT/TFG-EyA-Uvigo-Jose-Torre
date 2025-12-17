package uvigo.tfgalmacen.mobile_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.adapters.PedidosAdapter;
import uvigo.tfgalmacen.mobile_android.api.ApiClient;
import uvigo.tfgalmacen.mobile_android.api.PedidosApi;
import uvigo.tfgalmacen.mobile_android.models.itemPedidos;
public class MainActivity extends AppCompatActivity {

    private TextView nombreUsuarioText;
    private ListView listViewPedidos;

    private int id_usuario;

    private PedidosApi pedidosApi;
    private final ArrayList<itemPedidos> items = new ArrayList<>();
    private PedidosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombreUsuarioText = findViewById(R.id.nombre_usuario_text);
        listViewPedidos = findViewById(R.id.lista_pedidos);

        String nombre = getIntent().getStringExtra("nombre_usuario");
        String apellido = getIntent().getStringExtra("apellido_usuario");
        this.id_usuario = getIntent().getIntExtra("id_usuario", 0);

        if (nombre != null && apellido != null) {
            nombreUsuarioText.setText(nombre + " " + apellido);
        }

        // Adapter (campo, NO variable local)
        adapter = new PedidosAdapter(this, items);
        listViewPedidos.setAdapter(adapter);

        // API
        pedidosApi = ApiClient.getClient().create(PedidosApi.class);

        Toast.makeText(this, "id_usuario=" + id_usuario, Toast.LENGTH_SHORT).show();

        // Llamar a la API
        cargarPedidos(id_usuario);

        // (Opcional) click
        listViewPedidos.setOnItemClickListener((parent, view, position, id) -> {
            itemPedidos pedido = items.get(position);
            Intent intent = new Intent(MainActivity.this, PedidosActivity.class);
            intent.putExtra("pedido_name", pedido.getName());
            intent.putExtra("id_pedido", pedido.getId_pedido());
            intent.putExtra("palets_del_pedido", pedido.getPalets_del_pedido());


            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (id_usuario != 0) {
            cargarPedidos(id_usuario);
        }
    }

    private void cargarPedidos(int idUsuario) {
        pedidosApi.getPedidosEnProceso(idUsuario).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<java.util.List<uvigo.tfgalmacen.mobile_android.models.PedidoDto>> call,
                                   @NonNull retrofit2.Response<java.util.List<uvigo.tfgalmacen.mobile_android.models.PedidoDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                items.clear();
                for (uvigo.tfgalmacen.mobile_android.models.PedidoDto p : response.body()) {

                    items.add(new itemPedidos(
                            p.getCodigoReferencia(),
                            p.getHoraSalida(),
                            R.drawable.palet,
                            p.getIdPedido()
                    ));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<uvigo.tfgalmacen.mobile_android.models.PedidoDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
