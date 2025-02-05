package com.example.sqlariketa;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class gehituElementuActivity extends AppCompatActivity {

    private Spinner mySpinnerTool;
    private Spinner mySpinner;
    private ElementuaAdapter adapter;
    private ZerrendaDAO lengoaiaDAO;
    private RecyclerView recyclerView;
    private List<ProgramazioLengoaia> lengoaiak;
    private List<ProgramazioLengoaia> lengoaiakFiltrados;
    private EditText editTextText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gehitu_elementu);


        editTextText = findViewById(R.id.editTextText);


        lengoaiaDAO = new ZerrendaDAO(this);
        lengoaiak = lengoaiaDAO.lortuLengoaiak();
        lengoaiakFiltrados = new ArrayList<>(lengoaiak);




        editTextText.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == android.view.KeyEvent.ACTION_DOWN) {

                    filtArray(editTextText.getText().toString());
                return true;
            }
            return false;
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ElementuaAdapter(lengoaiakFiltrados, new ElementuaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProgramazioLengoaia item) {
                optionAdapter(item);
            }
        });


        recyclerView.setAdapter(adapter);


        mySpinnerTool = findViewById(R.id.spinnerToolbarOptions);
        ArrayAdapter<CharSequence> adapterTool = ArrayAdapter.createFromResource(
                this, R.array.toolbar_options, android.R.layout.simple_spinner_item);
        adapterTool.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinnerTool.setAdapter(adapterTool);

        mySpinnerTool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Intent intent = new Intent(gehituElementuActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        finishAffinity();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Spinner para seleccionar idioma
        mySpinner = findViewById(R.id.spinnerHizkuntza);
        ArrayAdapter<CharSequence> adapterLang = ArrayAdapter.createFromResource(
                this, R.array.languages_array, android.R.layout.simple_spinner_item);
        adapterLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapterLang);

        // Lógica del Spinner de idiomas
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("eu");
                        break;
                    case 2:
                        setLocale("es");
                        break;
                    case 3:
                        setLocale("en");
                        break;
                    default:
                        Log.w(TAG, "onItemSelected: Idioma no soportado");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setLocale(String languageCode) {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentLanguage = preferences.getString("Language", "eu");

        if (!currentLanguage.equals(languageCode)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Language", languageCode);
            editor.apply();

            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            Configuration config = getResources().getConfiguration();
            config.setLocale(locale);

            Resources resources = getResources();
            resources.updateConfiguration(config, resources.getDisplayMetrics());

            recreate();
        }
    }




    private void optionAdapter(ProgramazioLengoaia lengoaia) {
        View dialogView = getLayoutInflater().inflate(R.layout.option_adapter, null);

        Button btnAldatu = dialogView.findViewById(R.id.btnAldatu);
        Button btnEzabatu = dialogView.findViewById(R.id.btnEzabatu);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnAldatu.setOnClickListener(v -> {
            // Al hacer clic, debemos pasar el elemento seleccionado a la actividad de actualización
            Intent intent = new Intent(gehituElementuActivity.this, Update.class);

            // Pasamos el ítem seleccionado al Intent
            intent.putExtra("selectedItem", lengoaia); // Aquí usas el objeto `lengoaia` que es el elemento seleccionado

            // Iniciar la actividad Update
            startActivity(intent);
            finish();
        });


        btnEzabatu.setOnClickListener(v -> {
            lengoaiaDAO.ezabatuLengoaia(Integer.parseInt(lengoaia.getID()));
            recreate();
            Toast.makeText(gehituElementuActivity.this, R.string.Ezabatu, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filtArray(String text) {
        if (text.isEmpty()) {

            lengoaiakFiltrados.clear();
            lengoaiakFiltrados.addAll(lengoaiak);
        } else {

            lengoaiakFiltrados.clear();
            boolean found = false;
            for (ProgramazioLengoaia lengoaia : lengoaiak) {
                if (lengoaia.getIzena().toLowerCase().contains(text.toLowerCase())) {
                    lengoaiakFiltrados.add(lengoaia);
                    found = true;
                }
            }

            if (!found) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }

        adapter.notifyDataSetChanged();
    }





}
