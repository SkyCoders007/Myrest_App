package com.mxi.wazooapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.adapter.ReceptsAdapter;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.downloads;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.URL;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyRecepts extends Activity {

    ListView lv_list;
    ImageView iv_back_ls;
    private ProgressDialog pDialog;
    public ArrayList<downloads> downloadList;
    SQLiteWander dbcon;
    CommonClass cc;
    ProgressBar progressBar1;
    GPSTracker gps;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(MyRecepts.this,
                SideMenuActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recepts);

        lv_list = (ListView) findViewById(R.id.lv_list);
        iv_back_ls = (ImageView) findViewById(R.id.iv_back_ls);

        cc = new CommonClass(MyRecepts.this);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        dbcon = new SQLiteWander(MyRecepts.this);
        gps = new GPSTracker(MyRecepts.this);

        iv_back_ls.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        lv_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Log.e("Image Path", downloadList.get(position).path);

                if (downloadList.get(position).redeem.equals("0")) {
                    CheckLocationofUser(downloadList.get(position).lat, downloadList.get(position).lng, downloadList.get(position).offerid
                            , downloadList.get(position).store_name);
                } else {
                    cc.showToast("You already have redeemed this offer");
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + downloadList.get(position).path), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

		/*lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(MyRecepts.this);
				builder.setMessage("Are you sure you want to delete this receipt?")
						.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// FIRE ZE MISSILES!
							}
						})
						.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
								dialog.dismiss();
							}
						});
				// Create the AlertDialog object and return it
				builder.create().show();
				return false;
			}
		});*/


    }

    private void CheckLocationofUser(String lat, String lng, String offerid, String store_name) {
        // TODO Auto-generated method stub
        DecimalFormat df = new DecimalFormat("##.######");
        String current_lat = "0.0", current_lng = "0.0";

        String device_id = Secure.getString(MyRecepts.this.getContentResolver(),
                Secure.ANDROID_ID);
        try {

            lat = df.format(Double.parseDouble(lat));
            lng = df.format(Double.parseDouble(lng));
            current_lat = df.format(gps.getLatitude());
            current_lng = df.format(gps.getLongitude());

            Double dist = distance(Double.parseDouble(lat), Double.parseDouble(lng), Double.parseDouble(current_lat), Double.parseDouble(current_lng), "K");
            //cc.showToast("Match Location with Parner Business:\n"+ "Business Loc. "+lat+", "+lng+"  \nYour Current Loc "+current_lat+", "+current_lng+"  Dist:"+dist);

            // if user is within 0.10KM(100 meter) then send offer to him
            if (dist <= 0.50) {

                if (cc.isConnectingToInternet()) {
                    TakeOfferAsync(MyRecepts.this, device_id, offerid);
                }
            } else {
                cc.showToast("Please visit " + store_name + " and use this receipt to redeem offer");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    //  https://dzone.com/articles/distance-calculation-using-3
    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("K")) {
            dist = dist * 1.609344;
        } else if (unit.equals("N")) {
            dist = dist * 0.8684;
        }
        return (dist);
    }

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


    // Vollay method
    private void TakeOfferAsync(Context context, final String deviceid, final String offerid) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_take_offer_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response url_take_offer", response);

                dbcon.updateReceipt(offerid);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Response", "Error: " + error.getMessage());
                cc.showToast("Oops! No Internet connection");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deviceid", deviceid);
                params.put("offerid", offerid);

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

    @Override
    protected void onResume() {
        super.onResume();

        Cursor c = dbcon.getMyreceipts();
        if (c.getCount() != 0) {
            c.moveToFirst();
            downloadList = new ArrayList<downloads>();
            do {
                downloads data = new downloads();
                data.name = c.getString(9);
                data.path = c.getString(2);
                data.lat = c.getString(3);
                data.lng = c.getString(4);
                data.offerid = c.getString(1);
                data.store_name = c.getString(5);
                data.sub_name = c.getString(6);
                data.price = c.getString(7);
                data.description = c.getString(8);
                data.redeem = c.getString(10);
                downloadList.add(data);

            } while (c.moveToNext());

            ReceptsAdapter adapter = new ReceptsAdapter(getApplicationContext(), 0, downloadList);
            lv_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }


    }
}
