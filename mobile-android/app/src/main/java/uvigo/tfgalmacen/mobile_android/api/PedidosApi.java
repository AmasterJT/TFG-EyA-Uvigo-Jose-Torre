package uvigo.tfgalmacen.mobile_android.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import uvigo.tfgalmacen.mobile_android.models.PedidoDto;

public interface PedidosApi {

    @GET("/api/pedidos/en-proceso/usuario/{idUsuario}")
    Call<List<PedidoDto>> getPedidosEnProceso(@Path("idUsuario") int idUsuario);
}
