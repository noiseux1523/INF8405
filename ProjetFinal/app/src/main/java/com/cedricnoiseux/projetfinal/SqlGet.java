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
import java.util.LinkedList;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by User on 2016-03-22.
 */
public class SqlGet extends AsyncTask<String, Void, LinkedList<Event>> {
    private Exception exception;

    protected LinkedList<Event> doInBackground(String... args) {

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("username", SqlUtility.userName)
                    .add("password", SqlUtility.password)
                    .add("query", args[0])
                    .build();
            Request request = new Request.Builder()
                    .url("http://theprintmint-framing.com/tp3/SqlGet.php")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String output = response.body().string();
            System.out.println(output);
            return SqlUtility.phpStringToEvents(output);

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    protected  void onPostExecute(LinkedList<Event> l) {
        //Utility.lastData = o;
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
