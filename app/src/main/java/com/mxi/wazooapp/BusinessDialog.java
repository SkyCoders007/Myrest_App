package com.mxi.wazooapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.JsonGetData;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by parth on 22/8/16.
 */
//this class is used to display the Local Business Pick Activity
public class BusinessDialog extends Activity {

    GPSTracker gps;
    LinearLayout dialog_ll_direction, LinearLayout1;
    CommonClass cc;
    MediaPlayer mp;
    String audio="";
    String name,tag_name,address,phone,image,rating,website,place_lat,place_long,distance;
   // SQLiteWander dbcon;
    LinearLayout sv_offer_view;
    public TextView genbus_tv_name, genbus_tv_address,genbus_tv_tag,genbus_tv_distance,genbus_tv_phone;
    public ImageView genbus_iv_image;
    public RatingBar genbus_rb_rating;
    public LinearLayout row_ll_genbusin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getWindow().getAttributes().windowAnimations = R.style.Animation;
        int width = (metrics.widthPixels / 1);
        int height = (metrics.heightPixels / 3);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_business_dialog);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        //setContentView(R.layout.activity_business_dialog);
//        dbcon = new SQLiteWander(getApplicationContext());
        cc = new CommonClass(getApplicationContext());
        gps = new GPSTracker(BusinessDialog.this);
        dialog_ll_direction = (LinearLayout) findViewById(R.id.dialog_ll_busi_direction);
        LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);

        genbus_tv_name = (TextView)findViewById(R.id.genbus_tv_name);
        genbus_tv_address = (TextView)findViewById(R.id.genbus_tv_address);
        genbus_tv_tag = (TextView)findViewById(R.id.genbus_tv_tag);
        genbus_tv_distance = (TextView)findViewById(R.id.genbus_tv_dist);
        genbus_tv_phone = (TextView)findViewById(R.id.genbus_tv_phone);
        genbus_iv_image = (ImageView)findViewById(R.id.genbus_iv_pop_image);
        genbus_rb_rating = (RatingBar)findViewById(R.id.genbus_rb_rating);
        row_ll_genbusin = (LinearLayout)findViewById(R.id.row_ll_genbusin);

        Intent mIntent = getIntent();
        name = mIntent.getStringExtra("name");
        tag_name = mIntent.getStringExtra("tag_name");
        address = mIntent.getStringExtra("address");
        phone = mIntent.getStringExtra("phone");
        image = mIntent.getStringExtra("image");
        rating = mIntent.getStringExtra("rating");
        website = mIntent.getStringExtra("website");
        place_lat = mIntent.getStringExtra("place_lat");
        place_long = mIntent.getStringExtra("place_long");
        distance = mIntent.getStringExtra("distance");

        genbus_tv_name.setText(name);
        genbus_tv_address.setText(address);
        genbus_tv_tag.setText(tag_name);

        double dist= Double.parseDouble(distance);
        DecimalFormat precision = new DecimalFormat("0.00");
        distance=precision.format(dist);
    //changes applied - 1 condition for contrywise distance

        if(cc.loadPrefString("Country").equals("India")){

            dist=dist * 1.609344;

            distance=precision.format(dist)+" km away";
        }else{
            distance= precision.format(dist)+" m away";
        }
        genbus_tv_distance.setText(distance);
        genbus_tv_phone.setText(phone+"");

        mp = new MediaPlayer();
        if (cc.loadPrefBoolean("isaudio")) {
            if (audio.equals("")) {
                try {
                    mp = MediaPlayer.create(BusinessDialog.this, R.raw.iphonenoti);
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cc.isDebug) {
                        Log.e("MediaPlayer", e.getMessage());
                    }
                }
            } else {

                new AsyncMusic().execute();

            }
        }


        if (!image.equals("")) {
            Picasso.with(getApplicationContext())
                    .load(image)
                    .error(R.drawable.ni_image)
                    .placeholder(R.drawable.ni_image)
                    .into(genbus_iv_image);
        }else {
            genbus_iv_image.setImageResource(R.drawable.ni_image);
        }

        if (!rating.equals("")) {
            genbus_rb_rating.setRating(Float.parseFloat(rating));
        }else {
            genbus_rb_rating.setRating(0);
        }
        dialog_ll_direction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {

                    boolean isAppInstalled =  cc.appInstalledOrNot(BusinessDialog.this, "com.google.android.apps.maps");
                    Log.e("Dhaval ", "google map android app is installe = " + isAppInstalled);

                    if(isAppInstalled){
                        Intent directioIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr="+ place_lat + "," + place_long));
                        directioIntent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        startActivity(directioIntent);
                    } else {
                        String baseURL = "http://maps.google.com/maps?";
                        String fromLocation = "saddr=" + gps.getLatitude() + "," + gps.getLongitude();
                        String toLocation = "&daddr=" + place_lat + "," + place_long;
                        String mapUrl = baseURL + fromLocation + toLocation;
                        Log.e("Dhaval ", "Map URL = " + mapUrl);

                        Intent mapIntent = new Intent(BusinessDialog.this, ShowMapActivity.class);
                        mapIntent.putExtra("MAP_URL", mapUrl);
                        startActivity(mapIntent);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (cc.isDebug) {
                        Log.e("MAP Error", e.getMessage());
                    }
                }
            }
        });

        row_ll_genbusin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean isAppInstalled =  cc.appInstalledOrNot(BusinessDialog.this, "com.google.android.apps.maps");
                    Log.e("Dhaval ", "google map android app is installe = " + isAppInstalled);

                    if(isAppInstalled){
                        Intent directioIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr="+ place_lat + "," + place_long));
                        directioIntent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        startActivity(directioIntent);
                    } else {
                        String baseURL = "http://maps.google.com/maps?";
                        String fromLocation = "saddr=" + gps.getLatitude() + "," + gps.getLongitude();
                        String toLocation = "&daddr=" + place_lat + "," + place_long;
                        String mapUrl = baseURL + fromLocation + toLocation;
                        Log.e("Dhaval ", "Map URL = " + mapUrl);

                        Intent mapIntent = new Intent(BusinessDialog.this, ShowMapActivity.class);
                        mapIntent.putExtra("MAP_URL", mapUrl);
                        startActivity(mapIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cc.isDebug) {
                        Log.e("MAP Error", e.getMessage());
                    }
                }
            }
        });

/*
		Implemented on 26-09-2016
		after 5 seconds dialog will automatically close
 */
        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                finish();
            }
        }, 10000);
    }
    // offer dialog sound Async Class
    public class AsyncMusic extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            // media player object
            mp = new MediaPlayer();

            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (cc.isDebug) {
                Log.e("Backgroung", "Background");
            }
            try {
                // set media player data
                mp.setDataSource(audio);
            } catch (IllegalArgumentException e) {
                // Toast.makeText(getApplicationContext(),
                // "You might not set the URI correctly!",Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                // Toast.makeText(getApplicationContext(),"You might not set the URI correctly!",Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                // Toast.makeText(getApplicationContext(),"You might not set the URI correctly!",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mp.prepare();
            } catch (IllegalStateException e) {
                // Toast.makeText(getApplicationContext(),"You might not set the URI correctly!",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // Toast.makeText(getApplicationContext(),"You might not set the URI correctly!",Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Start media player offer dialog sound
            mp.start();

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            mp.stop();
            mp.release();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            mp.release();
        }
    }
}
