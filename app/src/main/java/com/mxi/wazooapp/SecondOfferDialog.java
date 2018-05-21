package com.mxi.wazooapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.JsonGetData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondOfferDialog extends Activity {

    LinearLayout ll_second;
    CommonClass cc;
    String name, address, lat, lng, audio, price, descriptions, deviceid, offerid, store_name, sub_name, valid = "";
    TextView tv_offer_price, tv_descriptions, tv_company_name,
            offer_tv_address, offer_tv_date, tv_valid;
    MediaPlayer mp;
    SQLiteWander dbcon;
    GPSTracker gps;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_dialog);

        ll_second = (LinearLayout) findViewById(R.id.ll_second);
        cc = new CommonClass(getApplicationContext());
        dbcon = new SQLiteWander(getApplicationContext());
        gps = new GPSTracker(SecondOfferDialog.this);
        Intent mIntent = getIntent();


        address = mIntent.getStringExtra("address");
        lat = mIntent.getStringExtra("lat");
        lng = mIntent.getStringExtra("long");
        price = mIntent.getStringExtra("price");
        descriptions = mIntent.getStringExtra("descriptions");
        offerid = mIntent.getStringExtra("offerid");
        store_name = mIntent.getStringExtra("store_name");
        sub_name = mIntent.getStringExtra("sub_name");
        valid = mIntent.getStringExtra("valid");

        tv_offer_price = (TextView) findViewById(R.id.tv_offer_price);
        tv_descriptions = (TextView) findViewById(R.id.tv_descriptions);
        tv_company_name = (TextView) findViewById(R.id.tv_company_name);
        offer_tv_address = (TextView) findViewById(R.id.offer_tv_address);
        offer_tv_date = (TextView) findViewById(R.id.offer_tv_date);
        tv_valid = (TextView) findViewById(R.id.tv_valid);

        tv_offer_price.setText(cc.loadPrefString("getCurrency") + "" + price);
        tv_descriptions.setText(descriptions);
        tv_company_name.setText("@" + store_name);
        offer_tv_address.setText(address);
        offer_tv_date.setText(getCurrentDate());
        tv_valid.setText(valid);

        deviceid = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        Cursor c = dbcon.getMyreceiptsByofferId(offerid);
        if (c.getCount() == 0) {
            SaveOfferReceipt();
        } else {
            cc.showToast("You already have a receipt for this offer");
            onBackPressed();
        }
    }

    private void SaveOfferReceipt() {
        // TODO Auto-generated method stub
        try {
            final String MEDIA_PATH = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Wazoo Offer/";

            Bitmap b = generatebitmap(ll_second);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(
                    "MMM_dd_yy_hh_mm_ss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String s = dateFormatter.format(today);


            String filename = store_name + "_" + s + ".png";
            Log.e("file name", filename + "");

            dbcon.insertMyReceipts(offerid, MEDIA_PATH + "" + filename, lat, lng, store_name, sub_name, price, descriptions, filename, "0");
            new ImageSave(b, filename).execute();

            cc.showToast("Your receipt for " + descriptions + " @" + store_name + " has been saved in your My Receipt page. Please present it to redeem offer.");
            // SecondOfferDialog.this.setFinishOnTouchOutside(true);

            finish();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Bitmap generatebitmap(View cluster) {
        // Generating Bitmaps

        cluster.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        cluster.layout(0, 0, cluster.getMeasuredWidth(), cluster.getMeasuredHeight());

        final Bitmap clusterBitmap = Bitmap.createBitmap(cluster.getMeasuredWidth(),
                cluster.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(clusterBitmap);
        cluster.draw(canvas);

        return clusterBitmap;

    }

    // store image into mobile
    private String storeImage(Bitmap b, String filename) {

        String filePath;

        // get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory()
                + "/Wazoo Offer";
        File sdIconStorageDir = new File(iconsStoragePath);

        // create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(
                    fileOutputStream);

            // choose another format if PNG doesn't suit you
            b.compress(CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return filePath;
    }


    public class ImageSave extends AsyncTask<String, Void, Void> {

        private String filename;
        private Bitmap b;

        public ImageSave(Bitmap b, String filename) {
            // TODO Auto-generated constructor stub
            this.b = b;
            this.filename = filename;
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {

                storeImage(b, filename);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//			cc.showToast("Save offer image in sd card");


        }
    }

    public String getCurrentDate() {
        //yyyyMMdd_HHmmss
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            if (CommonClass.isShowMap) {
                boolean isAppInstalled = cc.appInstalledOrNot(SecondOfferDialog.this, "com.google.android.apps.maps");
                Log.e("Dhaval ", "google map android app is installe = " + isAppInstalled);

                if (isAppInstalled) {
                    Intent directioIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lng));
                    directioIntent.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity");
                    startActivity(directioIntent);
                } else {
                    String baseURL = "http://maps.google.com/maps?";
                    String fromLocation = "saddr=" + gps.getLatitude() + "," + gps.getLongitude();
                    String toLocation = "&daddr=" + lat + "," + lng;
                    String mapUrl = baseURL + fromLocation + toLocation;
                    Log.e("Dhaval ", "Map URL = " + mapUrl);

                    Intent mapIntent = new Intent(SecondOfferDialog.this, ShowMapActivity.class);
                    mapIntent.putExtra("MAP_URL", mapUrl);
                    CommonClass.isShowMap = false;
                    startActivity(mapIntent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (cc.isDebug) {
                Log.e("MAP Error", e.getMessage());
            }
        }
    }
}
