package uvigo.tfgalmacen.mobile_android.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import uvigo.tfgalmacen.mobile_android.LoginRequest;
import uvigo.tfgalmacen.mobile_android.LoginResponse;

public interface AuthApi {

    @Headers("Content-Type: application/json")
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
