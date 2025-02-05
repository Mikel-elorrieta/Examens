package com.example.sqlariketa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Update extends AppCompatActivity {

    private ProgramazioLengoaia selectedItem; // El item seleccionado
    private EditText editTextIzena2, editTextDeskribapena2;
    private ZerrendaDAO lengoaiaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Recuperar el objeto seleccionado
        selectedItem = (ProgramazioLengoaia) getIntent().getSerializableExtra("selectedItem");

        // Inicializar los campos
        editTextIzena2 = findViewById(R.id.editTextIzena2);
        editTextDeskribapena2 = findViewById(R.id.editTextDeskribapena2);
        Button btnGuardar = findViewById(R.id.buttonAldatu);

        // Si hay un ítem seleccionado, mostrar sus datos
        if (selectedItem != null) {
            editTextIzena2.setText(selectedItem.getIzena());
            editTextDeskribapena2.setText(selectedItem.getDeskribapena());
        }

        // Configurar el botón de guardar
        btnGuardar.setOnClickListener(v -> {
            // Obtener los valores editados
            String nuevaIzena = editTextIzena2.getText().toString();
            String nuevaDeskribapena = editTextDeskribapena2.getText().toString();

            // Actualizar los datos en el objeto
            selectedItem.setIzena(nuevaIzena);
            selectedItem.setDeskribapena(nuevaDeskribapena);

            // Crear instancia de DAO
            lengoaiaDAO = new ZerrendaDAO(Update.this);

            // Actualizar los datos en la base de datos
            int resultado = lengoaiaDAO.eguneratuLengoaia(Integer.parseInt(selectedItem.getID()), selectedItem);

            // Verificar si la actualización fue exitosa
            if (resultado > 0) {
                Toast.makeText(Update.this, "Elemento actualizado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Update.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cerrar la actividad y volver a la anterior
            } else {
                Toast.makeText(Update.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
