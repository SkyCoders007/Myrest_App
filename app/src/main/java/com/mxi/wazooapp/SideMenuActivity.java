package com.mxi.wazooapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.adapter.NavDrawerListAdapter;
import com.mxi.wazooapp.businesslogic.TimecheckService;
import com.mxi.wazooapp.model.NavDrawerItem;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.JsonGetData;
import com.mxi.wazooapp.network.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SideMenuActivity extends FragmentActivity {

	public BroadcastReceiver mReceiver;

	private ProgressDialog mProgressDialog;
	private DrawerLayout mDrawerLayout;
	EditText etVerifyOTP;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	public ImageView imageView_drawer;
	private ProgressDialog pDialog;
	public JsonGetData jsonGetData;
	int i=0;
	Context context;
	// slide menu items
	private ArrayList<String> navMenuTitles = new ArrayList<String>();
	private ArrayList<String> navMenuIcons = new ArrayList<String>();
	// nav drawer title
	private CharSequence mDrawerTitle;
	SharedPreferences getPrefs;
	// used to store app title
	private CharSequence mTitle;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	CommonClass cc;
	GPSTracker gps;
	TimecheckService tcs;
	public static String state,country;
	public String strCountry;

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		cc = new CommonClass(SideMenuActivity.this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("PENDING_INTENT"))
		{
			//Do the codes
			Log.e("Sidemenu activity", "PENDING_INTENT");
			if (isMyServiceRunning(TimecheckService.class,SideMenuActivity.this)) {
				Log.e("Sidemenu activity", "isMyServiceRunning");
				this.stopService(new Intent(this, TimecheckService.class));
//				tcs.stopService();

				Intent mIntent = new Intent(SideMenuActivity.this, TimecheckService.class);
				startService(mIntent);

				cc.savePrefBoolean("isCancelFrequence", true);

			}
		} else {
			cc.savePrefBoolean("isCancelFrequence", false);
			tcs=new TimecheckService();
		}

		mTitle = mDrawerTitle = getTitle();
		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

/*		if(!cc.isConnectingToInternet()) {
			String message = "Cannot connect to Internet. \nPlease check your connection!";
			showVolleyErrorDialog(message);
		} else {
			if(JsonGetData.arraylist.size() == 0) {
				GetCategortList(SideMenuActivity.this);
			}
		}*/

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		imageView_drawer = (ImageView) findViewById(R.id.imageView_drawer);
		ArrayList<String> stalist = new ArrayList<String>();
		navDrawerItems = new ArrayList<NavDrawerItem>();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		gps = new GPSTracker(SideMenuActivity.this);


		// Add header In List View
		View header = getLayoutInflater().inflate(R.layout.header, null);
		mDrawerList.addHeaderView(header);


		// Get navmenu titles and navmene image from Webservice
		for (int l = 0; l < JsonGetData.arraylist.size(); l++) {
			String menu = JsonGetData.arraylist.get(l).getCategory_name();
			String image = jsonGetData.arraylist.get(l).getCategory_img();
			try {
				navMenuTitles.add(menu);
				navMenuIcons.add(image);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		Log.e("Drawer menu Titles", JsonGetData.arraylist.size() + "");
		Log.e("Menu image", navMenuTitles + "");
		// adding nav drawer items to array
		// navMenuTitles.clear();
		for (int i = 0; i < navMenuTitles.size(); i++) {

			// navDrawerItems.clear();
			navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(i),
					navMenuIcons.get(i) + ""));

		}

		// Recycle the typed array
		// navMenuIcons.recycle();

		// new GetContacts().execute();
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

	/*	// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
*/
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				try {
					getActionBar().setTitle(mTitle);
					// calling onPrepareOptionsMenu() to show action bar icons
					invalidateOptionsMenu();
				} catch (Exception e) {

				}
			}

			public void onDrawerOpened(View drawerView) {
				try {
					getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu();
				} catch (Exception e) {

				}
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

		imageView_drawer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.openDrawer(mDrawerList);
			}
		});

		if (!cc.loadPrefBoolean("isdrawer")) {
			mDrawerLayout.openDrawer(mDrawerList);
			cc.savePrefBoolean("isdrawer", true);
		}


		Intent mIntent = new Intent(getApplicationContext(), TimecheckService.class);
		Log.e("TimechekIntent","is called");
		startService(mIntent);

//		startService(new Intent(getApplicationContext(), OffersInserService.class));
		Log.e("OfferInServiceIntent","is called");
		new getCountryState().execute();

/*		if(!cc.loadPrefBoolean("isShowContestDialog")){
			showContestDialogs();
		}*/
	}

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	// Call fragment
	@SuppressWarnings("unused")
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

		switch (position) {

		case 0:
			fragment = new HomeActivity();
			break;

		default:

			//if (JsonGetData.arraylist.get((position) - 1).getSublist().size() != 0) {
				Intent ini = new Intent(getApplicationContext(),
						CategoryActivity.class);
				ini.putExtra("position", position + "");
				startActivity(ini);

			//} else {

			//}

			break;

		}

		try {
			if (fragment != null) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();

				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles.get(position));
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				// error in creating fragment
				Log.e("MainActivity", "Error in creating fragment");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public class getCountryState extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {

			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(SideMenuActivity.this, Locale.getDefault());

			try {
				Log.i("SieMenuActivity...", "gps.getLatitude() = " + gps.getLatitude());
				Log.i("SieMenuActivity...", "gps.getLongitude() = " + gps.getLongitude());
				addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//				addresses = geocoder.getFromLocation(42.259678, -83.210633, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//				String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//				String city = addresses.get(0).getLocality();
				 state = addresses.get(0).getAdminArea();
				strCountry = addresses.get(0).getCountryName();
				country = addresses.get(0).getCountryName();

				if(strCountry.equalsIgnoreCase("United States")){
					strCountry = "US";
				} else {
					strCountry = addresses.get(0).getCountryName();
				}
				Log.e("Country",country);
				//changes applied
				cc.savePrefString("State",state);
				cc.savePrefString("Country",country);
				String postalCode = addresses.get(0).getPostalCode();
				String knownName = addresses.get(0).getFeatureName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

//			if (cc.loadPrefString("getCurrency").equals("")){
				makeJSONforgetCurrency(country);
//			}
		}
	}

	private void makeJSONforgetCurrency(final String country) {

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
				URL.url_getCurrency_1, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {

				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getString("status").equals("success")){
						String data = jsonObject.getString("data");
						cc.savePrefString("getCurrency",data);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(i==0){
					cc.showToast("Be patient, loading info...");
					i++;
				}else if(i>0 && i<3){

				}else{
					cc.showToast("No Internet connection");
				}
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("country", strCountry);
				Log.e("getCurrency",params.toString());
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

	// Vollay method
	/*public void GetCategortList(final Context mcontext) {

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
				URL.url_get_cat_1, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e("url_get_cat", response);

				JsonGetData jsonGetData = new JsonGetData();
				jsonGetData.fatchCategory(mcontext, response);

				Intent mIntent= new Intent(SideMenuActivity.this, SideMenuActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(mIntent);
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				volleyError.printStackTrace();
				String message = "";
				if (volleyError instanceof NetworkError) {
					message = "Cannot connect to Internet. \nPlease check your connection!";
				} else if (volleyError instanceof NoConnectionError) {
					message = "Cannot connect to Internet.\nPlease check your connection!";
				} else if (volleyError instanceof TimeoutError) {
					message = "Connection TimeOut!\nPlease check your internet connection.";
				}

				if(message.equalsIgnoreCase("")) {
					volleyError.printStackTrace();
				} else {
					showVolleyErrorDialog(message);
				}
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<>();
				//  headers.put("user_token", cc.loadPrefString("user_token"));
				headers.put("wazoo-token", cc.loadPrefString("user_token"));
				Log.i("request header", headers.toString());
				return headers;
			}
		};

		*//*
        Adding webservice request timeout here
        param:1- webservice request Timeout after 8 seconds
        param:2- Max numbers of retries
        param:3- back of multiplier
*//*
		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 1, 1));

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

	}
*/
/*	public void showVolleyErrorDialog(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(SideMenuActivity.this);
		alert.setTitle("Wazoo");
		alert.setMessage(msg);
		alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GetCategortList(SideMenuActivity.this);
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		if(!((Activity) SideMenuActivity.this).isFinishing())
		{
			//show dialog
			alert.show();
		}
	}*/

	private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

/*	private void showContestDialogs() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(SideMenuActivity.this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.contest_dialog, null);
		alertDialog.setView(dialogView);
		alertDialog.setTitle("Wazoo Contest");
		alertDialog.setCancelable(false);
		alertDialog.setMessage("Please Enter Contest Code here");


		etVerifyOTP = (EditText) dialogView.findViewById(R.id.et_vrify_otp);
		TextView tvGetCode = (TextView) dialogView.findViewById(R.id.tv_get_contest_code);
		tvGetCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wazoomobile.com/Contest"));
				startActivity(browserIntent);
			}
		});

		alertDialog.setPositiveButton("Accept",
				new DialogInterface.OnClickListener()

				{
					public void onClick(DialogInterface dialog, int which) {
						String otp = "";
						otp = etVerifyOTP.getText().toString();
						Log.e("show dialog", "otp = " + otp);
						if(otp.equalsIgnoreCase("")){
							showContestDialogs();
							etVerifyOTP.setError("Please Enter Contest Code");
						} else {
							Log.e("show dialog", "otp = in else " + otp);
							VerifyContestCode(otp);
							dialog.dismiss();
						}
					}
				});

		alertDialog.setNegativeButton("Skip",
				new DialogInterface.OnClickListener()

				{
					public void onClick(DialogInterface dialog, int which) {
						cc.savePrefBoolean("isShowContestDialog", true);
						dialog.cancel();
					}
				});

		alertDialog.show();
	}*/

	// Vollay method
/*	public void VerifyContestCode(final String otp) {
		mProgressDialog = new ProgressDialog(SideMenuActivity.this);
		mProgressDialog.setMessage("Code is Verifying ...");
		mProgressDialog.show();
		StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
				URL.url_verifyContestCode_1 , new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e("url_verifyContestCode", "response = " + response);
				if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
				}
				try {
					JSONObject obj = new JSONObject(response);
					String status = obj.getString("status");
					String msg = obj.getString("message");
					if(status.equalsIgnoreCase("200")){
						cc.savePrefBoolean("isShowContestDialog", true);
						cc.showSnackbar(mDrawerLayout, msg);
					} else {
						cc.showSnackbar(mDrawerLayout, msg);
						showContestDialogs();
						etVerifyOTP.setError("Invalid Contest Code");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
				}
				if(i==0){
					cc.showToast("Be patient, loading info...");
					i++;
				}else if(i>0 && i<3){

				}else{
					cc.showToast("No Internet connection");
				}

			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Contest_code", otp);
				Log.e("Contest_code",params.toString());
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
	}*/
}
