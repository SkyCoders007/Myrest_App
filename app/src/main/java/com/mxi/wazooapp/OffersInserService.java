package com.mxi.wazooapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.offers;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class OffersInserService extends Service {

	Handler handler;
	GPSTracker gps;
	Runnable runnable;
	double latitude;
	double longitude;
	CommonClass cc;
	SQLiteWander dbcon;
	String lat;
	String log;

	public OffersInserService() {
		// TODO Auto-generated constructor stub
		handler = new Handler();
		Log.e("Offers service call", "offers servioce call");

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		gps = new GPSTracker(OffersInserService.this);
		cc = new CommonClass(getApplicationContext());
		dbcon = new SQLiteWander(getApplicationContext());
		// continues call on every 10 min.
		 runnable = new Runnable() {

			@SuppressWarnings("unused")
			@Override
			public void run() {
				
				try {
					// get location of user
					if (gps.canGetLocation()) {

						DecimalFormat df = new DecimalFormat("##.######");
						lat = df.format(gps.getLatitude());
						latitude = Double.parseDouble(lat);
						
						log = df.format(gps.getLongitude());
						longitude = Double.parseDouble(log);	

						makeJsonReqForHotels(latitude + "", longitude + "");
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.postDelayed(this, 600000);
			}

		};

		handler.postDelayed(runnable, 5000);

	}

	@Override
	public void onDestroy() { // TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);
		
		//cc.showToast("Service closed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	// Vollay method
	private void makeJsonReqForHotels(final String lat, final String lng) {

		StringRequest jsonObjReq = new StringRequest(Method.POST,
				URL.url_getoffer_1, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("url_getoffer", response);
						
						jsonParse(response);
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
				params.put("lat", lat);
				params.put("long", lng);
				params.put("miles", "50");
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

	private void jsonParse(String response) {
		// TODO Auto-generated method stub
		try {
			
			DecimalFormat df = new DecimalFormat("##.######");
			
			JSONObject jObject = new JSONObject(response);
			if (jObject.getString("status").equals("1")) {
				//Delete Table partner offer first
                dbcon.deletePartnerOffer();
				JSONObject jOffer = jObject.getJSONObject("offer");
				JSONArray offerArray = jOffer.getJSONArray("offer_list");
			for (int i = 0; i < offerArray.length(); i++) {
					JSONObject jObject2 = offerArray.getJSONObject(i);
					offers data = new offers();
					data.setOfferid(jObject2.getString("offerid"));
					data.setDescriptions(jObject2.getString("descriptions"));
					data.setLat(df.format(Double.parseDouble(jObject2.getString("lat"))));
					data.setLng(df.format(Double.parseDouble(jObject2.getString("long"))));
					data.setCategory_name(jObject2.getString("category_name"));
					data.setAddress(jObject2.getString("address"));
					data.setPrice(jObject2.getString("price"));
					data.setSub_name(jObject2.getString("sub_name"));
					data.setStore_name(jObject2.getString("store_name").replace("'", ""));	
					data.setDistance(Double.parseDouble(jObject2.getString("distance")));
					data.setCat_id(jObject2.getString("cat_id"));
					data.setSub_cat(jObject2.getString("sub_cat"));
				if (!jObject2.getString("start_time").equals("")) {
					data.setValid("Valid Between : "+jObject2.getString("start_time")+" - "+jObject2.getString("end_time")+" "+jObject2.getString("day"));
				} else {
					data.setValid("");
				}
				dbcon.insertPartnerOffer(Integer.parseInt(data.getOfferid()), data.getStore_name(), data.getSub_name(), String.valueOf(data.getDistance()), data.getCategory_name(), data.getPrice(), data.getAddress(), data.getLat(), data.getLng(), data.getDescriptions(),"no",data.getCat_id(),data.getSub_cat(),data.getValid());

				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
