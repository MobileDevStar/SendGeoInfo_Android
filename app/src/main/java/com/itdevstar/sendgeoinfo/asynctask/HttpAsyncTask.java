package com.itdevstar.sendgeoinfo.asynctask;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.itdevstar.sendgeoinfo.endpoint.EndPointUrl;
import com.itdevstar.sendgeoinfo.endpoint.EndpointUrlProvider;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpAsyncTask extends AsyncTask<String, Void, Response> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/form-data; charset=utf-8");

    private static final String TAG = "HttpAsyncTask";

    public HttpAsyncTask() {

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
        String postBody="";

        postBody = "uuid=" + uuid + "&lat=" + lat + "&lng=" + lng;

        EndPointUrl endPointUrl = EndpointUrlProvider.getDefaultEndPointUrl();
        String url = endPointUrl.getUrl();

        try{
            //Create OkHttpClient for sending request
            OkHttpClient client = new OkHttpClient();
            //Create the request body with the help of Media Type
            RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            //Send the request
            Response response = client.newCall(request).execute();
            return response;
        }catch (IOException exception){
            exception.printStackTrace();
            return null;
        }

        /*JSONObject json = params[0];

        OkHttpClient client = new OkHttpClient();

        EndPointUrl endPointUrl = EndpointUrlProvider.getDefaultEndPointUrl();
        String url = endPointUrl.getUrl();


        Log.e(TAG, "..................Sent Http..................." + url);

        RequestBody body = RequestBody.create(JSON, json.toString());


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Log.e(TAG, "..................Sent Http...................");
        Log.e(TAG, request.method());
        Log.e(TAG, body.toString());
        Log.e(TAG, request.toString());
        Log.e(TAG, json.toString());

        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
    }

    @Override
    protected void onPostExecute(Response response) {
        Log.e(TAG, "..................Receive Http Response...................");
        if (response != null && response.isSuccessful()) {
            try {
                String responseData = response.body().string();
                Log.e(TAG, "..................Response success...................");
                Log.e(TAG, responseData);
            } catch (IOException e) {
                Log.e(TAG, "..................Response body to string error...................");
            }
        } else {
            Log.e(TAG, "..................Response error...................");
        }
    }
}
