package com.mxi.wazooapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.businesslogic.TimecheckService;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.offers;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.JsonGetData;
import com.mxi.wazooapp.network.URL;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class OfferActivity extends Activity {
	ListView lv_list;
	ImageView iv_back_ls;
//	private ProgressDialog pDialog;
	Handler handler;
	GPSTracker gps;
	double latitude;
    LinearLayout ll_main;
    int i=0;
	double longitude;
	CommonClass cc;
	String device_id;
	OfferAdapter adapter;
	SeekBar seekBar1;
	TextView textView;
	double dist;
    EditText etVerifyOTP;
	String seekValue;
	SQLiteWander dbcon;
	TimecheckService tcs;
	ArrayList<offers> offeralist=JsonGetData.offeralist;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offer_listview);
		cc = new CommonClass(OfferActivity.this);
		tcs=new TimecheckService();
		lv_list = (ListView) findViewById(R.id.lv_list);
		iv_back_ls = (ImageView) findViewById(R.id.iv_back_ls);
		dbcon=new SQLiteWander(OfferActivity.this);
		gps = new GPSTracker(OfferActivity.this);
        ll_main=(LinearLayout)findViewById(R.id.ll_main);

		device_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID); 
		
		
		iv_back_ls.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		// get location of user
		if (gps.canGetLocation()) {

				latitude = gps.latitude;
				longitude = gps.longitude;
				GetOffer(latitude + "", longitude + "");

		}

		adapter = new OfferAdapter(OfferActivity.this, 0,
				offeralist);
		lv_list.setAdapter(adapter);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar1.setProgress(Integer.parseInt(cc.loadPrefString("partner_miles")));
		// applied changes - 1 for convert the distance according to country in Seekbar
		if(cc.loadPrefString("Country").equals("India")){

			double dist= Double.parseDouble(String.valueOf(seekBar1.getProgress()));
			dist=dist * 1.609344;
			String number = String.valueOf(dist);
			number = number.substring(number.indexOf(".")).substring(1);
			long val= Long.parseLong((number));
			if(dist == 0){
				dist =0;
			}else if(Double.parseDouble(String.valueOf(seekBar1.getProgress()))==8){
				dist = 13;
			}else{
				if(val>500000) {
					dist = Math.round(dist);
				}else{
					dist = Math.round(dist)+1;
				}
			}

			DecimalFormat precision = new DecimalFormat("0");
			seekValue="Offer fetch radius, km: "+precision.format(dist);
		}else{
			seekValue="Offer fetch radius, miles: "+seekBar1.getProgress();
		}
		textView = (TextView) findViewById(R.id.textView);
		textView.setText(seekValue);

		if(!cc.loadPrefBoolean("isShowContestDialog")){
			showContestDialogs();
		}
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// changes applied - 1 for convert the distance according to country in Seekbar
				if(cc.loadPrefString("Country").equals("India")){

					double dist= Double.parseDouble(String.valueOf(seekBar1.getProgress()));
					dist=dist * 1.609344;
					String number = String.valueOf(dist);
					number = number.substring(number.indexOf(".")).substring(1);
					long val= Long.parseLong((number));
					if(dist == 0){
						
					}else if(Double.parseDouble(String.valueOf(seekBar1.getProgress()))==8){
						dist = 13;
					}else{
						if(val>500000) {
							dist = Math.round(dist);
						}else{
							dist = Math.round(dist)+1;
						}
					}
					DecimalFormat precision = new DecimalFormat("0");
					seekValue="Offer fetch radius, km: "+precision.format(dist);
				}else{
					seekValue="Offer fetch radius, miles: "+seekBar.getProgress();
				}
				textView.setText(seekValue);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				// changes applied - 1 for convert the distance according to country in Seekbar
				if(cc.loadPrefString("Country").equals("India")){

					double dist= Double.parseDouble(String.valueOf(seekBar1.getProgress()));
					dist=dist * 1.609344;
					String number = String.valueOf(dist);
					number = number.substring(number.indexOf(".")).substring(1);
					long val= Long.parseLong((number));
					if(dist == 0){
						dist =0;
					}else if(Double.parseDouble(String.valueOf(seekBar1.getProgress()))==8){
						dist = 13;
					}else{
						if(val>500000) {
							dist = Math.round(dist);
						}else{
							dist = Math.round(dist)+1;
						}
					}
					DecimalFormat precision = new DecimalFormat("0");
					seekValue="Offer fetch radius, km: "+precision.format(dist);
				}else{
					seekValue="Offer fetch radius, miles: "+seekBar.getProgress();
				}

			textView.setText(seekValue);
				gps = new GPSTracker(OfferActivity.this);
				Log.e("Dhaval ", "seekBar1.getProgress() = " + seekBar1.getProgress());
				cc.savePrefString("partner_miles",seekBar1.getProgress()+"");
				latitude = gps.latitude;
				longitude = gps.longitude;
				GetOffer(latitude + "", longitude + "");
			}
		});


        new LoadMainScreen().execute();

	}


    private void showContestDialogs() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OfferActivity.this);
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
    }


    public void VerifyContestCode(final String otp) {
//        pDialog = new ProgressDialog(OfferActivity.this);
//        pDialog.setMessage("Code is Verifying ...");
//        pDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_verifyContestCode_1 , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("url_verifyContestCode", "response = " + response);
//                if(pDialog.isShowing()){
//                    pDialog.dismiss();
//                }
                try {
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");
                    String msg = obj.getString("message");
                    if(status.equalsIgnoreCase("200")){
                        cc.savePrefBoolean("isShowContestDialog", true);
                        cc.showSnackbar(ll_main, msg);
                    } else {
                        cc.showSnackbar(ll_main, msg);
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
//                if(pDialog.isShowing()){
//                    pDialog.dismiss();
//                }
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
    }

    // Volley method
	private void GetOffer(final String latitude, final String longitude) {

//		pDialog = new ProgressDialog(OfferActivity.this);
//		pDialog.setMessage("Please wait...");
//		pDialog.setCancelable(false);
//		pDialog.show();
//

		StringRequest jsonObjReq = new StringRequest(Method.POST,
				URL.url_getoffer_1, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				JsonGetData jsonGetData = new JsonGetData();
				jsonGetData.parseData(OfferActivity.this, response);


//				if (pDialog.isShowing())
//					pDialog.dismiss();

				adapter.notifyDataSetChanged();
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
					showErrorDialog(message);
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
				Log.e("######", params.toString());
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

	public class OfferAdapter extends ArrayAdapter<offers>  {
		private LayoutInflater mInflater;
		Context mContext;
		ArrayList<offers> offeralist;

		public OfferAdapter(Context paramContext, int paramInt,
				ArrayList<offers> list) {
			super(paramContext, paramInt, list);
			// TODO Auto-generated constructor stub

			this.mContext = paramContext;
			this.offeralist = ((ArrayList) list);

			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		private class Holder {
			ImageView ivOfferImage;
			public TextView tv_offer_value,tv_offer_address,tv_offer_dist,tv_valid, tv_offer_tag;
			public LinearLayout dialog_ll_direction,sv_offer_view,ll_available;
		}

		public int getCount() {
			return this.offeralist.size();
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Holder holder;

			if(JsonGetData.offeralist.get(position).isHasimage()){
				convertView = mInflater.inflate(R.layout.activity_offer_dialog_new, parent, false);
			}else{
				convertView = mInflater.inflate(R.layout.activity_offer_dialog_no_image, parent, false);
			}


			if (convertView != null) {

				holder = new Holder();
				
				holder.tv_offer_value = (TextView) convertView.findViewById(R.id.tv_offer_value);
				holder.tv_offer_address = (TextView)convertView.findViewById(R.id.tv_offer_address);
				holder.tv_offer_dist = (TextView)convertView.findViewById(R.id.tv_offer_dist);
				holder.tv_valid = (TextView)convertView.findViewById(R.id.tv_valid);
				holder.dialog_ll_direction = (LinearLayout)convertView.findViewById(R.id.dialog_ll_direction);
				holder.sv_offer_view = (LinearLayout)convertView.findViewById(R.id.sv_offer_view);
				holder.ll_available = (LinearLayout)convertView.findViewById(R.id.ll_available);
				holder.tv_offer_tag = (TextView) convertView.findViewById(R.id.tv_offer_tag);
				holder.ivOfferImage = (ImageView) convertView.findViewById(R.id.iv_offer_image);
				convertView.setTag(holder);

			} else {
				holder = (Holder) convertView.getTag();
			}

			String store_name = JsonGetData.offeralist.get(position).getStore_name();
			String price = JsonGetData.offeralist.get(position).getPrice();
			String descriptions = JsonGetData.offeralist.get(position).getDescriptions();
			String offerImage = JsonGetData.offeralist.get(position).getOfferImage();

			Log.e("Offer Adapter", "offerImage = " + offerImage);

			Picasso.with(mContext).load(offerImage).into(holder.ivOfferImage);

			holder.tv_offer_value.setText(cc.loadPrefString("getCurrency")+""+price+", "+descriptions+" @"+store_name);
			holder.tv_offer_address.setText(JsonGetData.offeralist.get(position).getAddress());
			holder.tv_offer_tag.setText(JsonGetData.offeralist.get(position).getSub_name());

			String distance;
			/// changes applied - 1 for convert the distance according to country
			if(cc.loadPrefString("Country").equals("India")){

				double dist=JsonGetData.offeralist.get(position).getDistance();
				dist=dist * 1.609344;
				DecimalFormat precision = new DecimalFormat("0.00");
				distance=precision.format(dist)+" km away";
			}else{
				DecimalFormat precision = new DecimalFormat("0.00");
				distance=precision.format(JsonGetData.offeralist.get(position).getDistance())+" m away";
			}
			holder.tv_offer_dist.setText(distance);
			holder.tv_valid.setText(JsonGetData.offeralist.get(position).getValid());
			if(JsonGetData.offeralist.get(position).getValid().equals("")){
				holder.ll_available.setVisibility(View.GONE);
			}else{
				holder.ll_available.setVisibility(View.VISIBLE);
			}


			holder.dialog_ll_direction.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (cc.isConnectingToInternet()) {
						makeJsonReqForAddOffer(device_id, JsonGetData.offeralist.get(position).getOfferid());
					}

					Intent mIntent = new Intent(getApplicationContext(),
							SecondOfferDialog.class);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					mIntent.putExtra("address", JsonGetData.offeralist.get(position).getAddress());
					mIntent.putExtra("price", JsonGetData.offeralist.get(position).getPrice());
					mIntent.putExtra("lat", JsonGetData.offeralist.get(position).getLat());
					mIntent.putExtra("long", JsonGetData.offeralist.get(position).getLng());
					mIntent.putExtra("descriptions", JsonGetData.offeralist.get(position).getDescriptions());
					mIntent.putExtra("offerid", JsonGetData.offeralist.get(position).getOfferid());
					mIntent.putExtra("store_name", JsonGetData.offeralist.get(position).getStore_name());
					mIntent.putExtra("sub_name", JsonGetData.offeralist.get(position).getSub_name());
					mIntent.putExtra("valid", JsonGetData.offeralist.get(position).getValid());
					CommonClass.isShowMap = true;
					startActivity(mIntent);
				}
			});
			holder.sv_offer_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if (cc.isConnectingToInternet()) {
						makeJsonReqForAddOffer(device_id, JsonGetData.offeralist.get(position).getOfferid());
					}

					Intent mIntent = new Intent(getApplicationContext(),
							SecondOfferDialog.class);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					mIntent.putExtra("address", JsonGetData.offeralist.get(position).getAddress());
					mIntent.putExtra("price", JsonGetData.offeralist.get(position).getPrice());
					mIntent.putExtra("lat", JsonGetData.offeralist.get(position).getLat());
					mIntent.putExtra("long", JsonGetData.offeralist.get(position).getLng());
					mIntent.putExtra("descriptions", JsonGetData.offeralist.get(position).getDescriptions());
					mIntent.putExtra("offerid", JsonGetData.offeralist.get(position).getOfferid());
					mIntent.putExtra("store_name", JsonGetData.offeralist.get(position).getStore_name());
					mIntent.putExtra("sub_name", JsonGetData.offeralist.get(position).getSub_name());
					mIntent.putExtra("valid", JsonGetData.offeralist.get(position).getValid());

					startActivity(mIntent);
				}
			});
			
			return convertView;
		}
	}

	// Vollay method
	private void makeJsonReqForAddOffer(final String deviceid, final String offerid) {

		StringRequest jsonObjReq = new StringRequest(Method.POST,
				URL.url_add_device_1, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if(cc.isDebug) {
							Log.e("Response url_add_device", response);
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(cc.isDebug) {
							Log.e("Error Response", "Error: " + error.getMessage());
						}
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

	public void showErrorDialog(String msg){
//		if(pDialog.isShowing()) {
//			pDialog.dismiss();
//		}
		AlertDialog.Builder alert = new AlertDialog.Builder(OfferActivity.this);
		alert.setTitle("Wazoo");
		alert.setMessage(msg);
		alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// get location of user
				if (gps.canGetLocation()) {

					latitude = gps.latitude;
					longitude = gps.longitude;
					GetOffer(latitude + "", longitude + "");

				}
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
		Log.e("@@@Here","goToMainActivity");

        Intent intent= new Intent(OfferActivity.this, SideMenuActivity.class);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        finish();
    }


    public class LoadMainScreen extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
//            pDialog.setMessage("Loading...");
//            pDialog.setCanceledOnTouchOutside(false);
//            pDialog.show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(!cc.isConnectingToInternet()) {
                    String message = "Cannot connect to Internet. \nPlease check your connection!";
                    showVolleyErrorDialog(message);
                } else {
                    if(JsonGetData.arraylist.size() == 0) {
//                        GetCategortList(OfferActivity.this);

                        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                                URL.url_get_cat_1, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.e("@@@url_get_cat", response);

                                JsonGetData jsonGetData = new JsonGetData();
                                jsonGetData.fatchCategory(OfferActivity.this, response);

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

                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 1, 1));

                        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            pDialog.dismiss();
/*            Intent intent= new Intent(OfferActivity.this, SideMenuActivity.class);
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            finish();*/
            super.onPostExecute(aVoid);
        }
    }



	public void GetCategortList(final Context mcontext) {

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
				URL.url_get_cat_1, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e("@@@url_get_cat", response);

				JsonGetData jsonGetData = new JsonGetData();
				jsonGetData.fatchCategory(mcontext, response);

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

		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 1, 1));

		AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

	}

	public void showVolleyErrorDialog(String msg){
		AlertDialog.Builder alert = new AlertDialog.Builder(OfferActivity.this);
		alert.setTitle("Wazoo");
		alert.setMessage(msg);
		alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GetCategortList(OfferActivity.this);
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		if(!((Activity) OfferActivity.this).isFinishing())
		{
			alert.show();
		}
	}


}
