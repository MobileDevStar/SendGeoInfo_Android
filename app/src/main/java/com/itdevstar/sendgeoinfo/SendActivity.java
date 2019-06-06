package com.itdevstar.sendgeoinfo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itdevstar.sendgeoinfo.asynctask.HttpAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendActivity extends AppCompatActivity {

    public static final String TAG = "SendGeoInfo";

    public static final String SHARED_PREFS_FILE = "SendGeoInfo";

    public static final String START_SERVICE_FLAG = "service_start";
    public static final String SEND_GEO_FLAG = "send_data";

    String[] appPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int PERMISSION_REQUEST_CODE = 1240;

    private LocationMessage messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageReceiver = new LocationMessage();
        registerReceiver(messageReceiver, new IntentFilter("com.itdevstar.sendgeoinfo.NEW_MESSAGE"));

        if (checkAndRequestPermissions()) {
            initApp();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }

    private void initApp() {

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        String restoredText = prefs.getString(START_SERVICE_FLAG, null);
        if (restoredText != null) {
            Log.e(TAG, "---- activity send request");
            sendData();
        } else {
            Log.e(TAG, "---- this is start service");
            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE).edit();
            editor.putString(START_SERVICE_FLAG, "started");
            editor.apply();

            Intent intent = new Intent(this, SendService.class);
            startService(intent);
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


    private void sendData()
    {
        Log.e(TAG, "---- this is send data to service");
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE).edit();
        editor.putInt(SEND_GEO_FLAG, 1);
        editor.apply();
    }


    public class LocationMessage extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("com.itdevstar.sendgeoinfo.NEW_MESSAGE")){
                Bundle extra = intent.getExtras();
                String uuid = extra.getString("uuid");
                String latitude = extra.getString("latitude");
                String longitude = extra.getString("longitude");

                Log.e(TAG, "lat=" + latitude + " lng=" + longitude);
                Toast.makeText(getApplicationContext(), uuid + ":" + latitude + ":" + longitude, Toast.LENGTH_SHORT).show();

                sendLocDataToServer(uuid, latitude, longitude);
            }
        }

        private void sendLocDataToServer(String uuid, String latitude, String longitude) {
            new HttpAsyncTask(getApplicationContext()).execute(uuid, latitude, longitude);
        }
    }
}
