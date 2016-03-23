package com.cedricnoiseux.tp2;




import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by User on 2016-03-22.
 */
public class Utility {

    public static String lastData = null;


    public static String getAllLines(String url) throws IOException{
        try {
            GetData process = new GetData();
            process.execute(url);
            return process.get();
        }
        catch (Exception e) {
            System.out.println("didn't work");
            return null;
        }
    }

    public static void setAllLines(String url, String value) throws  IOException {

        try {
            ArrayList<String> l= new ArrayList<String>();
            l.add(url);
            l.add(value);
            SetData process = new SetData();
            process.execute(l);
            process.get();
        }
        catch (Exception e) {
        }
    }

}
