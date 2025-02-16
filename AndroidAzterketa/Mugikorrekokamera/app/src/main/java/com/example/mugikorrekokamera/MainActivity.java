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

import android.nfc.NfcAdapter;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;

public class MainActivity extends AppCompatActivity {

    private ImageView imgAurreikuspena;
    private Uri argazkiUri;
    private String egungoArgazkiBidea;
    private Button btnArgazkiaAtera;
    private Button NfcButton;
    private Button WifiButton;
    private Button BluetoothButton;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnArgazkiaAtera = findViewById(R.id.btnKamera);
        imgAurreikuspena = findViewById(R.id.imgAurreikuspena);
        NfcButton = findViewById(R.id.btnNFC);
        WifiButton = findViewById(R.id.btnWifi);
        BluetoothButton = findViewById(R.id.btnBluethooth);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        imgAurreikuspena.setImageURI(argazkiUri);
                        Toast.makeText(this, "¡Foto guardada con éxito!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Se canceló la captura de la foto.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }

        btnArgazkiaAtera.setOnClickListener(view -> kameraZabaldu());

        NfcButton.setOnClickListener(view ->
                Toast.makeText(this, nfcBegiratu(this), Toast.LENGTH_SHORT).show()
        );

        WifiButton.setOnClickListener(view ->
                Toast.makeText(this, wifiBegiratu(this), Toast.LENGTH_SHORT).show()
        );

        BluetoothButton.setOnClickListener(view ->
                Toast.makeText(this, bluetoothBegiratu(this), Toast.LENGTH_SHORT).show()
        );
    }


    // NFC
    public static String nfcBegiratu(Context context) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null) {
            return "Ez dago NFC modulurik";
        } else if (nfcAdapter.isEnabled()) {
            return "NFC modulua aktibatuta dago";
        } else {
            return "NFC modulua ez dago aktibatuta";
        }
    }

    // WiFi
    public static String wifiBegiratu(Context context){
        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            return "Wifi aktibatuta dago";
        } else {
            return "Wifi ez dago aktibatuta";
        }
    }

    // Bluetooth
    public static String bluetoothBegiratu(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return "Ez dago Bluetooth modulurik";
        } else if (bluetoothAdapter.isEnabled()) {
            return "Bluetooth modulua aktibatuta dago";
        } else {
            return "Bluetooth modulua ez dago aktibatuta";
        }
    }

    // Kamera
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

    // Kamera
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
