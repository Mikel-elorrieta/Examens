package com.example.mugikorrekokamera;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Erregistratu extends AppCompatActivity {

        EditText izena, abizena, emaila, erabiltzailea, pasahitza, nana;
        Button btnErregistratu;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_erregistratu);

            izena = findViewById(R.id.editNombre);
            abizena = findViewById(R.id.editApellido);
            emaila = findViewById(R.id.editEmail);
            erabiltzailea = findViewById(R.id.editUser);
            pasahitza = findViewById(R.id.editPass);
            nana = findViewById(R.id.editDni);
            btnErregistratu = findViewById(R.id.registerButton);

            btnErregistratu.setOnClickListener(v -> {
                // Aquí podrías validar y guardar en SharedPreferences, DB, etc.
                startActivity(new Intent(Erregistratu.this, Login.class));
                Toast.makeText(this, "Erregistratua!", Toast.LENGTH_SHORT).show();
            });
        }
    }
