package uvigo.tfgalmacen.mobile_android.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import uvigo.tfgalmacen.mobile_android.R;

public class MainActivity extends AppCompatActivity {

    private TextView nombreUsuarioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // tu layout

        // Referencia al TextView
        nombreUsuarioText = findViewById(R.id.nombre_usuario_text);

        // Obtener datos del Intent
        String nombre = getIntent().getStringExtra("nombre_usuario");
        String apellido = getIntent().getStringExtra("apellido_usuario");

        // Mostrar el nombre completo
        if (nombre != null && apellido != null) {
            nombreUsuarioText.setText(nombre + " " + apellido);
        }
    }
}
