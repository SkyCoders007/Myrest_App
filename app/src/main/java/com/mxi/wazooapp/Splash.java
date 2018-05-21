package com.mxi.wazooapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.JsonGetData;
import com.mxi.wazooapp.network.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Splash extends Activity {

    boolean isSkipedToRecepts = false;
    LinearLayout llController;
    ImageView iv_logo;
    private JsonGetData jsonGetData;
    ProgressBar mProgressBar;
    CommonClass cc;
    GPSTracker gps;
    SQLiteWander dbcon;
    Button skipToRecepts;
    String[] sentences = new String[]{"For the best deals near you!", "Any food just a click away!", "Automatically get directions!", "Hotels Shopping etc...", "Get amazing offers", "Set Your interests"};
    private int mCounter = 5;
    private HTextView hTextView;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cc = new CommonClass(Splash.this);

        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        if (!cc.loadPrefBoolean("isDeviceRegistered")) {
            cc.savePrefBoolean("isDeviceRegistered", true);
            makeJsonCallRegisterDevice(android_id);
        }

        llController = (LinearLayout) findViewById(R.id.ll_controller_splash);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        skipToRecepts = (Button) findViewById(R.id.btn_skip_to_myrecepts);
        skipToRecepts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSkipedToRecepts = true;

                if (gps.canGetLocation()) {

                    if (cc.isConnectingToInternet()) {
                        GetCategortList(Splash.this);
                    } else {
                        showVolleyErrorDialog("Please check internet connection");
                    }
                } else {
                    gps.showSettingsAlert();
                }

                Intent receptsIntent = new Intent(Splash.this, MyRecepts.class);
                startActivity(receptsIntent);
                finish();
            }
        });

        hTextView = (HTextView) findViewById(R.id.text2);
        hTextView.setTextColor(Color.WHITE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_splash_screen);
        // hTextView.setBackgroundColor(Color.WHITE);
        hTextView.setAnimateType(HTextViewType.SCALE);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for marshmallow and newer versions
            checkGPSPermission();
        } else {
            gps = new GPSTracker(Splash.this);
            dbcon = new SQLiteWander(Splash.this);
            checkAvailableRecpts();

            jsonGetData = new JsonGetData();
            new CountDownTimer(5000, 1500) {

                public void onTick(long millisUntilFinished) {
                    updateCounter();
                }

                public void onFinish() {
                    if (gps.canGetLocation()) {

                        if (cc.isConnectingToInternet()) {
                            GetCategortList(Splash.this);
                        } else {
                            showVolleyErrorDialog("Please check internet connection");
                        }
                    } else {
                        gps.showSettingsAlert();
                    }
                }
            }.start();
        }
    }

    private void makeJsonCallRegisterDevice(final String android_id) {


        final StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_reg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("url_reg", response);

                try {
                    JSONObject object = new JSONObject(response);
//                    cc.savePrefString("user_token", object.getString("status"));
                    Log.e("RegMsg", object.getString("message"));
                    Log.e("RegKey", object.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_id", android_id);
                Log.e("request logout", params.toString());
                return params;
            }
/*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
//                headers.get("");
                cc.savePrefString("user_token", headers.get("wazoo-token"));
                Log.i("request header", headers.toString());
                Log.e("wazoo-token",headers.get("wazoo-token"));
                return headers;
            }*/

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                String wazoo_token = response.headers.get("wazoo-token");
                Log.e("wazoo-token", wazoo_token);
                cc.savePrefString("user_token", wazoo_token);
                return super.parseNetworkResponse(response);
            }
        };
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkGPSPermission() {
        int gpsWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (gpsWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            gps = new GPSTracker(Splash.this);
            checkReadWritePermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkReadWritePermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        } else {
            dbcon = new SQLiteWander(Splash.this);
            checkAvailableRecpts();

            jsonGetData = new JsonGetData();
            new CountDownTimer(5000, 1500) {

                public void onTick(long millisUntilFinished) {
                    updateCounter();
                }

                public void onFinish() {
                    if (gps.canGetLocation()) {

                        if (cc.isConnectingToInternet()) {
                            GetCategortList(Splash.this);
                        } else {
                            showVolleyErrorDialog("Please check internet connection");
                        }
                    } else {
                        gps.showSettingsAlert();
                    }
                }
            }.start();
        }
    }

    public void onClick(View v) {
        updateCounter();
    }

    private void updateCounter() {
        mCounter = mCounter >= sentences.length - 1 ? 0 : mCounter + 1;
        hTextView.animateText(sentences[mCounter]);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    // Vollay method
    public void GetCategortList(final Context mcontext) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_get_cat_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("url_get_cat", response);

                jsonGetData.fatchCategory(mcontext, response);

                if (!isSkipedToRecepts) {
                    if (!cc.loadPrefBoolean("isthankyou")) {
                        // TODO Auto-generated method stub
                        Intent mIntent = new Intent(getApplicationContext(),
                                ThankYouActivity.class);

                        cc.savePrefBoolean("isthankyou", true);
                        cc.savePrefBoolean("isFirstTime", true);

                        cc.savePrefBoolean("isaudio", true);
                        cc.savePrefBoolean("isoffer", true);
                        startActivity(mIntent);
                    } else {
//						Intent mIntent = new Intent(getApplicationContext(),
//								SideMenuActivity.class);
                        Intent mIntent = new Intent(getApplicationContext(),
                                OfferActivity.class);
                        startActivity(mIntent);
                    }
                    finish();
                }

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

                if (message.equalsIgnoreCase("")) {
                    volleyError.printStackTrace();
                } else {
                    showVolleyErrorDialog(message);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //  headers.put("user_token", cc.loadPrefString("user_token"));
                headers.put("wazoo-token", cc.loadPrefString("user_token"));
                Log.i("request header", headers.toString());
                return headers;
            }
        };

		/*
        Adding webservice request timeout here
        param:1- webservice request Timeout after 8 seconds
        param:2- Max numbers of retries
        param:3- back of multiplier
*/
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(8000, 1, 1));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }


    public View makeView() {

        TextView t = new TextView(this);
        t.setGravity(Gravity.CENTER);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        return t;
    }

    public void showErrorDialog(String msg, final boolean isFromPermission) {
        mProgressBar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(Splash.this);
        alert.setTitle("Wazoo");
        alert.setMessage(msg);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFromPermission) {
                    checkGPSPermission();
                } else {
                    finish();
                }
            }
        });
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dbcon = new SQLiteWander(Splash.this);
                    checkAvailableRecpts();

                    jsonGetData = new JsonGetData();
                    new CountDownTimer(5000, 1500) {

                        public void onTick(long millisUntilFinished) {
                            updateCounter();
                        }

                        public void onFinish() {
                            if (gps.canGetLocation()) {

                                if (cc.isConnectingToInternet()) {
                                    GetCategortList(Splash.this);
                                } else {
                                    showVolleyErrorDialog("Please check internet connection");
//						cc.showToast("Please check your internet connectivity");
                                }

                            } else {
                                gps.showSettingsAlert();
                            }
                        }
                    }.start();

                } else {
                    // Permission Denied
                    //  Toast.makeText(NewPrescriptionRequest.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                    showErrorDialog("Please allow the permission for better performance", true);
                }
                break;
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps = new GPSTracker(Splash.this);
                    checkReadWritePermission();
                } else {
                    showErrorDialog("Please allow the permission for better performance", true);
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showVolleyErrorDialog(String msg) {
        mProgressBar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(Splash.this);
        alert.setTitle("Wazoo");
        alert.setMessage(msg);
        alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetCategortList(Splash.this);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        if (!((Activity) Splash.this).isFinishing()) {
            //show dialog
            alert.show();
        }
    }

    public boolean checkAvailableRecpts() {
        Cursor c = dbcon.getMyreceipts();
        if (c.getCount() != 0) {
            skipToRecepts.setVisibility(View.VISIBLE);
            return true;
        } else {
            skipToRecepts.setVisibility(View.GONE);
            return false;
        }
    }
}