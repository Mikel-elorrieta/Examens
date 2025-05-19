package com.example.mugikorrekokamera;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class QrSkanner extends AppCompatActivity {
    private ImageView imgAurreikuspena;
    private Uri argazkiUri;
    private String egungoArgazkiBidea;

    private Button btnArgazkiaAtera;
    private ActivityResultLauncher<Intent> cameraLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_skanner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button btnArgazkiaAtera = findViewById(R.id.btnAQR);



            btnArgazkiaAtera.setOnClickListener(v1 -> {
                kameraZabaldu();
            });
            return insets;





        });

        // Configurar resultado de la cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (imgAurreikuspena != null && argazkiUri != null) {
                            imgAurreikuspena.setImageURI(argazkiUri);
                        }
                        Toast.makeText(this, "¡ONDO!", Toast.LENGTH_SHORT).show();

                        // Generar ID random para la cola/ticket (ejemplo 1-1000)
                        int idRandom = new Random().nextInt(16) + 1;

                        // Pasar ID y URI a la siguiente pantalla Menu
                        Intent intent = new Intent(QrSkanner.this, Menu.class);
                        intent.putExtra("ID_RANDOM", idRandom);
                        intent.putExtra("URI_FOTO", argazkiUri.toString());
                        startActivity(intent);


                    } else {
                        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        );



        // Comprobar permisos
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }

    }

    public long crearCola(int idLocal, int turnoAleatorio, int idUsuario) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("local_id", idLocal);              // ID del local
        values.put("usuario_id", idUsuario);          // ID del usuario
        values.put("turno", turnoAleatorio);          // Turno generado aleatoriamente
        values.put("tiempo_restante", 10);            // Por defecto: 10 minutos
        values.put("estado", "esperando");            // Estado inicial

        long resultado = db.insert("colas", null, values);
        db.close();
        return resultado; // Devuelve -1 si falla
    }


    private void kameraZabaldu() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (isCameraAvailable()) {
            File argazkiFitxategia = null;
            try {
                argazkiFitxategia = sortuIrudiFitxategia();
            } catch (IOException e) {
                Log.e("Kamera", "Error al crear el archivo de imagen", e);
                Toast.makeText(this, "Error al preparar el archivo para guardar la foto", Toast.LENGTH_SHORT).show();
                return;
            }

            if (argazkiFitxategia != null) {
                argazkiUri = FileProvider.getUriForFile(this, "com.example.mugikorrekokamera.fileprovider", argazkiFitxategia);
                Log.d("Kamera", "URI de la imagen: " + argazkiUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, argazkiUri);
                cameraLauncher.launch(intent);
            } else {
                Toast.makeText(this, "No se pudo crear el archivo para la imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCameraAvailable() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private File sortuIrudiFitxategia() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fitxategiIzena = "JPEG_" + timestamp + "_";
        File direktorioa = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fitxategiIzena, ".jpg", direktorioa);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Kamera baimena eman da!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Kamera baimena beharrezkoa da!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}