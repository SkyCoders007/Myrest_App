package com.mxi.wazooapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.adapter.GeneralBusinessAdapter;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.general_bus;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeneralBusiness extends Activity implements OnItemSelectedListener {

    ListView lv_list;
    ImageView iv_back_ls;
    ProgressDialog pDialog;
    Handler handler;

    GPSTracker gps;
    double latitude;
    double longitude;
    CommonClass cc;
    ProgressBar progressBar1;
    public ArrayList<general_bus> generalbusArray;
    Spinner spinner1;
    int i = 0;
    LinearLayout linear_ll_sendrequest;
    Button btn_sendrequest;

    SQLiteWander sqLiteWander;
    StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_business);

        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setEmptyView(findViewById(R.id.empty));
        iv_back_ls = (ImageView) findViewById(R.id.iv_back_ls);

        gps = new GPSTracker(GeneralBusiness.this);
        cc = new CommonClass(GeneralBusiness.this);
        sqLiteWander = new SQLiteWander(GeneralBusiness.this);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        spinner1 = (Spinner) findViewById(R.id.spinner1);

        linear_ll_sendrequest = (LinearLayout) findViewById(R.id.linear_ll_sendrequest);
        btn_sendrequest = (Button) findViewById(R.id.btn_sendrequest);

        iv_back_ls.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });

        // get location of user
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        spinner1.setOnItemSelectedListener(this);

        btn_sendrequest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new getLocation(latitude, longitude).execute();
            }
        });

    }

    // Vollay method
    public void GetAddress(final double latitude, final double longitude, final String type, final String catType) {

        Log.e("------------------", "in GetAddress method---------------------");
        pDialog = new ProgressDialog(GeneralBusiness.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        Log.e("------------------", "after progress dialog call ---------------------");

        AppController.getInstance().getRequestQueue().getCache().clear();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_get_location_info_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                if (cc.isDebug) {
                Log.e("url_get_location_info", response);
//                }
                parseLocationData(response);
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                String message = "";
               /* if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet. \nPlease check your connection!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet.\nPlease check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut!\nPlease check your internet connection.";
                }*/

                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet. \nPlease check your connection!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Oops! something went wrong. \nPlease try again!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Oops! Your Current session has expired.Please try again!";
                }

                showErrorDialog(message);//alertDialog
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("place_lat", gps.latitude + "");
//                params.put("place_long", gps.longitude + "");
                params.put("place_lat", latitude+ "");
                params.put("place_long", longitude + "");
//                params.put("place_lat", 42.259678 + "");
//                params.put("place_long", -83.210633 + "");
                params.put("miles", cc.loadPrefString("miles"));
                params.put("country", SideMenuActivity.country);
                params.put("state", SideMenuActivity.state);
                params.put("type[]", type);
                params.put("type_value[]", catType);

                Log.e("get location info", "params  = " + params.toString());

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
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }

    private void parseLocationData(String response) {
        try {
            JSONObject jObject = new JSONObject(response);
            generalbusArray = null;
            generalbusArray = new ArrayList<general_bus>();

            if (jObject.getString("status").equals("success")) {

                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject2 = jArray.getJSONObject(i);
                    general_bus busi = new general_bus();
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
                    // applied Changes - 1
                    String subcat_id = sqLiteWander.getSubcatId(jObject2.getString("tag_name"));
                    busi.setSubcat_id(Integer.parseInt(subcat_id));
                    int subCatPrefVal = Integer.parseInt(sqLiteWander.getPrefBySubcatId(subcat_id));
                    double distance = Double.parseDouble(busi.getDistance());
                    //formula aaplied to calculate the score
                    double score = subCatPrefVal * (Double.parseDouble(cc.loadPrefString("miles")) - distance);
                    DecimalFormat precision = new DecimalFormat("0.000000");
                    precision.format(score);
                    busi.setScore(score);
                    generalbusArray.add(busi);
//                            Log.e("LocalBusiness...", "generalbusArray [" + i + "] : offername & " + jObject2.getString("name") + "distance = " + jObject2.getString("distance") + " & score = " + score);
                }
                if (cc.isDebug) {
                    Log.e("general_arraylist", generalbusArray.size() + "");
                }
                //convert list in descending order according to score
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

//                        for (int i = 0; i < generalbusArray.size(); i++) {
//                            Log.e("LocalBusiness...", "generalbusArray [" + i + "] : offername & " + generalbusArray.get(i).getName() + "distance = " + generalbusArray.get(i).getDistance() + " & score = " + generalbusArray.get(i).getScore());
//                        }
                //------------>
                GeneralBusinessAdapter adapter = new GeneralBusinessAdapter(GeneralBusiness.this, 0, generalbusArray);
                lv_list.setAdapter(adapter);


            } else {
                generalbusArray.clear();
                GeneralBusinessAdapter adapter = new GeneralBusinessAdapter(GeneralBusiness.this, 0, generalbusArray);
                lv_list.setAdapter(adapter);
                lv_list.setEmptyView(findViewById(R.id.empty));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getCategoryPrefString(String cat_name) {

        String cat_id = sqLiteWander.getLocalCatId(cat_name);


        sb = new StringBuilder();

        boolean is_sb_comma = false;
        Cursor cursor = sqLiteWander.getPrefString(cat_id);

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
        return sb.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gps = new GPSTracker(GeneralBusiness.this);
        if (cc.isConnectingToInternet()) {
            // get location of user

            if (gps.canGetLocation()) {
                latitude = gps.latitude;
                longitude = gps.longitude;
            }

            linear_ll_sendrequest.setVisibility(View.GONE);
            String cat = getCategoryPrefString(spinner1.getSelectedItem().toString());
            Log.e("dhaval", "cat ======= " + cat);
            String type = spinner1.getSelectedItem().toString();
            Log.e("dhaval", "type ======= " + type);
            GetAddress(latitude, longitude, type, cat);
        } else {
            cc.showToast("No internet connection");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class getLocation extends AsyncTask<String, Void, Void> {

        double latitude, longitude;
        String address;

        public getLocation(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.latitude = latitude;
            this.longitude = longitude;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(GeneralBusiness.this);
            pDialog.setMessage("Requesting..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(GeneralBusiness.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (cc.isDebug) {
                Log.e("address_background", address);
            }
            makeJsonReqForBusiness(address);

        }

    }

    private void makeJsonReqForBusiness(final String address) {

        StringRequest jsonObjReq = new StringRequest(Method.POST,
                URL.url_request_business_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (cc.isDebug) {
                    Log.e("url_request_business", response);
                    cc.showToast("Your Request for Business is send to admin");
                }
                pDialog.dismiss();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                cc.showToast("No Internet connection");
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                gps = new GPSTracker(GeneralBusiness.this);
                params.put("latitude", gps.latitude + "");
                params.put("longitude", gps.longitude + "");
                params.put("address", address);
                params.put("category", spinner1.getSelectedItem().toString() + "");

                if (cc.isDebug) {
                    Log.e("url_business", params + "");
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

    public void showErrorDialog(String msg) {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(GeneralBusiness.this);
        alert.setTitle("Wazoo");
        alert.setMessage(msg);
        alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cat = getCategoryPrefString(spinner1.getSelectedItem().toString());
                GetAddress(latitude, longitude, spinner1.getSelectedItem().toString(), cat);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alert.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("@@@Here","goToMainActivity");
//        new LoadMainScreen().execute();
        Intent mIntent = new Intent(GeneralBusiness.this,
                SideMenuActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }
}
