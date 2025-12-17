package uvigo.tfgalmacen.mobile_android.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import uvigo.tfgalmacen.mobile_android.models.CambiarEstadoProductoDto;
import uvigo.tfgalmacen.mobile_android.models.CambiarPaletizadoDto;
import uvigo.tfgalmacen.mobile_android.models.DetallePedidoDto;

public interface DetallesPedidoApi {

    @PATCH("/api/detalles-pedido/{idDetalle}/estado-producto")
    Call<DetallePedidoDto> cambiarEstadoProducto(
            @Path("idDetalle") int idDetalle,
            @Body CambiarEstadoProductoDto body
    );


    @PATCH("/api/detalles-pedido/{idDetalle}/paletizado")
    Call<DetallePedidoDto> setPaletizado(
            @Path("idDetalle") int idDetalle,
            @Body CambiarPaletizadoDto body
    );
}
