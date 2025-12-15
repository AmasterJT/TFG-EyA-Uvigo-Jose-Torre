package uvigo.tfgalmacen.mobile_android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import uvigo.tfgalmacen.mobile_android.R;

public class PedidosActivity extends AppCompatActivity  {

    TextView codigo_referencia_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Toast.makeText(this, "PedidosActivity abierta", Toast.LENGTH_SHORT).show();

        codigo_referencia_text = findViewById(R.id.codigo_referencia_text);
        if (codigo_referencia_text == null) {
            Toast.makeText(this, "NO encuentro codigo_referencia_text en el layout", Toast.LENGTH_LONG).show();
            return;
        }

        String pedido = getIntent().getStringExtra("pedido_name");
        codigo_referencia_text.setText(pedido != null ? pedido : "(sin pedido_name)");
    }
}