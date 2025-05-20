package com.example.mugikorrekokamera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin, btnGoToRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.usernameEditText);
        etPass = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        btnGoToRegister = findViewById(R.id.toRegister);

        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(v -> {
            boolean valido = validarUsuario(etUser.getText().toString(), etPass.getText().toString());
            if (valido) {
                startActivity(new Intent(this, Menu.class));

                finish();
            } else {
                Toast.makeText(this, "Erabiltzaile edo pasahitza txarto daude.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Erregistratu.class));
        });
    }

    private boolean validarUsuario(String username, String password) {
        return dbHelper.login(username, password);
    }
}