package com.itdevstar.sendgeoinfo;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.itdevstar.sendgeoinfo.asynctask.HttpAsyncTask;

import static android.os.SystemClock.sleep;

public class SendService extends Service {

    public static final String TAG = "SendService";

    SharedPreferences prefs = null;
    String uuid = "";
    String latitude = "";
    String longitude = "";

    public LocationManager locationManager;
    public CurPosLocationListener  listener;



    @Override
    public void onCreate() {
        super.onCreate();

        initService();
    }

    private void initService() {
        prefs = getSharedPreferences(SendActivity.SHARED_PREFS_FILE, MODE_PRIVATE);
        uuid = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        monitorFlag();

        handler.postDelayed(runnable, 20000);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new CurPosLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }



    private void monitorFlag() {
        new Thread(new Runnable() {
            @Override
            public void run() {
            while(true) {
                int flag = prefs.getInt(SendActivity.SEND_GEO_FLAG, 0);
                Log.e(TAG, "---- this is recieve broad cast checking");
                if (flag == 1) {
                    SharedPreferences.Editor editor = getSharedPreferences(SendActivity.SHARED_PREFS_FILE, MODE_PRIVATE).edit();
                    editor.putInt(SendActivity.SEND_GEO_FLAG, 0);
                    editor.apply();
                    Log.e(TAG, "---- this is recieve broad cast manual");

                    Intent i = new Intent("com.itdevstar.sendgeoinfo.NEW_MESSAGE");
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", uuid);
                    bundle.putString("latitude", latitude);
                    bundle.putString("longitude", longitude);

                    i.putExtras(bundle);
                    sendBroadcast(i);
                }
                sleep(1000);
            }
            }
        }).start();
    }

    public void sendLocData() {
        if (latitude == null) return;
        if (latitude.length() == 0) return;

        Log.e(TAG, "lat=" + latitude + " lng=" + longitude);
        new HttpAsyncTask(getApplicationContext()).execute(uuid, latitude, longitude);
    }

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            sendLocData();

            Log.e(TAG, "---- this is response ========================================== ");
            handler.postDelayed(this, 20000);
        }
    };

    public class CurPosLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Location curLoc = loc;
            latitude = Double.toString(curLoc.getLatitude());
            longitude = Double.toString(curLoc.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
