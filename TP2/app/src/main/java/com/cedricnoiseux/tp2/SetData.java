package com.cedricnoiseux.tp2;

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
public class SetData extends AsyncTask<ArrayList<String>, Void, String> {
    private Exception exception;

    protected String doInBackground(ArrayList<String>... args) {
            ArrayList<String> l = args[0];

            String value = l.get(0);
            String fileName = l.get(1);
            System.out.println(value);
            System.out.println(fileName);
            try {

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("texte", value)
                        .add("fichier", fileName)
                        .build();
                Request request = new Request.Builder()
                        .url("http://theprintmint-framing.com/tp2/writefile.php")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println(response.body().string());

            }
            catch (Exception e) {
                e.printStackTrace();
            }
//            File path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_MOVIES);
//            File localFile = new File(path, "/temp.txt");
//            PrintWriter writer = new PrintWriter(localFile, "UTF-8");
//            writer.print(value);
//            writer.close();
//            String remoteFile = url;
//
//
//            f.connect("www.theprintmint-framing.com");
//            f.login("clementgamachepf", "31415926535Pi1");
//            InputStream inputStream = new FileInputStream(localFile);
//            boolean done = f.storeFile(remoteFile, inputStream);
//            if (done) {
//                System.out.println("The first file is uploaded successfully.");
//            }
//            else {
//                System.out.println(f.getReply());
//                System.out.println(f.getReplyString());
//                System.out.println(":(");
//            }
//            localFile.delete();
//            inputStream.close();
//
//        } catch (Exception e) {
//            this.exception = e;
//            System.out.println("didn't work");
//            e.printStackTrace();
//        }
//        try {
//            if (f.isConnected()) {
//                f.logout();
//                f.disconnect();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return null;
    }

    protected void onPostExecute(String s) {
        //Utility.lastData = o;
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
