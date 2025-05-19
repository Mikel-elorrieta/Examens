package com.example.mugikorrekokamera;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Random;

public class QrSkanner extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView imageView;
    private Button btnTakePicture;
    private Uri imageUri;
    private DBHelper dbHelper;
    private int idUsuario = 1; // Este ID debería venir del usuario logueado
    private int idLocal = 1; // Este ID debería obtenerse del QR escaneado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_skanner);

        imageView = findViewById(R.id.qrScannerPreview);
        btnTakePicture = findViewById(R.id.btnAQR);
        dbHelper = new DBHelper(this);

        btnTakePicture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la cámara");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);

                // Simulación lectura QR
                idLocal = new Random().nextInt(16) + 1; // ID local entre 1 y 16
                int tiempoRestante = new Random().nextInt(1) + 1; // minutos

// Nota: mejor no asignar turno aleatorio, debería ser autoincremental en DB, así que eliminamos esto
                 int turno = new Random().nextInt(300);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("turno", turno);
                values.put("usuario_id", idUsuario);
                values.put("local_id", idLocal);
                values.put("tiempo_restante", tiempoRestante);
                values.put("estado", "esperando");

// Insertar y recuperar el ID autogenerado (clave primaria)
                long idCola = db.insert("colas", null, values);

                if (idCola != -1) {
                    // Aquí idCola es el ID real del registro insertado
                    Toast.makeText(this, "Te has unido a la cola. ID de cola: " + idCola + ", tiempo de espera: " + tiempoRestante + " minutos", Toast.LENGTH_LONG).show();

                    // Ahora puedes lanzar la siguiente actividad pasando este idCola
                    Intent intent = new Intent(this, Menu.class);
                    intent.putExtra("ID_COLA", (int)idCola);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error al unirse a la cola", Toast.LENGTH_SHORT).show();
                }


                db.close();

            } catch (IOException e) {
                Log.e("QRActivity", "Error al cargar imagen", e);
            }
        }
    }
}
