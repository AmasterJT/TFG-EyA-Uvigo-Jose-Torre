package uvigo.tfgalmacen.mobile_android.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.adapters.ProductosAdapter;
import uvigo.tfgalmacen.mobile_android.api.ApiClient;
import uvigo.tfgalmacen.mobile_android.api.DetallesPedidoApi;
import uvigo.tfgalmacen.mobile_android.api.PedidosApi;
import uvigo.tfgalmacen.mobile_android.api.ProductosApi;
import uvigo.tfgalmacen.mobile_android.models.CambiarEstadoPedidoDto;
import uvigo.tfgalmacen.mobile_android.models.CambiarEstadoProductoDto;
import uvigo.tfgalmacen.mobile_android.models.CambiarPaletizadoDto;
import uvigo.tfgalmacen.mobile_android.models.CambiarPaletsPedidoDto;
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

    private ImageButton paletizarButton;

    private int paletsDelPedido;


    private final Map<Integer, String> cacheIdentificador = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        codigo_referencia_text = findViewById(R.id.codigo_referencia_text);
        lista_productos_pendientes = findViewById(R.id.lista_productos_pendientes);
        lista_productos_en_palet = findViewById(R.id.lista_productos_en_palet);


        paletizarButton = findViewById(R.id.paletizarButton);


        // paletizarButton puede no existir aún -> NO crashear
        if (paletizarButton == null) {
            Toast.makeText(this, "No existe paletizarButton en el layout (ok si aún no lo añadiste)", Toast.LENGTH_SHORT).show();
        }

        if (paletizarButton != null) {
            paletizarButton.setOnClickListener(v -> paletizarYActualizarPedido());
        }


        id_pedido = getIntent().getIntExtra("id_pedido", 0);
        String pedido = getIntent().getStringExtra("pedido_name");
        paletsDelPedido = getIntent().getIntExtra("palets_del_pedido", 0);

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

    private void confirmarYCambiarEstado(itemProductos item, ImageButton btn) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Cambiar estado?\n\n" + item.getName())
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Aceptar", (d, w) -> ejecutarCambioEstado(item, btn))
                .show();
    }

    private void paletizarYActualizarPedido() {
        if (enPalet.isEmpty()) {
            Toast.makeText(this, "No hay productos en palet para paletizar", Toast.LENGTH_SHORT).show();
            return;
        }

        paletizarButton.setEnabled(false);
        CambiarPaletizadoDto body = new CambiarPaletizadoDto(true);

        List<itemProductos> snapshot = new ArrayList<>(enPalet);

        final int total = snapshot.size();
        final int[] ok = {0};
        final int[] done = {0};

        for (itemProductos item : snapshot) {
            detallesPedidoApi.setPaletizado(item.getIdDetalle(), body)
                    .enqueue(new Callback<DetallePedidoDto>() {
                        @Override
                        public void onResponse(@NonNull Call<DetallePedidoDto> call,
                                               @NonNull Response<DetallePedidoDto> response) {
                            done[0]++;
                            if (response.isSuccessful()) ok[0]++;

                            if (done[0] == total) {
                                if (ok[0] == total) {
                                    //sumar palets del pedido
                                    sumarPaletYComprobarCompletado();
                                } else {
                                    paletizarButton.setEnabled(true);
                                    Toast.makeText(PedidosActivity.this,
                                            "Algunos productos no se pudieron paletizar (" + ok[0] + "/" + total + ")",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DetallePedidoDto> call, @NonNull Throwable t) {
                            done[0]++;
                            if (done[0] == total) {
                                paletizarButton.setEnabled(true);
                                Toast.makeText(PedidosActivity.this,
                                        "Error paletizando: " + t.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sumarPaletYComprobarCompletado() {
        paletsDelPedido = paletsDelPedido + 1;

        pedidosApi.actualizarPalets(id_pedido, new CambiarPaletsPedidoDto(paletsDelPedido))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (!response.isSuccessful()) {
                            paletizarButton.setEnabled(true);
                            Toast.makeText(PedidosActivity.this,
                                    "Error actualizando palets: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // recargar detalles (como ahora ocultamos paletizados, deberían desaparecer)
                        recargarYSiNoQuedanProductosCompletar();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        paletizarButton.setEnabled(true);
                        Toast.makeText(PedidosActivity.this,
                                "Error conexión palets: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void recargarYSiNoQuedanProductosCompletar() {
        pedidosApi.getDetallePedido(id_pedido).enqueue(new Callback<List<DetallePedidoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<DetallePedidoDto>> call,
                                   @NonNull Response<List<DetallePedidoDto>> response) {

                paletizarButton.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PedidosActivity.this,
                            "Error recargando: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // ¿queda algún detalle NO paletizado?
                boolean quedaNoPaletizado = false;
                for (DetallePedidoDto d : response.body()) {
                    if (!d.isPaletizado()) { quedaNoPaletizado = true; break; }
                }

                // refrescar la UI con tu método normal
                cargarDetallePedido(id_pedido);

                if (!quedaNoPaletizado) {
                    // ✅ marcar pedido como completado
                    pedidosApi.actualizarEstado(id_pedido, new CambiarEstadoPedidoDto("Completado"))
                            .enqueue(new Callback<>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                                    if (r.isSuccessful()) {
                                        Toast.makeText(PedidosActivity.this,
                                                "Pedido completado",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PedidosActivity.this,
                                                "No se pudo completar: " + r.code(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                    Toast.makeText(PedidosActivity.this,
                                            "Error completando: " + t.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(PedidosActivity.this,
                            "Paletizado OK. Palets del pedido = " + paletsDelPedido,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DetallePedidoDto>> call, @NonNull Throwable t) {
                paletizarButton.setEnabled(true);
                Toast.makeText(PedidosActivity.this,
                        "Error recargando: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void ejecutarCambioEstado(itemProductos item, ImageButton btn) {

        btn.setEnabled(false);          // desactivar
        btn.setAlpha(0.4f);             // opcional: “gris” visual

        CambiarEstadoProductoDto body = new CambiarEstadoProductoDto(!item.isEstadoProductoPedido());

        detallesPedidoApi.cambiarEstadoProducto(item.getIdDetalle(), body)
                .enqueue(new Callback<DetallePedidoDto>() {

                    @Override
                    public void onResponse(@NonNull Call<DetallePedidoDto> call,
                                           @NonNull Response<DetallePedidoDto> response) {

                        btn.setEnabled(true);     // reactivar
                        btn.setAlpha(1f);

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(PedidosActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean nuevoEstado = response.body().isEstadoProductoPedido();
                        boolean paletizado = response.body().isPaletizado();
                        item.setEstadoProductoPedido(nuevoEstado);

                        // mover entre listas...
                        if(!paletizado){
                            if (nuevoEstado) {
                                pendientes.remove(item);
                                enPalet.add(item);
                            } else {
                                enPalet.remove(item);
                                pendientes.add(item);
                            }
                        }

                        adapterPendientes.notifyDataSetChanged();
                        adapterEnPalet.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NonNull Call<DetallePedidoDto> call,
                                          @NonNull Throwable t) {

                        btn.setEnabled(true);     // reactivar también en fallo
                        btn.setAlpha(1f);

                        Toast.makeText(PedidosActivity.this, "Error conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

                    // IMPORTANTE: si ya está paletizado, NO se muestra
                    if (d.isPaletizado()) continue;

                    int idProducto = d.getIdProducto();

                    itemProductos item = new itemProductos(
                            d.getIdDetalle(),
                            d.getIdProducto(),
                            "Cargando...",
                            String.valueOf(d.getCantidad()),
                            d.isEstadoProductoPedido()
                    );

                    if (d.isEstadoProductoPedido()) enPalet.add(item);
                    else pendientes.add(item);

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
