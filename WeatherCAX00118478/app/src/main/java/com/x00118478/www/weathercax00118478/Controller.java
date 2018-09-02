package com.x00118478.www.weathercax00118478;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Controller {

    //Retrieve the Network information from the device.
    public static NetworkInfo retrieveNetworkInfo(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectionManager.getActiveNetworkInfo();
    }

    //Check if their is any internet connection.
    public static boolean isDeviceConnected(Context context) {
        NetworkInfo networkinfo = Controller.retrieveNetworkInfo(context);
        return (networkinfo != null && networkinfo.isConnected());
    }

    public static String retrieveData(String urlTarget) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            //Create connection
            url = new URL(urlTarget);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("content-type", "application/json;  charset=utf-8");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);

            InputStream inputStream;
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getErrorStream();
            } else {
                inputStream = urlConnection.getInputStream();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuffer responseLine = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                responseLine.append(line);
                responseLine.append('\r');
            }
            bufferedReader.close();
            return responseLine.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
