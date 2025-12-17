package uvigo.tfgalmacen.mobile_android.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import uvigo.tfgalmacen.mobile_android.models.PaletDto;

public interface PaletsApi {

    @GET("/api/palets")
    Call<List<PaletDto>> getPalets();
}
