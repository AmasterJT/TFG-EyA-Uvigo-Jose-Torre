package uvigo.tfgalmacen.mobile_android.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uvigo.tfgalmacen.mobile_android.R;
import uvigo.tfgalmacen.mobile_android.api.ApiClient;
import uvigo.tfgalmacen.mobile_android.api.PaletsApi;
import uvigo.tfgalmacen.mobile_android.models.PaletDto;

public class EditPaletActivity extends AppCompatActivity {

    // Combo (AutoComplete)
    private AutoCompleteTextView comboPalet;

    // Inputs (TextInputLayout) para poder acceder a getEditText()
    private TextInputLayout tilAlto, tilAncho, tilProfundo, tilCantidad;

    // TextViews
    private TextView tvCantidadOriginal, tvNuevaCantidad;

    // API
    private PaletsApi paletsApi;

    // Datos en memoria
    private final List<PaletDto> palets = new ArrayList<>();
    private final Map<Integer, PaletDto> paletById = new HashMap<>();

    // Estado actual
    private int cantidadOriginal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_palet);

        // 1) Referencias UI
        comboPalet = findViewById(R.id.spinner_estado); // tu AutoCompleteTextView

        tilAlto = findViewById(R.id.modificar_alto_text);
        tilAncho = findViewById(R.id.modificar_ancho_text);
        tilProfundo = findViewById(R.id.modificar_profundo_text);
        tilCantidad = findViewById(R.id.modificar_cantidad_text);

        tvCantidadOriginal = findViewById(R.id.cantidad_original);
        tvNuevaCantidad = findViewById(R.id.nueva_cantidad);

        // 2) API
        paletsApi = ApiClient.getClient().create(PaletsApi.class);

        // 3) Cargar palets y rellenar combo
        cargarPalets();

        // 4) Listener: seleccionar de la lista
        comboPalet.setOnItemClickListener((parent, view, position, id) -> {
            String texto = (String) parent.getItemAtPosition(position);
            aplicarPaletPorTexto(texto);
        });

        // 5) Listener: si el usuario escribe a mano un id y sale del campo
        comboPalet.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                aplicarPaletPorTexto(comboPalet.getText() != null ? comboPalet.getText().toString() : "");
            }
        });

        // 6) Calcular nueva cantidad en tiempo real
        if (tilCantidad.getEditText() != null) {
            tilCantidad.getEditText().addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    recalcularNuevaCantidad();
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void cargarPalets() {
        paletsApi.getPalets().enqueue(new Callback<List<PaletDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PaletDto>> call,
                                   @NonNull Response<List<PaletDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EditPaletActivity.this,
                            "Error palets: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                palets.clear();
                paletById.clear();

                ArrayList<String> ids = new ArrayList<>();

                for (PaletDto p : response.body()) {
                    palets.add(p);
                    paletById.put(p.getIdPalet(), p);
                    ids.add(String.valueOf(p.getIdPalet()));

                }

                // Rellenar el combo
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                        EditPaletActivity.this,
                        android.R.layout.simple_list_item_1,
                        ids
                );

                comboPalet.setAdapter(adapter);
                comboPalet.setThreshold(0);

                // para que al pulsar se abra el dropdown
                comboPalet.setOnClickListener(v -> comboPalet.showDropDown());
            }

            @Override
            public void onFailure(@NonNull Call<List<PaletDto>> call, @NonNull Throwable t) {
                Toast.makeText(EditPaletActivity.this,
                        "Error conexión palets: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarPaletPorTexto(String texto) {
        if (texto == null) texto = "";
        texto = texto.trim();

        if (texto.isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Id palet inválido: " + texto, Toast.LENGTH_SHORT).show();
            return;
        }

        PaletDto palet = paletById.get(id);
        if (palet == null) {
            Toast.makeText(this, "No existe palet con id=" + id, Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) Poner hints de dimensiones (editables)
        setHint(tilAlto, String.valueOf(palet.getAlto()));
        setHint(tilAncho, String.valueOf(palet.getAncho()));
        setHint(tilProfundo, String.valueOf(palet.getProfundo()));

        // (opcional) limpiar lo que hubiese escrito el usuario
        clearText(tilAlto);
        clearText(tilAncho);
        clearText(tilProfundo);

        // 2) Cantidad original
        cantidadOriginal = palet.getCantidadDeProducto();
        tvCantidadOriginal.setText(String.valueOf(cantidadOriginal));

        // 3) Reset cantidad modificada a vacío (o 0) y recalcular
        clearText(tilCantidad);
        recalcularNuevaCantidad();
    }

    private void recalcularNuevaCantidad() {
        int restar = 0;

        if (tilCantidad.getEditText() != null) {
            String s = tilCantidad.getEditText().getText() != null ? tilCantidad.getEditText().getText().toString().trim() : "";
            if (!s.isEmpty()) {
                try {
                    restar = Integer.parseInt(s);
                } catch (NumberFormatException ignored) {
                    restar = 0;
                }
            }
        }

        int nueva = cantidadOriginal - restar;

        // no puede ser negativa
        if (nueva < 0) {
            nueva = 0;
            // opcional: mostrar error si el usuario intenta restar más de lo disponible
            tilCantidad.setError("No puede ser mayor que " + cantidadOriginal);
        } else {
            tilCantidad.setError(null);
        }

        tvNuevaCantidad.setText(String.valueOf(nueva));
    }

    private void setHint(TextInputLayout til, String hint) {
        if (til != null && til.getEditText() != null) {
            til.getEditText().setHint(hint);
        }
    }

    private void clearText(TextInputLayout til) {
        if (til != null && til.getEditText() != null) {
            til.getEditText().setText("");
        }
    }
}
