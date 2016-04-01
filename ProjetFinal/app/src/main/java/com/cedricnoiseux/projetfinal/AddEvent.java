package com.cedricnoiseux.projetfinal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;


import com.squareup.okhttp.FormEncodingBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.net.URLEncoder;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by User on 2016-03-22.
 */
public class AddEvent extends AsyncTask<Event, Void, String> {
    private Exception exception;

    protected String doInBackground(Event... args) {
        Event e = args[0];

        try {

            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("username", SqlUtility.userName)
                    .add("password", SqlUtility.password)
                    .add("name", e.name)
                    .add("host", e.host)
                    .add("locationName", e.locationName)
                    .add("x", Float.valueOf(e.locX).toString())
                    .add("y", Float.valueOf(e.locY).toString())
                    .build();
            Request request = new Request.Builder()
                    .url("http://theprintmint-framing.com/tp3/AddEvent.php")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(String s) {
        //Utility.lastData = o;
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
