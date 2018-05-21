package com.mxi.wazooapp.network;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by android3 on 17/10/16.
 */
public class HttpHelper {
    String url = "";
    public String getDirectionsUrl(LatLng origin, LatLng dest, String country){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        if(country.equals("India")){
            // Building the url to the web service
            // Output format
            String output = "json?mode=driving&";
//            String output = "json?key=AIzaSyCJVmJFp_veXNgPWMNNmzir1i5MYehxZbM&mode=driving&";
//            String output = "json?units=imperial&mode=driving&";
            url = "https://maps.googleapis.com/maps/api/directions/"+output+""+parameters;
        }else{
            // Building the url to the web service
            // Output format
            String output = "json?units=imperial&mode=driving&";
            url = "https://maps.googleapis.com/maps/api/directions/"+output+""+parameters;
        }

        return url;
    }

    /** A method to download json data from url */
    public String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}