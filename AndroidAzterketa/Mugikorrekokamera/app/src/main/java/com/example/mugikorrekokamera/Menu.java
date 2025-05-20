package com.example.mugikorrekokamera;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
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

    private TextView tvlokala, tvKioskoa, tvTiempoEstimado, tvTurno;
    private Button  btnScanQR, dejarCola;
    private MapView mapaIkuspegia;
    private DBHelper dbHelper;
    private int idCola;
    private Handler handler = new Handler();
    private long tiempoRestanteMillis;

    private static final int PERMISO_NOTIFICACIONES_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);

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

        if (idCola != -1) {
            recuperarDatosYMostrar();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(1); // Cancelar la notificación si se entra desde ella
        } else {
            mostrarSinCola();
        }

        configurarListeners();
    }

    private void inicializarVistas() {
        tvlokala = findViewById(R.id.tvBooking);
        tvKioskoa = findViewById(R.id.tvKioskoa);
        btnScanQR = findViewById(R.id.btnScanQR);
        mapaIkuspegia = findViewById(R.id.mapa);
        tvTurno = findViewById(R.id.tvTurno);
        dejarCola = findViewById(R.id.btnDejarCola);
        tvTiempoEstimado = findViewById(R.id.tvEsperaEstimado);
        mapaIkuspegia.setMultiTouchControls(true);
    }

    private void dejarCola() {
        dbHelper.eliminarCola(idCola);
        Toast.makeText(this, "Ilara utzi da", Toast.LENGTH_SHORT).show();
        cancelarCuentaAtras();
        mostrarSinCola();
    }

    private void mostrarSinCola() {
        tvlokala.setText("Ez dago ilararik esleiturik");
        tvKioskoa.setText("");
        tvTurno.setText("");
        tvTiempoEstimado.setText("");
        mapaIkuspegia.getOverlays().clear();
        mapaIkuspegia.getController().setZoom(2.0);
        mapaIkuspegia.invalidate();
    }

    private void configurarListeners() {
        btnScanQR.setOnClickListener(v -> {
            startActivity(new Intent(Menu.this, QrSkanner.class));
            finish();
        });
        dejarCola.setOnClickListener(v -> {
            dejarCola();
        });
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
                String turno = colaCursor.getString(colaCursor.getColumnIndexOrThrow("turno"));
                String estado = colaCursor.getString(colaCursor.getColumnIndexOrThrow("estado"));

                try (Cursor localCursor = dbHelper.getLocalById(localId)) {
                    if (localCursor != null && localCursor.moveToFirst()) {
                        String lokala = localCursor.getString(localCursor.getColumnIndexOrThrow("nombre"));
                        double lat = localCursor.getDouble(localCursor.getColumnIndexOrThrow("latitud"));
                        double lon = localCursor.getDouble(localCursor.getColumnIndexOrThrow("longitud"));
                        String descripcion = localCursor.getString(localCursor.getColumnIndexOrThrow("descripcion_eu"));

                        tvlokala.setText("Lokala: " + lokala);
                        tvKioskoa.setText("Deskripzioa: " + descripcion);
                        tvTurno.setText("Txanda: " + turno);
                        GeoPoint local = new GeoPoint(lat, lon);
                        mapaIkuspegia.getController().setZoom(15.0);
                        mapaIkuspegia.getController().setCenter(local);
                        mapaIkuspegia.getOverlays().clear();

                        Marker marker = new Marker(mapaIkuspegia);
                        marker.setPosition(local);
                        marker.setTitle(lokala);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mapaIkuspegia.getOverlays().add(marker);
                        mapaIkuspegia.invalidate();

                        if ("atendido".equalsIgnoreCase(estado)) {
                            tvTiempoEstimado.setText("Zure txanda iritsi da!");

                            new AlertDialog.Builder(this)
                                    .setTitle("Avisoa")
                                    .setMessage("Zure txanda iritsi da. Joateko prest zaude?")
                                    .setPositiveButton("Bai", null)
                                    .show();
                        } else {
                            tvTiempoEstimado.setText("Itxarote-denbora: "  + tiempoEspera + " minutu gutxi gorabehera");
                            iniciarCuentaAtras(tiempoEspera, lokala, descripcion, turno);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ez da aurkitu lokalerik: " + e.getMessage());
            Toast.makeText(this, "Errorea datuak berreskuratzean", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarCuentaAtras(long minutosEspera, String lokala, String descripcion, String turno) {
        tiempoRestanteMillis = minutosEspera * 60 * 1000;

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            lanzarNotificacion(lokala, descripcion, turno);
            tvTiempoEstimado.setText("Zure txanda iritsi da!");
        }, tiempoRestanteMillis);
    }

    private void cancelarCuentaAtras() {
        handler.removeCallbacksAndMessages(null);
        tvTiempoEstimado.setText("");
    }

    private void lanzarNotificacion(String lokala, String descripcion, String turno) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String canalId = "canal_turno";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, "TXANDA", NotificationManager.IMPORTANCE_HIGH);
            canal.setDescription("Txanda iritsi da");
            notificationManager.createNotificationChannel(canal);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        dbHelper.marcarColaComoAtendida(this.idCola);

        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("ID_COLA", this.idCola);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("¡TXANDA!")
                .setContentText("Zure txanda iritsi da\n Hemen: " + lokala + "\n Deskripzioa: " + descripcion + "\n Txanda: " + turno)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }
}
