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
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.HttpHelper;
import com.mxi.wazooapp.network.URL;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class OfferDialog extends Activity {

	LinearLayout dialog_ll_direction, LinearLayout1;
	CommonClass cc;
	String  address, lat, lng, audio="", price, offerid , device_id, sub_name, store_name,descriptions,valid="";
	String imagePath;
	double distance;
	GPSTracker gps;

	ImageView ivOfferImage;
	TextView tv_offer_value, tv_offer_address,tv_offer_dist,tv_valid, tv_offer_tag;
	MediaPlayer mp;
	SQLiteWander dbcon;
	LinearLayout sv_offer_view;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		getWindow().getAttributes().windowAnimations = R.style.Animation;
		int width = (metrics.widthPixels / 1);
		int height = (metrics.heightPixels / 3);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_offer_dialog);
		getWindow().getAttributes().width = width;
		getWindow().getAttributes().height = height;

		WindowManager.LayoutParams wmlp = getWindow().getAttributes();
		wmlp.gravity = Gravity.BOTTOM;
		dbcon = new SQLiteWander(getApplicationContext());
		cc = new CommonClass(getApplicationContext());
		gps = new GPSTracker(OfferDialog.this);
		dialog_ll_direction = (LinearLayout) findViewById(R.id.dialog_ll_direction);
		LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);

		Intent mIntent = getIntent();
		
		address = mIntent.getStringExtra("address");
		lat = mIntent.getStringExtra("lat");
		lng = mIntent.getStringExtra("long");
		price = mIntent.getStringExtra("price");
		offerid = mIntent.getStringExtra("offerid");
		sub_name = mIntent.getStringExtra("sub_name");
		store_name = mIntent.getStringExtra("store_name");
		descriptions = mIntent.getStringExtra("descriptions");
		distance = mIntent.getDoubleExtra("distance", 0.0);
		valid = mIntent.getStringExtra("valid");
		imagePath = mIntent.getStringExtra("offer_image");

//		HttpHelper httpHelper = new HttpHelper();
//		LatLng fromLocation = new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLong));
//		LatLng toLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//		String directionUrl = httpHelper.getDirectionsUrl(fromLocation, toLocation, cc.loadPrefString("Country"));
//		GetDistance(directionUrl);

		ivOfferImage = (ImageView) findViewById(R.id.iv_offer_image);
		tv_offer_value = (TextView) findViewById(R.id.tv_offer_value);
		tv_offer_address = (TextView)findViewById(R.id.tv_offer_address);
		tv_offer_dist = (TextView)findViewById(R.id.tv_offer_dist);
		tv_offer_tag = (TextView) findViewById(R.id.tv_offer_tag);
		tv_valid = (TextView)findViewById(R.id.tv_valid);
		sv_offer_view = (LinearLayout)findViewById(R.id.sv_offer_view);

		Picasso.with(OfferDialog.this).load(imagePath).into(ivOfferImage);
		tv_offer_value.setText(cc.loadPrefString("getCurrency")+""+price+", "+descriptions+" @"+store_name);
		tv_offer_address.setText(address);
		tv_offer_tag.setText(sub_name);
		/// changes applied - 1 for convert the distance according to country

		if(cc.loadPrefString("Country").equals("India")){
			double dist = distance* 1.609344;
			DecimalFormat precision = new DecimalFormat("0.00");
			tv_offer_dist .setText(precision.format(dist)+" km away");
		}else{
			tv_offer_dist .setText(distance+"m away");
		}
		tv_valid.setText(valid);
		// Offer dialog audio play
		mp = new MediaPlayer();

		 device_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID); 
		
		
		if (cc.loadPrefBoolean("isaudio")) {
			if (audio.equals("")) {
				try {
					mp = MediaPlayer.create(OfferDialog.this, R.raw.iphonenoti);
					mp.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {

				new AsyncMusic().execute();

			}
		}
	
		// offer dialog direction pass through google link
		dialog_ll_direction.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {

					Intent mIntent = new Intent(getApplicationContext(),
							SecondOfferDialog.class);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					mIntent.putExtra("address", address);
					mIntent.putExtra("price", price);
					mIntent.putExtra("lat", lat);
					mIntent.putExtra("long", lng);
					mIntent.putExtra("descriptions", descriptions);
					mIntent.putExtra("offerid", offerid);
					mIntent.putExtra("store_name", store_name);
					mIntent.putExtra("sub_name", sub_name);
					mIntent.putExtra("valid",valid);
					CommonClass.isShowMap = true;
					startActivity(mIntent);

					if (cc.isConnectingToInternet()) {
						makeJsonReqForAddOffer(device_id, offerid);
					}
//					getMapLocation();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		sv_offer_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent mIntent = new Intent(getApplicationContext(),
						SecondOfferDialog.class);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				mIntent.putExtra("address", address);
				mIntent.putExtra("price", price);
				mIntent.putExtra("lat", lat);
				mIntent.putExtra("long", lng);
				mIntent.putExtra("descriptions", descriptions);
				mIntent.putExtra("offerid", offerid);
				mIntent.putExtra("store_name", store_name);
				mIntent.putExtra("sub_name", sub_name);
				mIntent.putExtra("valid",valid);
				startActivity(mIntent);

				if (cc.isConnectingToInternet()) {
					makeJsonReqForAddOffer(device_id, offerid);
				}

				finish();
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
		}, 30000);
	}

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	public void getMapLocation() {

		try {

			boolean isAppInstalled =  cc.appInstalledOrNot(OfferDialog.this, "com.google.android.apps.maps");
			Log.e("Dhaval ", "google map android app is installe = " + isAppInstalled);

			if(isAppInstalled){
				Intent directioIntent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?daddr=" + lat + ","
								+ lng));
				directioIntent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(directioIntent);
			} else {
				String baseURL = "http://maps.google.com/maps?";
				String fromLocation = "saddr=" + gps.getLatitude() + "," + gps.getLongitude();
				String toLocation = "&daddr=" + lat + "," + lng;
				String mapUrl = baseURL + fromLocation + toLocation;
				Log.e("Dhaval ", "Map URL = " + mapUrl);

				Intent mapIntent = new Intent(OfferDialog.this, ShowMapActivity.class);
				mapIntent.putExtra("MAP_URL", mapUrl);
				startActivity(mapIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		finish();

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
			Log.e("Backgroung", "Background");
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
			Log.e("POST	", "POST");

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("onPause	", "onPause");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("onResume	", "onResume");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("onDestroy	", "onDestroy");
		try {
			mp.stop();
			mp.release();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mp.release();
		}
	}

	
	// Vollay method
	private void makeJsonReqForAddOffer(final String deviceid, final String offerid) {

		StringRequest jsonObjReq = new StringRequest(Method.POST,
				URL.url_add_device_1, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("Response", response);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						cc.showToast("No Internet connection");
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
}
