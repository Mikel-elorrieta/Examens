package com.example.mugikorrekokamera;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin, btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUser = findViewById(R.id.usernameEditText);
        etPass = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        btnGoToRegister = findViewById(R.id.toRegister);

        btnLogin.setOnClickListener(v -> {


            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();

            if (!user.isEmpty() && !pass.isEmpty()) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
                finish();
            }
            startActivity(new Intent(Login.this, Menu.class));
        });


        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Erregistratu.class));
        });
    }
}
