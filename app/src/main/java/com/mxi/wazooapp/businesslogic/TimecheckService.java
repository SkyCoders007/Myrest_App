package com.mxi.wazooapp.businesslogic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.BusinessDialog;
import com.mxi.wazooapp.OfferActivity;
import com.mxi.wazooapp.OfferDialog;
import com.mxi.wazooapp.R;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.general_bus;
import com.mxi.wazooapp.model.offers;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TimecheckService extends Service {
    private ProgressDialog pDialog;

    Handler handler;

    String lat;
    String log;
    int notificationNumber = 0;
    long secondFrequency = 0;

    myRunnable runnable = null;
    Context mcotext;
    CommonClass cc;
    SQLiteWander dbcon;
    GPSTracker gps;
    int i = 0, j = 0;
    String type = "Restaurants";
    ArrayList<general_bus> generalbusArray, tempArraylist;
    // Enter Category in type to get the business
    String cat_pref, cat_id, state, country;
    StringBuilder sb;

    public TimecheckService() {
        Log.e("timecheck", "timecheck service is called");
        handler = new Handler();
    }

    @Override
    public void onCreate() {

        // TODO Auto-generated method stub
        super.onCreate();
        cc = new CommonClass(getBaseContext());
        dbcon = new SQLiteWander(this);
        gps = new GPSTracker(this);
        mcotext = TimecheckService.this;
        defineTypeValues();
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(TimecheckService.this, Locale.getDefault());

        try {
            Log.e("Timecheck service", "addresses = " + geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1));
            addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            if (cc.isDebug) {
                Log.e("Country", country);
            }
            cc.savePrefString("Country", country);

        } catch (Exception e) {
            e.printStackTrace();
        }

        gps = new GPSTracker(this);
        runnable = new myRunnable();

        if (cc.loadPrefBoolean("isCancelFrequence")) {
            secondFrequency = 10;
        } else {
            secondFrequency = 10000;
        }
        handler.postDelayed(runnable, secondFrequency);
        //10000
    }

    public class myRunnable implements Runnable {

        public long getUserFrequency() {
            // get location of user
            if (gps.canGetLocation()) {
                lat = String.valueOf(gps.getLatitude());
                log = String.valueOf(gps.getLongitude());
            }

            if (cc.loadPrefBoolean("Counter")) {
                if (!cc.loadPrefString("Category").equals(type)) {
                    i = 0;
                    cc.savePrefBoolean("Counter", false);
                    dbcon.deleteLocalBusinessPickTable();
                }
            }
            // Apply logic here to change contry wise distance indicator.
            double dist = distance(gps.getLatitude(), gps.getLongitude(), Double.parseDouble(cc.loadPrefString("home_lat")), Double.parseDouble(cc.loadPrefString("home_lng")), cc.loadPrefString("Country"));
            cc.savePrefLong("distance", (long) dist);
            int local_radius = Integer.parseInt(cc.loadPrefString("local_radius"));

            // Checking if user is on Local mode or Travel mode
            long frequency = 0;
            if (dist < local_radius) {
                frequency = cc.loadPrefLong("local_time_freq");
                cc.savePrefString("mode", "local");
            } else {
                frequency = cc.loadPrefLong("travel_time_freq");
                cc.savePrefString("mode", "travel");
            }

            return frequency;

        }

        @Override
        public void run() {
            try {
                long current_millis = System.currentTimeMillis();
                long last_offer_time = Long.parseLong(cc.loadPrefString("last_offer_time"));
                // Below function will executes
//                    frequency = 30000;
                secondFrequency = 10000;
                long time = last_offer_time + getUserFrequency();
                Log.e("Interval*******", getUserFrequency() + "");
                if (cc.loadPrefBoolean("isCancelFrequence")) {
                    time = 0;
                }
                if (cc.loadPrefBoolean("isFirstTime")) {
                    long tempTime = System.currentTimeMillis();
                    time = tempTime + 180000;
                }
                if (current_millis > time) {
                    cc.savePrefString("last_offer_time", System.currentTimeMillis() + "");

                    if (cc.loadPrefBoolean("isoffer")) {
                        if (cc.loadPrefBoolean("isOfferFound")) {
                            Log.e("Timecheck service..", "is device screen off ? in showOffers = " + cc.isScreenOn(TimecheckService.this));
                            ShowOffeers();
                        } else {
                            Log.e("Timecheck service..", "is device screen off ? in noOffers = " + cc.isScreenOn(TimecheckService.this));
                            noOffer();
                        }
                    }
                } else {
                    if (cc.isDebug)
                        Log.d("(Offer will not show)", current_millis + "  > " + last_offer_time + " + " + getUserFrequency());
                    // Continues Running Algo. Nothing happens
                }

                if (cc.loadPrefBoolean("isFirstTime")) {
                    cc.savePrefBoolean("isFirstTime", false);
                    handler.postDelayed(this, 180000);//frequency
                } else {
                    handler.postDelayed(this, getUserFrequency());//frequency
                }
                if (cc.isDebug)
                    Log.d("call after: ", getUserFrequency() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacks(runnable);

    }

    public void resetFrequency() {
        secondFrequency = 10000;
        if (runnable != null) {
            runnable = new myRunnable();
        }
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, secondFrequency);
//        runnable.resetRunnable();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e("Timecheck service", " In the onBind method");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //cc.showToast("Service Start");
        Log.e("Timecheck service", " In the onStartCommand method");
        return super.onStartCommand(intent, flags, startId);
    }

    // Method to stop the service
    public void stopService() {
        onDestroy();
    }

    //http://stackoverflow.com/questions/8486878/how-to-cut-off-decimal-in-java-without-rounding
    public static double roundDown5(double d) {
        return (long) (d * 1e1) / 1e1;
    }

    //		--------------------- changed on 23-09-2016------------------
    public void ShowOffeers() {
        /*return 30*60000;*/
        //	DecimalFormat df = new DecimalFormat("##.###");
        /*String lat = df.format(gps.getLatitude());
		String log = df.format(gps.getLongitude());*/
        if (cc.isDebug)
            Log.e("************", gps.getLatitude() + ",  " + gps.getLongitude());


		/*String lat = String.format("%.2f",Float.parseFloat(lt));
		String log = String.format("%.2f",Float.parseFloat(ln));
*/
        gps = new GPSTracker(TimecheckService.this);
        // get location of user
        if (gps.canGetLocation()) {
            lat = String.valueOf(gps.latitude);
            log = String.valueOf(gps.longitude);
            callWSOffer(lat, log);
        }
    }

    public boolean checkVisitedOffer(int offerID) {
        Cursor cur = dbcon.getVisitedOffers();

        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                int visitedOfferID = Integer.parseInt(cur.getString(0));
                if (visitedOfferID == offerID) {
                    return true;
                }
            } while (cur.moveToNext());
        }
        return false;
    }

    public boolean checkVisitedLocalBusiness(int localBusinessID) {
        Cursor cur = dbcon.getVisitedLocalBusiness();

        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                int visitedLocalBusinessID = Integer.parseInt(cur.getString(0));
                if (visitedLocalBusinessID == localBusinessID) {
                    return true;
                }
            } while (cur.moveToNext());
        }
        return false;
    }

    // Volley method
    private void callWSOffer(final String latitude, final String longitude) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_getoffer_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//				JsonGetData jsonGetData = new JsonGetData();
//				jsonGetData.parseData(getBaseContext(), response);
                ArrayList<offers> tempArray = new ArrayList<offers>();


//                Log.e("offer we respose ==== ", response);
                if (response != null) {
                    try {

                        JSONObject jObject = new JSONObject(response);
                        Log.e("url_getoffer_1", response);

                        if (jObject.getString("status").equals("1")) {

                            JSONObject jOffer = jObject.getJSONObject("offer");

                            JSONArray offerArray = jOffer.getJSONArray("offer_list");
                            for (int i = 0; i < offerArray.length(); i++) {

                                JSONObject jObject2 = offerArray.getJSONObject(i);

                                offers data = new offers();

                                data.setOfferid(jObject2.getString("offerid"));
                                data.setOwnerid(jObject2.getString("ownerid"));
                                data.setName(jObject2.getString("name"));
                                data.setDescriptions(jObject2.getString("descriptions"));
                                data.setDistance(Double.parseDouble(jObject2.getString("distance")));
                                data.setAudio(jObject2.getString("audio"));
                                data.setLat(jObject2.getString("lat"));
                                data.setLng(jObject2.getString("long"));
                                data.setAddress(jObject2.getString("address"));
                                data.setAudio(jObject2.getString("audio"));
                                data.setPrice(jObject2.getString("price"));
                                data.setOfferImage(jObject2.getString("imagename"));
                                data.setSub_name(jObject2.getString("sub_name"));
                                data.setDistance(Double.parseDouble(jObject2.getString("distance")));
                                data.setStore_name(jObject2.getString("store_name").replace("'", ""));
                                // applied changes - 1
                                String subcat_id = jObject2.getString("sub_cat");
                                data.setSub_cat_id(subcat_id);

                                String cat_id = jObject2.getString("cat_id");
                                data.setCat_id(cat_id);
                                int catPref_val;
                                if (cc.loadPrefString("mode").equals("local")) {
                                    catPref_val = Integer.parseInt(dbcon.getLocalPrefByCatId(cat_id));
                                } else {
                                    catPref_val = Integer.parseInt(dbcon.getTravelPrefByCatId(cat_id));
                                }
//                                Log.e("Dhaval...", "catPref_val = " + catPref_val);
                                int subCatPref_val = Integer.parseInt(dbcon.getPrefBySubcatId(subcat_id));
//                                Log.e("Dhaval...", "subCatPref_val = " + subCatPref_val);
                                double distance = data.getDistance();
                                //formula aaplied to calculate the score
                                double score = subCatPref_val * (Double.parseDouble(cc.loadPrefString("partner_miles")) - distance) * catPref_val;
                                DecimalFormat precision = new DecimalFormat("0.000000");
                                precision.format(score);
                                data.setScore(score);

                                if (!jObject2.getString("start_time").equals("")) {
                                    data.setValid("Valid Between : " + jObject2.getString("start_time") + " - " + jObject2.getString("end_time") + " " + jObject2.getString("day"));
                                } else {
                                    data.setValid("");
                                }

                                tempArray.add(data);
                            }
                        }

                        if (tempArray.size() > 0) {
                            Collections.sort(tempArray, new Comparator<offers>() {
                                public int compare(offers obj1, offers obj2) {
                                    // TODO Auto-generated method stub
                                    return (obj1.score < obj2.score) ? -1
                                            : (obj1.score > obj2.score) ? 1 : 0;
                                }
                            });
                            Collections.reverse(tempArray);

                            cc.savePrefBoolean("isOfferFound", true);
                        } else {
                            cc.savePrefBoolean("isOfferFound", false);
                        }

                        offers tc = null;
                        boolean offerFound = false;
                        for (int i = 0; i < tempArray.size(); i++) {
                            tc = tempArray.get(i);
                            if (!checkVisitedOffer(Integer.parseInt(tc.offerid))) {
                                int miles = Integer.parseInt(cc.loadPrefString("partner_miles"));
                                if (tc.getDistance() <= miles) {
                                    Cursor c = dbcon.getMyreceiptsByofferId(tc.getOfferid());
                                    if (c.getCount() == 0) {
                                        offerFound = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (offerFound) {
                            if (cc.isScreenOn(TimecheckService.this)) {
                                Intent mIntent = new Intent(getApplicationContext(), OfferDialog.class);
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mIntent.putExtra("address", tc.address);
                                mIntent.putExtra("price", tc.price);
                                mIntent.putExtra("lat", tc.lat);
                                mIntent.putExtra("long", tc.lng);
                                mIntent.putExtra("store_name", tc.store_name);
                                mIntent.putExtra("descriptions", tc.descriptions);
                                mIntent.putExtra("distance", tc.distance);
                                mIntent.putExtra("offerid", tc.getOfferid());
                                mIntent.putExtra("sub_name", tc.getSub_name());
                                mIntent.putExtra("valid", tc.valid);
                                mIntent.putExtra("offer_image", tc.getOfferImage());
//                            long current_millis = System.currentTimeMillis();
//                            cc.savePrefString("last_offer_time", current_millis + "");
                                dbcon.updatePartnerOffer(Integer.parseInt(tc.offerid));
                                dbcon.insertVisitedOffers(tc.offerid);
                                startActivity(mIntent);
                            } else {
                                Log.e("Timcheck service", "in offer else part");
                                addNotification();
                            }
                        } else {
//                            cc.savePrefBoolean("isOfferFound", false);
                            Bundle bundle = new Bundle();
                            noOffer();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    cc.showToast("No data");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (cc.isDebug) {
                    error.printStackTrace();
                }
                cc.showToast("No Internet connection");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", latitude);
                params.put("long", longitude);
                params.put("miles", cc.loadPrefString("partner_miles"));
                if (cc.isDebug) {
                    Log.e("######", params.toString());
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //  headers.put("user_token", cc.loadPrefString("user_token"));
                headers.put("wazoo-token", cc.loadPrefString("user_token"));
                Log.i("request header", headers.toString());
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }

//    public class Offer {
//        public String address, price, lat, lng, offerid, sub_name, store_name,
//                descriptions, distance, cat_id, sub_cat, valid;
//        public int score_value;
//    }

    //https://dzone.com/articles/distance-calculation-using-3
    public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("India")) {
            dist = dist * 1.609344;
        } else if (unit.equals("N")) {
            dist = dist * 0.8684;
        }
        return (dist);
    }


    //----------------------->
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts decimal degrees to radians             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts radians to decimal degrees             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    // applied changes -1
    private void getCategoryPrefString() {

		/*String cat_id = dbcon.getLocalCatId(cat_name);*/

        sb = new StringBuilder();

        boolean is_sb_comma = false;
        Cursor cursor = dbcon.getPrefString(cat_id);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (is_sb_comma) {
                    sb.append(",");

                }

                sb.append(cursor.getString(0));
                is_sb_comma = true;

            } while (cursor.moveToNext());

            if (cc.isDebug) {
                Log.e("### String ###", sb.toString());
            }
        }

    }

//------------------------------------>


    public void noOffer() {

        tempArraylist = new ArrayList<>();
// applied Changes - 1
        defineTypeValues();
        callWebservice();
//        goToNextListData();
//-------------------------------->

    }

    // below method is used to get the cursor to the next local business pic and call the business Dialog Activity
//    public void goToNextListData() {
//        Cursor cur = dbcon.getLocalBusinessPick();
//
//        if (cur != null && cur.getCount() != 0) {
//            cur.moveToFirst();
//            do {
//                general_bus busi = new general_bus();
//                busi.setBusiness_id(cur.getString(0));
//                busi.setName(cur.getString(1));
//                busi.setTag(cur.getString(2));
//                busi.setDistance(cur.getString(3));
//                busi.setPhone(cur.getString(4));
//                busi.setAddress(cur.getString(5));
//                busi.setPlace_lat(cur.getString(6));
//                busi.setPlace_long(cur.getString(7));
//                busi.setImage(cur.getString(8));
//                busi.setRating(cur.getString(9));
//                busi.setScore(Double.parseDouble(cur.getString(10)));
//                tempArraylist.add(busi);
//            } while (cur.moveToNext());
//        }
//        if (tempArraylist.size() > 0) {
//            Collections.sort(tempArraylist, new Comparator<general_bus>() {
//                public int compare(general_bus obj1, general_bus obj2) {
//                    // TODO Auto-generated method stub
//                    return (obj1.score < obj2.score) ? -1
//                            : (obj1.score > obj2.score) ? 1 : 0;
//                }
//            });
//            Collections.reverse(tempArraylist);
//        }
//
//        try {
//
//            general_bus data = null;
//            boolean isLocalBusinessFound = false;
//            if (j < tempArraylist.size()) {
//                for (int i = j; i < tempArraylist.size(); i++) {
//                    data = tempArraylist.get(i);
//                    if (!checkVisitedLocalBusiness(Integer.parseInt(data.business_id))) {
//                        isLocalBusinessFound = true;
//                        break;
//                    }
//                }
//                if (isLocalBusinessFound) {
//                    if(cc.isScreenOn(TimecheckService.this)){
//                        Intent intent = new Intent(getApplicationContext(), BusinessDialog.class);
//                        intent.putExtra("name", data.getName());
//                        intent.putExtra("tag_name", data.getTag());
//                        intent.putExtra("address", data.getAddress());
//                        intent.putExtra("phone", data.getPhone());
//                        intent.putExtra("image", data.getImage());
//                        intent.putExtra("rating", data.getRating());
//                        intent.putExtra("website", data.getWebsite());
//                        intent.putExtra("place_lat", data.getPlace_lat());
//                        intent.putExtra("place_long", data.getPlace_long());
//                        intent.putExtra("distance", data.getDistance());
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        dbcon.insertVisitedLocalBusiness(data.business_id);
//                        j++;
//                        startActivity(intent);
//                    } else {
//                        addNotification();
//                    }
//                } else {
//                    j = 0;
//                }
//            } else {
//                j = 0;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void callWebservice() {

//applied changes -1`web service is called and data is stored into the Table
        getCategoryPrefString();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_get_location_info_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    Map<String, ArrayList<general_bus>> temp = new HashMap<>();

                    JSONObject jObject = new JSONObject(response);
                    generalbusArray = new ArrayList<general_bus>();
                    if (jObject.getString("status").equals("success")) {
                        dbcon.deleteLocalBusinessPickTable();
                        JSONArray jArray = jObject.getJSONArray("data");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jObject2 = jArray.getJSONObject(i);
                            general_bus busi = new general_bus();
                            busi.setBusiness_id(jObject2.getString("id"));
                            busi.setName(jObject2.getString("name"));
                            busi.setTag(jObject2.getString("tag_name"));
                            busi.setAddress(jObject2.getString("address"));
                            busi.setPhone(jObject2.getString("phone"));
                            busi.setImage(jObject2.getString("image"));
                            busi.setRating(jObject2.getString("rating"));
                            busi.setWebsite(jObject2.getString("website"));
                            busi.setPlace_lat(jObject2.getString("place_lat"));
                            busi.setPlace_long(jObject2.getString("place_long"));
                            busi.setDistance(jObject2.getString("distance"));

                            String subcat_id = dbcon.getSubcatId(jObject2.getString("tag_name"));
                            busi.setSubcat_id(Integer.parseInt(subcat_id));
                            int pref_val = Integer.parseInt(dbcon.getPrefBySubcatId(subcat_id));
                            double distance = Double.parseDouble(busi.getDistance());
                            double score = pref_val * (Double.parseDouble(cc.loadPrefString("miles")) - distance);
                            DecimalFormat precision = new DecimalFormat("0.000000");
                            precision.format(score);
                            busi.setScore(score);
//                            dbcon.insertLocalBusinessPick(Integer.parseInt(busi.getBusiness_id()), busi.getName(), busi.getTag(), busi.getDistance(), busi.getPhone(), busi.getAddress(), busi.getPlace_lat(), busi.getPlace_long(), busi.getImage(), busi.getRating(), busi.getScore());
                            generalbusArray.add(busi);
                        }
                    } else {
                        if (cc.isDebug) {
                            Log.e("No Offer", "No LocalBusinessPick Found");
                        }
                    }

                    if (generalbusArray.size() > 0) {
                        Collections.sort(generalbusArray, new Comparator<general_bus>() {
                            public int compare(general_bus obj1, general_bus obj2) {
                                // TODO Auto-generated method stub
                                return (obj1.score < obj2.score) ? -1
                                        : (obj1.score > obj2.score) ? 1 : 0;
                            }
                        });
                        Collections.reverse(generalbusArray);
                    }

                    try {

                        general_bus data = null;
                        boolean isLocalBusinessFound = false;
                        if (j < generalbusArray.size()) {
                            for (int i = j; i < generalbusArray.size(); i++) {
                                data = generalbusArray.get(i);
                                if (!checkVisitedLocalBusiness(Integer.parseInt(data.business_id))) {
                                    isLocalBusinessFound = true;
                                    break;
                                }
                            }
                            if (isLocalBusinessFound) {
                                if (cc.isScreenOn(TimecheckService.this)) {
                                    Intent intent = new Intent(getApplicationContext(), BusinessDialog.class);
                                    intent.putExtra("name", data.getName());
                                    intent.putExtra("tag_name", data.getTag());
                                    intent.putExtra("address", data.getAddress());
                                    intent.putExtra("phone", data.getPhone());
                                    intent.putExtra("image", data.getImage());
                                    intent.putExtra("rating", data.getRating());
                                    intent.putExtra("website", data.getWebsite());
                                    intent.putExtra("place_lat", data.getPlace_lat());
                                    intent.putExtra("place_long", data.getPlace_long());
                                    intent.putExtra("distance", data.getDistance());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    dbcon.insertVisitedLocalBusiness(data.business_id);
                                    j++;
                                    startActivity(intent);
                                } else {
                                    addNotification();
                                }
                            } else {
                                j = 0;
                            }
                        } else {
                            j = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (cc.isDebug) {
                    cc.showToast("Oops! Somthing went wrong");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                if (gps.canGetLocation()) {
                    params.put("place_lat", gps.getLatitude() + "");
                    params.put("place_long", gps.getLongitude() + "");
                }
                params.put("miles", cc.loadPrefString("miles"));
                params.put("country", cc.loadPrefString("Country"));
                params.put("state", cc.loadPrefString("State"));

                if (type.equals("Restaurants")) {
                    params.put("type[]", "Restaurants");
                    params.put("type_value[]", sb.toString());

                } else if (type.equals("Hotels")) {
                    params.put("type[]", "Hotels");
                    params.put("type_value[]", sb.toString());

                } else if (type.equals("Attractions")) {
                    params.put("type[]", "Attractions");
                    params.put("type_value[]", sb.toString());

                } else if (type.equals("Shopping")) {
                    params.put("type[]", "Shopping");
                    params.put("type_value[]", sb.toString());

                }

                Log.e("localBusiness ", "params = " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //  headers.put("user_token", cc.loadPrefString("user_token"));
                headers.put("wazoo-token", cc.loadPrefString("user_token"));
                Log.i("request header", headers.toString());
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }

    // below method is used for getting the heighest priority business from the database
    public void defineTypeValues() {
        Cursor c2;
        if (cc.loadPrefString("mode").equals("local")) {
            c2 = dbcon.getLocalSetting();
        } else {
            c2 = dbcon.getTravelSetting();
        }

        int temp = 0;

        if (c2 != null && c2.getCount() != 0) {
            c2.moveToLast();
            do {
                try {
                    String temp_Type = c2.getString(2);
                    String value = c2.getString(3);


                    if (Integer.parseInt(value) >= temp) {
                        temp = Integer.parseInt(value);
                        type = temp_Type;
                        cat_id = c2.getString(1);
                        Log.e("Timecheck service", "category = " + type);
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } while (c2.moveToPrevious());
        } else {
            type = "Restaurants";
        }
    }

    private void addNotification() {
        notificationNumber++;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle("Wazoo")
                        .setContentText("You have new wazoo offer .")
                        .setSmallIcon(R.mipmap.ic_launcher);

//        Intent notificationIntent = new Intent(this, SideMenuActivity.class);
        Intent notificationIntent = new Intent(this, OfferActivity.class);
        notificationIntent.putExtra("PENDING_INTENT", "restart_timecheck");
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}