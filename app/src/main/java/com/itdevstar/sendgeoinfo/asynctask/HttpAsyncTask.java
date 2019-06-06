package com.itdevstar.sendgeoinfo.asynctask;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpAsyncTask extends AsyncTask<String, Void, Response> {

    private static final String TAG = "HttpAsyncTask";

    private Context context;

    public HttpAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "..................Send Http Start...................");
    }

    @Override
    protected Response doInBackground(String... params) {
        Log.e(TAG, "..................Sending Http...................");

        String uuid = params[0];
        String lat = params[1];
        String lng = params[2];

        String url = "https://digifli.com/mobile/api/";

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uuid", uuid)
                .addFormDataPart("lat", lat)
                .addFormDataPart("lng", lng)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        Log.e(TAG, "..................Receive Http Response...................");
        if (response != null && response.isSuccessful()) {
            try {
                String responseData = response.body().string();
                Log.e(TAG, "..................Response success...................");
                Log.e(TAG, responseData);
                Toast.makeText(context, responseData, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, "Response body to string error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "..................Response body to string error...................");
            }
        } else {
            Log.e(TAG, "..................Response error...................");
            Toast.makeText(context, "Response error", Toast.LENGTH_SHORT).show();
        }
    }
}
