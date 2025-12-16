package uvigo.tfgalmacen.mobile_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uvigo.tfgalmacen.mobile_android.R;

public class SelectOptionsActivity extends AppCompatActivity {

    private TextView nombreUsuarioText;
    private ImageButton edit_palet_button;
    private ImageButton show_pedidos;

    private String nombre;
    private String apellido;

    private int id_usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_options); // tu layout

        // Referencia al TextView
        nombreUsuarioText = findViewById(R.id.nombre_usuario_text);
        edit_palet_button = findViewById(R.id.edit_palet_button);
        show_pedidos = findViewById(R.id.show_pedidos);

        // Obtener datos del Intent
        nombre = getIntent().getStringExtra("nombre_usuario");
        apellido = getIntent().getStringExtra("apellido_usuario");
        id_usuario = getIntent().getIntExtra("id_usuario", 0);

        // Mostrar el nombre completo
        if (nombre != null && apellido != null) {
            nombreUsuarioText.setText(nombre + " " + apellido);
        }

        show_pedidos.setOnClickListener(v -> abrirPedidos());
        edit_palet_button.setOnClickListener(v -> editarPalet());

    }

    private void editarPalet() {


        // Crear Intent para abrir SelectOptionsActivity
        Intent intent = new Intent(getApplicationContext(), EditPaletActivity.class);


        // Iniciar nueva Activity
        startActivity(intent);
    }

    private void abrirPedidos() {


        // Crear Intent para abrir SelectOptionsActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        // Pasar los datos al siguiente Activity
        intent.putExtra("nombre_usuario", nombre);
        intent.putExtra("apellido_usuario", apellido);
        intent.putExtra("id_usuario", id_usuario);

        // Iniciar nueva Activity
        startActivity(intent);
    }
}
