package com.itdevstar.sendgeoinfo.asynctask;

import java.io.IOException;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.itdevstar.sendgeoinfo.MainActivity;
import com.itdevstar.sendgeoinfo.R;
import com.itdevstar.sendgeoinfo.endpoint.EndPointUrl;
import com.itdevstar.sendgeoinfo.endpoint.EndpointUrlProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpAsyncTask extends AsyncTask<Void, Void, Response> {

    private ProgressDialog progressDialog;

    MainActivity mActivity;

    public HttpAsyncTask(MainActivity activity) {
        mActivity = activity;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.getting_data));
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected Response doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();

        EndPointUrl endPointUrl = EndpointUrlProvider.getDefaultEndPointUrl();
        String url = endPointUrl.getUrl();

        Request request = new Request.Builder()
                .url(url).build();

        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response != null && response.isSuccessful()) {
            try {
                String responseData = response.body().string();
                mActivity.showResponseMessage(responseData);
            } catch (IOException e) {
                mActivity.showErrorMessage();
            }
        } else {
            mActivity.showErrorMessage();
        }
        progressDialog.dismiss();
    }
}
