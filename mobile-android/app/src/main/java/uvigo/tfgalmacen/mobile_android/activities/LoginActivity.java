package uvigo.tfgalmacen.mobile_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uvigo.tfgalmacen.mobile_android.LoginRequest;
import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.api.ApiClient;
import uvigo.tfgalmacen.mobile_android.api.AuthApi;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // tu XML

        // 1. Referencias a la UI
        usernameEditText = findViewById(R.id.username_text);
        passwordEditText = findViewById(R.id.password_text);
        loginButton = findViewById(R.id.login_btn);

        // 2. Crear instancia de la API
        authApi = ApiClient.getClient().create(AuthApi.class);

        // 3. Listener del botón
        loginButton.setOnClickListener(v -> hacerLogin());
    }

    private void hacerLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Opcional: desactivar botón mientras se hace la petición
        loginButton.setEnabled(false);
        loginButton.setText("Entrando...");

        LoginRequest request = new LoginRequest(username, password);

        Call<LoginResponse> call = authApi.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("Iniciar sesión");

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,
                            "Error del servidor: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponse loginResponse = response.body();
                if (loginResponse == null) {
                    Toast.makeText(LoginActivity.this,
                            "Respuesta vacía del servidor",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (loginResponse.isOk()) {

                    Toast.makeText(LoginActivity.this,
                            "Login OK, abriendo MainActivity...",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Obtener los datos del usuario desde la respuesta
                    String nombre = loginResponse.getNombre();        // nombre que te da la API
                    String apellido1 = loginResponse.getApellido1();  // si lo tienes en la API

                    // Crear Intent para abrir MainActivity
                    Intent intent = new Intent(String.valueOf(MainActivity.class));

                    // Pasar los datos al siguiente Activity
                    intent.putExtra("nombre_usuario", nombre);
                    intent.putExtra("apellido_usuario", apellido1);

                    // Iniciar nueva Activity
                    startActivity(intent);

                    // Cerrar la pantalla de login para no volver atrás
                    finish();
                } else {
                    // Usuario/contraseña incorrectos u otro error lógico
                    Toast.makeText(LoginActivity.this,
                            loginResponse.getMessage() != null
                                    ? loginResponse.getMessage()
                                    : "Login incorrecto",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Iniciar sesión");
                Toast.makeText(LoginActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
