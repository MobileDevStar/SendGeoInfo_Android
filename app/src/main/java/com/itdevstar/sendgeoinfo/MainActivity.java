package com.itdevstar.sendgeoinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    String[] appPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int PERMISSION_REQUEST_CODE = 1240;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude, longitude, android_id;
    protected boolean gps_enabled, network_enabled;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkAndRequestPermissions()) {
            initApp();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();

            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0) {
                initApp();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void initApp() {
        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void sendData() {

    }

    public void showResponseMessage(String response) {
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
    }

    public void showErrorMessage() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }

    public boolean checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        txtLat = (TextView) findViewById(R.id.textview1);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());

        latitude = Double.toString(location.getLatitude());
        longitude = Double.toString(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

}