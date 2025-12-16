package uvigo.tfgalmacen.mobile_android.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import uvigo.tfgalmacen.mobile_android.models.ProductoIdentificadorDto;

public interface ProductosApi {

    @GET("/api/productos/identificador/{idProducto}")
    Call<ProductoIdentificadorDto> getIdentificador(@Path("idProducto") int idProducto);
}
