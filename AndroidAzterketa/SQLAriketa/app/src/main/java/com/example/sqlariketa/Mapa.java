package com.example.sqlariketa;

import static com.example.sqlariketa.R.*;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Mapa extends AppCompatActivity {

    private MapView mapaIkuspegia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(layout.activity_mapa);


        mapaIkuspegia = findViewById(id.mapa);
        mapaIkuspegia.setMultiTouchControls(true);

        // Coordenadas de Elorrieta
        GeoPoint elorrieta = new GeoPoint(43.2842987, -2.9669894);
        mapaIkuspegia.getController().setZoom(15.0);
        mapaIkuspegia.getController().setCenter(elorrieta);

        // Primer marcador: Elorrieta-Errekamari LHII
        Marker txintxeta = new Marker(mapaIkuspegia);
        txintxeta.setPosition(elorrieta);
        txintxeta.setTitle("Elorrieta-Errekamari LHII");
        mapaIkuspegia.getOverlays().add(txintxeta);

        // Coordenadas de otro lugar (PEDRO)
        GeoPoint elorrieta2 = new GeoPoint(43.3135, -2.9750894);

        // Segundo marcador: PEDRO
        Marker txintxeta2 = new Marker(mapaIkuspegia);
        txintxeta2.setPosition(elorrieta2);
        txintxeta2.setTitle("PEDRO");
        mapaIkuspegia.getOverlays().add(txintxeta2);

        // Coordenadas de las estaciones de metro

        // Estación Metro Bilbao (Línea 1)
        GeoPoint metroBilbao = new GeoPoint(43.263833, -2.933309);
        Marker markerBilbao = new Marker(mapaIkuspegia);
        markerBilbao.setPosition(metroBilbao);
        markerBilbao.setTitle("Estación Metro Bilbao");
        mapaIkuspegia.getOverlays().add(markerBilbao);

        // Estación Metro Madrid (Línea 10)
        GeoPoint metroMadrid = new GeoPoint(40.416775, -3.703790);
        Marker markerMadrid = new Marker(mapaIkuspegia);
        markerMadrid.setPosition(metroMadrid);
        markerMadrid.setTitle("Estación Metro Madrid");
        mapaIkuspegia.getOverlays().add(markerMadrid);
    }
}
