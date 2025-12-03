package uvigo.tfgalmacen.mobile_android.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import uvigo.tfgalmacen.mobile_android.R;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends AppCompatActivity {

    private TextView nombre_usuario_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // tu layout


        Toast.makeText(MainActivity.this,
                "Creando el MainActivity",
                Toast.LENGTH_SHORT
        ).show();


        // Referencia al TextView
        nombre_usuario_text = findViewById(R.id.nombre_usuario_text);

        // Obtener datos del Intent
        String nombre = getIntent().getStringExtra("nombre_usuario");
        String apellido = getIntent().getStringExtra("apellido_usuario");

        // Mostrar el nombre completo
        if (nombre != null && apellido != null) {
            String text = nombre + " " + apellido;
            nombre_usuario_text.setText(text);
        }
    }

}