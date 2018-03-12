package edu.temple.mapchatapp;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nmale_000 on 3/12/2018.
 */

public class RegisterLocation extends AsyncTask {
    public RegisterLocation(){

    }

    //TODO scrap this and use volley?


    @Override
    protected Object doInBackground(Object[] objects) {
        String urlString = (String)objects[0].toString(); // URL to call

        String data = (String)objects[1]; //data to post

        OutputStream out = null;
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            writer.write(data);

            writer.flush();

            writer.close();

            out.close();

            urlConnection.connect();


        } catch (Exception e) {

            System.out.println(e.getMessage());



        }


    }
}
