package com.cedricnoiseux.tp2;

import android.os.AsyncTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by User on 2016-03-22.
 */
public class GetData extends AsyncTask<String, Void, String> {
    private Exception exception;

    protected String doInBackground(String... urls) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://theprintmint-framing.com/tp2/" + urls[0])
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String o) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
