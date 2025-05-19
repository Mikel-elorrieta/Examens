package com.example.mugikorrekokamera;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Erregistratu extends AppCompatActivity {

    EditText etNombre, etApellidos, etCorreo, etUsuario, etContrasena, etDni;
    Button btnRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erregistratu);

        etNombre = findViewById(R.id.editNombre);
        etApellidos = findViewById(R.id.editApellido);
        etCorreo = findViewById(R.id.editEmail);
        etUsuario = findViewById(R.id.editUser);
        etContrasena = findViewById(R.id.editPass);
        etDni = findViewById(R.id.editDni);
        btnRegister = findViewById(R.id.registerButton);


        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String usuario = etUsuario.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String dni = etDni.getText().toString().trim();

            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.registerUser(nombre, apellidos, correo, usuario, contrasena, dni);

            if (success) {
                Toast.makeText(this, "Registro exitoso, ya puedes iniciar sesión", Toast.LENGTH_LONG).show();
                finish(); // vuelve al login
            } else {
                Toast.makeText(this, "El usuario ya existe, prueba con otro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
