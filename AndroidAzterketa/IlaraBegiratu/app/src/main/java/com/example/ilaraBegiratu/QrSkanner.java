package com.example.ilaraBegiratu;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Random;

public class QrSkanner extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView imageView;
    private Button btnTakePicture;
    private Uri imageUri;
    private DBHelper dbHelper;
    private int idUsuario;


    private int idLocal = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ilaraBegiratu.R.layout.activity_qr_skanner);

        imageView = findViewById(R.id.qrScannerPreview);
        btnTakePicture = findViewById(R.id.btnAQR);
        dbHelper = new DBHelper(this);

        idUsuario = dbHelper.getLoggedInUser();

        btnTakePicture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openQRScanner();
               // openCamera();
            }
        });
    }
    private void openQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Eskaneatu QR kodea");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "QR Code");
        values.put(MediaStore.Images.Media.DESCRIPTION, "QR Code File");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                try {
                    idLocal = Integer.parseInt(result.getContents()); // QR code must contain an integer
                    int tiempoRestante = new Random().nextInt(1) + 1;
                    int turno = new Random().nextInt(300);

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("turno", turno);
                    values.put("usuario_id", idUsuario);
                    values.put("local_id", idLocal);
                    values.put("tiempo_restante", tiempoRestante);
                    values.put("estado", "esperando");

                    long idCola = db.insert("colas", null, values);

                    if (idCola != -1) {
                        Toast.makeText(this, "Ilara bat gehitu zara. Ilara ID: " + idCola + ", itxaron denbora: " + tiempoRestante + " minutu", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, Menu.class);
                        intent.putExtra("ID_COLA", (int) idCola);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Errorea: ezin izan da ilara sortu.", Toast.LENGTH_SHORT).show();
                        recreate();
                    }

                    db.close();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "QR-ak ez du baliozko ID bat. Saiatu berriro.", Toast.LENGTH_SHORT).show();
                    recreate();
                }
            } else {
                Toast.makeText(this, "QR irakurketa bertan behera utzi da.", Toast.LENGTH_SHORT).show();
                recreate();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
