package com.example.mugikorrekokamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Menu extends AppCompatActivity {

    private ImageView imgAurreikuspena;
    private Uri argazkiUri;
    private String egungoArgazkiBidea;
    private Button btnArgazkiaAtera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Inicializar vistas
        setContentView(R.layout.activity_menu);
        btnArgazkiaAtera = findViewById(R.id.btnScanQR);


        // Evento click

        btnArgazkiaAtera.setOnClickListener(v -> {
            startActivity(new Intent(Menu.this, QrSkanner.class));
        });


    }



}
