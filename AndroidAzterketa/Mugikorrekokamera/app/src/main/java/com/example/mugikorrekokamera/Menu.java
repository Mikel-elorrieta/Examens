package com.example.mugikorrekokamera;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Menu extends AppCompatActivity {

    private TextView tvBooking, tvKioskoa, tvCuentaAtras;
    private Button btnActualizar, btnScanQR;
    private MapView mapaIkuspegia;
    private DBHelper dbHelper;

    private int idCola;
    private Handler handler = new Handler();
    private long tiempoRestanteMillis; // en milisegundos

    private static final int PERMISO_NOTIFICACIONES_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);

        // Pedir permiso POST_NOTIFICATIONS en Android 13+ (Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISO_NOTIFICACIONES_CODE);
            }
        }

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_menu);

        inicializarVistas();

        idCola = getIntent().getIntExtra("ID_COLA", -1);

        if (idCola == -1) {
            idCola = obtenerUltimoTurnoInsertado();
        }

        if (idCola != -1) {
            recuperarDatosYMostrar();
        } else {
            mostrarSinCola();
        }

        configurarListeners();
    }

    private void inicializarVistas() {
        tvBooking = findViewById(R.id.tvBooking);
        tvKioskoa = findViewById(R.id.tvKioskoa);
        tvCuentaAtras = findViewById(R.id.tvCuentaAtras);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnScanQR = findViewById(R.id.btnScanQR);
        mapaIkuspegia = findViewById(R.id.mapa);

        mapaIkuspegia.setMultiTouchControls(true);
    }

    private int obtenerUltimoTurnoInsertado() {
        int turno = -1;
        try (Cursor cursor = dbHelper.getUltimaCola()) {
            if (cursor != null && cursor.moveToFirst()) {
                turno = cursor.getInt(cursor.getColumnIndexOrThrow("turno"));
            }
        } catch (Exception e) {

        }
        return turno;
    }

    private void mostrarSinCola() {
        tvBooking.setText("Ez dago ilararik esleiturik");
        tvKioskoa.setText("");
        tvCuentaAtras.setText("");
        mapaIkuspegia.getOverlays().clear();
        mapaIkuspegia.invalidate();
    }

    private void configurarListeners() {
        btnScanQR.setOnClickListener(v -> {
            startActivity(new Intent(Menu.this, QrSkanner.class));
            finish();
        });

        btnActualizar.setOnClickListener(v -> recuperarDatosYMostrar());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_NOTIFICACIONES_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Eskerrik asko, notifikazioak gaituta", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Errorea, permisurik gabe, ez dira iritsiko notifikazioak.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void recuperarDatosYMostrar() {
        try (Cursor colaCursor = dbHelper.getUltimaCola()) {
            if (colaCursor != null && colaCursor.moveToFirst()) {
                int localId = colaCursor.getInt(colaCursor.getColumnIndexOrThrow("local_id"));
                long tiempoEspera = colaCursor.getLong(colaCursor.getColumnIndexOrThrow("tiempo_restante"));

                try (Cursor localCursor = dbHelper.getLocalById(localId)) {
                    if (localCursor != null && localCursor.moveToFirst()) {
                        String booking = localCursor.getString(localCursor.getColumnIndexOrThrow("nombre"));
                        double lat = localCursor.getDouble(localCursor.getColumnIndexOrThrow("latitud"));
                        double lon = localCursor.getDouble(localCursor.getColumnIndexOrThrow("longitud"));

                        tvBooking.setText("Booking: " + booking);
                        tvKioskoa.setText("Kioskoa: " + booking);

                        GeoPoint local = new GeoPoint(lat, lon);
                        mapaIkuspegia.getController().setZoom(15.0);
                        mapaIkuspegia.getController().setCenter(local);
                        mapaIkuspegia.getOverlays().clear();

                        Marker marker = new Marker(mapaIkuspegia);
                        marker.setPosition(local);
                        marker.setTitle("Local asignado");
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mapaIkuspegia.getOverlays().add(marker);
                        mapaIkuspegia.invalidate();

                        iniciarCuentaAtras(tiempoEspera);
                    } else {
                        Toast.makeText(this, "No se encontró el local con ID: " + localId, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "No se encontró ninguna cola.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "recuperarDatosYMostrar: No hay datos en getUltimaCola");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error recuperando datos: " + e.getMessage());
            Toast.makeText(this, "Error recuperando datos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarCuentaAtras(long minutosEspera) {
        tiempoRestanteMillis = minutosEspera * 60 * 1000;

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            lanzarNotificacion();
            tvCuentaAtras.setText("¡Tu turno ha llegado!");
        }, tiempoRestanteMillis);

        actualizarTextoCuentaAtras();
    }

    private void actualizarTextoCuentaAtras() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tiempoRestanteMillis > 0) {
                    tiempoRestanteMillis -= 1000;
                    long minutos = (tiempoRestanteMillis / 1000) / 60;
                    long segundos = (tiempoRestanteMillis / 1000) % 60;
                    tvCuentaAtras.setText(String.format("Espera: %02d:%02d", minutos, segundos));
                    actualizarTextoCuentaAtras();
                }
            }
        }, 1000);
    }

    private void lanzarNotificacion() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String canalId = "canal_turno";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, "Turno Notificaciones", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(canal);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Turno")
                .setContentText("¡Tu turno ha llegado!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
