package com.mxi.wazooapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.mxi.wazooapp.adapter.MenuListAdapter;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.scategory_list;
import com.mxi.wazooapp.network.AppController;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.JsonGetData;
import com.mxi.wazooapp.network.URL;

import java.util.HashMap;
import java.util.Map;


public class CategoryActivity extends Activity {

    TextView tv_rest_head;
    public String position;
    SeekBar seekBar2;
    ListView lv_listView;
    MenuListAdapter adapter;
    SQLiteWander controller;
    LinearLayout rest_ll_pref;
    CommonClass cc;
    ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurent_list);

        cc = new CommonClass(CategoryActivity.this);
        lv_listView = (ListView) findViewById(R.id.lv_listView);
        tv_rest_head = (TextView) findViewById(R.id.tv_rest_head);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        controller = new SQLiteWander(getApplicationContext());
        Intent in = getIntent();
        position = in.getStringExtra("position");
        rest_ll_pref = (LinearLayout) findViewById(R.id.rest_ll_pref);

        tv_rest_head.setText(JsonGetData.arraylist.get(Integer.parseInt(position) - 1).getCategory_name());

        if (JsonGetData.arraylist.get(Integer.parseInt(position) - 1).getCategory_name().equals("Hotels")) {
            rest_ll_pref.setVisibility(View.VISIBLE);
            seekBar2.setProgress(cc.loadPrefInt("hotel_pref"));
            seekBar2.getProgressDrawable().setColorFilter(
                    Color.parseColor("#F9A042"), Mode.SRC_IN);
        } else {
            adapter = new MenuListAdapter(getApplicationContext(), 0, JsonGetData.arraylist.get(Integer.parseInt(position) - 1)
                    .getSublist());

            lv_listView.setAdapter(adapter);


        }

        seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                for (int i = 0; i < JsonGetData.arraylist.get(Integer.parseInt(position) - 1).getSublist().size(); i++) {
                    scategory_list subcatlistarraylist = JsonGetData.arraylist.get(Integer.parseInt(position) - 1).getSublist().get(i);
                    controller.updatePref(subcatlistarraylist.getSubcat_id(), String.valueOf(seekBar.getProgress()));
                }
                cc.savePrefInt("hotel_pref", seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBar2.getProgressDrawable().setColorFilter(
                        Color.parseColor("#F9A042"), Mode.SRC_IN);


            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("@@@Here","goToMainActivity");
//        new LoadMainScreen().execute();
        Intent mIntent = new Intent(CategoryActivity.this,
                SideMenuActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }

    public class LoadMainScreen extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pDialog=new ProgressDialog(CategoryActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
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
                        GetCategortList(CategoryActivity.this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();
            Intent intent= new Intent(CategoryActivity.this, SideMenuActivity.class);
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            finish();
            super.onPostExecute(aVoid);
        }
    }



    public void GetCategortList(final Context mcontext) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                URL.url_get_cat_1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("url_get_cat", response);

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
        AlertDialog.Builder alert = new AlertDialog.Builder(CategoryActivity.this);
        alert.setTitle("Wazoo");
        alert.setMessage(msg);
        alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetCategortList(CategoryActivity.this);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        if(!((Activity) CategoryActivity.this).isFinishing())
        {
            alert.show();
        }
    }



}
