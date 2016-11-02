package mx.peta.hellomap.servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by rayo on 10/29/16.
 * Developer information in https://developer.android.com/index.html -> DEVELP -> API Guides
 */

public class ServicioGPS extends Service implements LocationListener{

    private Context ctx;

    double latitud;
    double longitud;
    Location location;
    boolean gpsActivo = false;
    TextView texto;
    LocationManager locationManager;

    public ServicioGPS() {
        super();
        this.ctx = this.getApplicationContext();
    }

    public ServicioGPS(Context c) {
        super();
        this.ctx = c;
        getLocation();
    }

    public void setView(View v) {
        texto = (TextView) v;
        texto.setText("Coordenadas:" + latitud + ", " + longitud);
    }

    public LatLong getLatLong() {
        LatLong latLong = new LatLong();

        latLong.setLatitud(this.latitud);
        latLong.setLongitud(this.longitud);
        return latLong;
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) this.ctx.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception e) {}

        if (gpsActivo) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                    500,            // cada cuantos milisegundos se actualiza la posición
                    10,             // cada cuantos metros de actualiza la posición
                    this);
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            locationManager.removeUpdates(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
