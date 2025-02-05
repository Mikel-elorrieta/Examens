package com.example.sqlariketa;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        TextView txtIzena = findViewById(R.id.editTextIzena);
        TextView txtDeskribapena = findViewById(R.id.editTextDeskribapena);

        Button btnGorde = findViewById(R.id.ButtonGorde);
        Button btnBistaratu = findViewById(R.id.ButtonBistaratu);
        Button bttnMapa = findViewById(R.id.buttonMAPA);

        RadioButton rbSoftwareLibre = findViewById(R.id.radioButtonSoftware);
        bttnMapa.setOnClickListener(v -> {

                    Intent intentMapa = new Intent(this, Mapa.class);
                    startActivity(intentMapa);
                }

        );

        btnGorde.setOnClickListener(v -> {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog);

            dialog.setCancelable(true);

            Button btnCancel = dialog.findViewById(R.id.btnDialogCancel);
            Button btnConfirm = dialog.findViewById(R.id.btnDialogConfirm);

            btnCancel.setOnClickListener(view -> {

                dialog.dismiss();
            });

            btnConfirm.setOnClickListener(view -> {
                dialog.dismiss();


                String izena = txtIzena.getText().toString();
                String deskribapena = txtDeskribapena.getText().toString();

                if (izena.isEmpty() || deskribapena.isEmpty()) {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                    return;
                }

                ZerrendaDAO db = new ZerrendaDAO(this);
                ProgramazioLengoaia lengoaia = new ProgramazioLengoaia(izena, deskribapena, rbSoftwareLibre.isChecked());
                Log.d(TAG, "onCreate: " + lengoaia);
                db.gehituLengoaia(lengoaia);
                Toast.makeText(this, R.string.gordeEginDa, Toast.LENGTH_SHORT).show();
                txtIzena.setText("");
                txtDeskribapena.setText("");
                rbSoftwareLibre.setChecked(false);
            });

            dialog.show();

        });


        btnBistaratu.setOnClickListener(v -> {
            Intent intent = new Intent(this, gehituElementuActivity.class);
            startActivity(intent);
        });

    }
}