package uvigo.tfgalmacen.mobile_android.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import uvigo.tfgalmacen.mobile_android.models.CambiarEstadoPedidoDto;
import uvigo.tfgalmacen.mobile_android.models.CambiarPaletsPedidoDto;
import uvigo.tfgalmacen.mobile_android.models.DetallePedidoDto;
import uvigo.tfgalmacen.mobile_android.models.PedidoDto;

public interface PedidosApi {

    @GET("/api/pedidos/en-proceso/usuario/{idUsuario}")
    Call<List<PedidoDto>> getPedidosEnProceso(@Path("idUsuario") int idUsuario);

    @GET("/api/pedidos/{idPedido}/detalles")
    Call<List<DetallePedidoDto>> getDetallePedido(@Path("idPedido") int idPedido);


    @PATCH("/api/pedidos/{idPedido}/palets")
    Call<Void> actualizarPalets(
            @Path("idPedido") int idPedido,
            @Body CambiarPaletsPedidoDto body
    );

    @PATCH("/api/pedidos/{idPedido}/estado")
    Call<Void> actualizarEstado(
            @Path("idPedido") int idPedido,
            @Body CambiarEstadoPedidoDto body
    );
}
