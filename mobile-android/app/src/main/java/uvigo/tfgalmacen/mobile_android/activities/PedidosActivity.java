package uvigo.tfgalmacen.mobile_android.activities;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.adapters.ProductosAdapter;
import uvigo.tfgalmacen.mobile_android.api.ApiClient;
import uvigo.tfgalmacen.mobile_android.api.DetallesPedidoApi;
import uvigo.tfgalmacen.mobile_android.api.PedidosApi;
import uvigo.tfgalmacen.mobile_android.api.ProductosApi;
import uvigo.tfgalmacen.mobile_android.models.CambiarEstadoProductoDto;
import uvigo.tfgalmacen.mobile_android.models.DetallePedidoDto;
import uvigo.tfgalmacen.mobile_android.models.ProductoIdentificadorDto;
import uvigo.tfgalmacen.mobile_android.models.itemProductos;

public class PedidosActivity extends AppCompatActivity {

    private TextView codigo_referencia_text;
    private ListView lista_productos_pendientes;
    private ListView lista_productos_en_palet;

    private int id_pedido;

    private PedidosApi pedidosApi;
    private ProductosApi productosApi;

    private final ArrayList<itemProductos> pendientes = new ArrayList<>();
    private final ArrayList<itemProductos> enPalet = new ArrayList<>();

    private ProductosAdapter adapterPendientes;
    private ProductosAdapter adapterEnPalet;

    private DetallesPedidoApi detallesPedidoApi;


    private final Map<Integer, String> cacheIdentificador = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        codigo_referencia_text = findViewById(R.id.codigo_referencia_text);
        lista_productos_pendientes = findViewById(R.id.lista_productos_pendientes);
        lista_productos_en_palet = findViewById(R.id.lista_productos_en_palet);

        id_pedido = getIntent().getIntExtra("id_pedido", 0);
        String pedido = getIntent().getStringExtra("pedido_name");

        if (pedido != null) {
            codigo_referencia_text.setText(pedido);
        }

        adapterPendientes = new ProductosAdapter(this, pendientes, this::confirmarYCambiarEstado);
        adapterEnPalet = new ProductosAdapter(this, enPalet, this::confirmarYCambiarEstado);
        lista_productos_pendientes.setAdapter(adapterPendientes);
        lista_productos_en_palet.setAdapter(adapterEnPalet);

        pedidosApi = ApiClient.getClient().create(PedidosApi.class);
        productosApi = ApiClient.getClient().create(ProductosApi.class);
        detallesPedidoApi = ApiClient.getClient().create(DetallesPedidoApi.class);


        cargarDetallePedido(id_pedido);
    }

    private void confirmarYCambiarEstado(itemProductos item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Quieres cambiar el estado de este producto?\n\n" + item.getName())
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Aceptar", (dialog, which) -> ejecutarCambioEstado(item))
                .show();
    }

    private void ejecutarCambioEstado(itemProductos item) {

        int idDetalle = item.getIdDetalle();

        CambiarEstadoProductoDto body =
                new CambiarEstadoProductoDto(true); // o false según lógica

        detallesPedidoApi
                .cambiarEstadoProducto(idDetalle, body)
                .enqueue(new retrofit2.Callback<DetallePedidoDto>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<DetallePedidoDto> call,
                            @NonNull retrofit2.Response<DetallePedidoDto> response
                    ) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(PedidosActivity.this,
                                    "Error: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean nuevoEstado =
                                response.body().isEstadoProductoPedido();

                        item.setEstadoProductoPedido(nuevoEstado);

                        if (nuevoEstado) {
                            pendientes.remove(item);
                            enPalet.add(item);
                        } else {
                            enPalet.remove(item);
                            pendientes.add(item);
                        }

                        adapterPendientes.notifyDataSetChanged();
                        adapterEnPalet.notifyDataSetChanged();

                        Toast.makeText(PedidosActivity.this,
                                "Estado actualizado",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DetallePedidoDto> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(PedidosActivity.this,
                                "Error conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void cargarDetallePedido(int idPedido) {
        pedidosApi.getDetallePedido(idPedido).enqueue(new retrofit2.Callback<List<DetallePedidoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<DetallePedidoDto>> call,
                                   @NonNull Response<List<DetallePedidoDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PedidosActivity.this,
                            "Error detalle: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                pendientes.clear();
                enPalet.clear();

                for (DetallePedidoDto d : response.body()) {

                    int idProducto = d.getIdProducto();

                    itemProductos item = new itemProductos(
                            d.getIdDetalle(),
                            d.getIdProducto(),
                            "Cargando...",
                            String.valueOf(d.getCantidad()),
                            d.isEstadoProductoPedido()
                    );


                    if (d.isEstadoProductoPedido()) {
                        enPalet.add(item);
                    } else {
                        pendientes.add(item);
                    }

                    cargarIdentificadorProducto(idProducto, item);
                }

                adapterPendientes.notifyDataSetChanged();
                adapterEnPalet.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<DetallePedidoDto>> call,
                                  @NonNull Throwable t) {
                Toast.makeText(PedidosActivity.this,
                        "Error conexión detalle: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarIdentificadorProducto(int idProducto, itemProductos item) {

        if (cacheIdentificador.containsKey(idProducto)) {
            item.setName(cacheIdentificador.get(idProducto));
            adapterPendientes.notifyDataSetChanged();
            adapterEnPalet.notifyDataSetChanged();
            return;
        }

        productosApi.getIdentificador(idProducto).enqueue(new retrofit2.Callback<ProductoIdentificadorDto>() {
            @Override
            public void onResponse(@NonNull Call<ProductoIdentificadorDto> call,
                                   @NonNull Response<ProductoIdentificadorDto> response) {

                String identificador = "Prod " + idProducto;

                if (response.isSuccessful() && response.body() != null) {
                    String tmp = response.body().getIdentificadorProducto();
                    if (tmp != null && !tmp.isEmpty()) identificador = tmp;
                }

                cacheIdentificador.put(idProducto, identificador);
                item.setName(identificador);

                adapterPendientes.notifyDataSetChanged();
                adapterEnPalet.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ProductoIdentificadorDto> call,
                                  @NonNull Throwable t) {
                item.setName("Prod " + idProducto);
                adapterPendientes.notifyDataSetChanged();
                adapterEnPalet.notifyDataSetChanged();
            }
        });
    }
}
