package com.mxi.wazooapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mxi.wazooapp.businesslogic.TimecheckService;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.local_setting;
import com.mxi.wazooapp.network.CommonClass;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Switch;
import com.rey.material.widget.Switch.OnCheckedChangeListener;

import java.util.ArrayList;

public class SettingActivity extends Activity implements
        OnCheckedChangeListener, OnClickListener {

    TimecheckService tcs;
    ImageView iv_back_ls;
    ListView lv_list;
    SQLiteWander controller;
    CommonClass cc;
    public ArrayList<local_setting> alist;


    TextView tv_offer_freq, tv_alert, tv_range, tv_privacy_polocy;
    SeekBar travel_sp_freq, local_sp_freq;
    ImageView iv_back_gs;
    Switch gen_st_audio, gen_st_offer;

    Button setting_btn_local, setting_btn_travel;
    String btn_selected = "local";
    Button setting_bt_radius, setting_bt_radiusgeneral;

    TextView localsetting_tv_equ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_setting);

        lv_list = (ListView) findViewById(R.id.lv_list);
        iv_back_ls = (ImageView) findViewById(R.id.iv_back_ls);
        controller = new SQLiteWander(SettingActivity.this);
        cc = new CommonClass(getApplicationContext());
        tcs = new TimecheckService();
        tv_offer_freq = (TextView) findViewById(R.id.tv_offer_freq);
        travel_sp_freq = (SeekBar) findViewById(R.id.travel_sp_freq);
        local_sp_freq = (SeekBar) findViewById(R.id.local_sp_freq);
        tv_alert = (TextView) findViewById(R.id.tv_alert);
        tv_range = (TextView) findViewById(R.id.tv_range);
        tv_privacy_polocy = (TextView) findViewById(R.id.tv_privacy_polocy);

        gen_st_audio = (Switch) findViewById(R.id.gen_st_audio);
        gen_st_offer = (Switch) findViewById(R.id.gen_st_offer);

        setting_btn_local = (Button) findViewById(R.id.setting_btn_local);
        setting_btn_travel = (Button) findViewById(R.id.setting_btn_travel);

        setting_bt_radius = (Button) findViewById(R.id.setting_bt_radius);
        setting_bt_radiusgeneral = (Button) findViewById(R.id.setting_bt_radiusgeneral);


        setting_bt_radius.setOnClickListener(this);
        setting_bt_radiusgeneral.setOnClickListener(this);
        setting_btn_local.setOnClickListener(this);
        setting_btn_travel.setOnClickListener(this);


        localsetting_tv_equ = (TextView) findViewById(R.id.localsetting_tv_equ);
        setLocalsetting();

        setting_bt_radius.setText(cc.loadPrefString("local_radius"));
        setting_bt_radiusgeneral.setText(cc.loadPrefString("miles"));

        iv_back_ls.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });

        String text = "<a href='http://wazoomobile.com/privacypolicy'>Privacy Policy</a>";
        tv_privacy_polocy.setText(Html.fromHtml("<font color=#FFFFFF>" + text + "</font>"));
        tv_privacy_polocy.setClickable(true);
        tv_privacy_polocy.setMovementMethod(LinkMovementMethod.getInstance());
        tv_privacy_polocy.setLinksClickable(true);

        localsetting_tv_equ.setText("This is equivalent to receiving a nearby offers once every " + cc.loadPrefString("travel_freq") + " miles traveled");

        travel_sp_freq.setProgress(Integer.parseInt(cc.loadPrefString("travel_freq_position")));
        local_sp_freq.setProgress(Integer.parseInt(cc.loadPrefString("local_freq_position")));

        travel_sp_freq.getProgressDrawable().setColorFilter(Color.parseColor("#F9A042"), Mode.SRC_IN);
        local_sp_freq.getProgressDrawable().setColorFilter(Color.parseColor("#F9A042"), Mode.SRC_IN);

        travel_sp_freq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                switch (seekBar.getProgress()) {

                    case 0:
                        cc.savePrefLong("travel_time_freq", 3600000);
//						cc.savePrefLong("travel_time_freq", 30000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 1 hours ");
                        break;

                    case 1:
                        cc.savePrefLong("travel_time_freq", 14400000);
//					cc.savePrefLong("travel_time_freq", 60000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 4 hours ");
                        break;
                    case 2:
                        cc.savePrefLong("travel_time_freq", 43200000);
//					cc.savePrefLong("travel_time_freq", 90000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 12 hours ");
                        break;
                    case 3:
                        cc.savePrefLong("travel_time_freq", 86400000);
//					cc.savePrefLong("travel_time_freq", 120000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 1 day ");
                        break;
                    case 4:
                        cc.savePrefLong("travel_time_freq", 259200000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 3 days ");
                        break;
                    case 5:
                        cc.savePrefLong("travel_time_freq", 604800000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 1 week ");
                        break;
                    case 6:
                        cc.savePrefLong("travel_time_freq", 604800000 * 2);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 2 weeks ");
                        break;
                    case 7:
                        cc.savePrefLong("travel_time_freq", 604800000 * 4);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 4 weeks ");
                        break;
                }

                tcs.resetFrequency();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                travel_sp_freq.getProgressDrawable().setColorFilter(
                        Color.parseColor("#F9A042"), Mode.SRC_IN);
                int general_value = travel_sp_freq.getProgress();
                cc.savePrefString("travel_freq_position", general_value + "");
                //cc.showToast(general_value+"");
                localsetting_tv_equ.setText("This is equivalent to receiving a nearby offers once every " + general_value + " miles traveled");
            }
        });

        local_sp_freq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

//				long mainFreq = 60000;

                switch (seekBar.getProgress()) {
                    case 0:
                        cc.savePrefLong("local_time_freq", 3600000);  // 3600000
//						cc.savePrefLong("local_time_freq", mainFreq * 1);  // 3600000
                        cc.showToast("This is equivalent to receiving a nearby offers once every 1 hours ");
                        break;
                    case 1:
                        cc.savePrefLong("local_time_freq", 14400000);  // 14400000
//					cc.savePrefLong("local_time_freq", mainFreq * 3);  // 14400000
                        cc.showToast("This is equivalent to receiving a nearby offers once every 4 hours ");
                        break;
                    case 2:
                        cc.savePrefLong("local_time_freq", 43200000);
//					cc.savePrefLong("local_time_freq", mainFreq * 5);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 12 hours ");
                        break;
                    case 3:
                        cc.savePrefLong("local_time_freq", 86400000);
//					cc.savePrefLong("local_time_freq", mainFreq * 7);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 1 day ");
                        break;
                    case 4:
                        cc.savePrefLong("local_time_freq", 259200000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 3 days ");
                        break;
                    case 5:
                        cc.savePrefLong("local_time_freq", 604800000);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 1 week ");
                        break;
                    case 6:
                        cc.savePrefLong("local_time_freq", 604800000 * 2);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 2 weeks ");

                        break;
                    case 7:
                        cc.savePrefLong("local_time_freq", 604800000 * 4);
                        cc.showToast("This is equivalent to receiving a nearby offers once every 4 weeks ");
                        break;
                }
                tcs.resetFrequency();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                local_sp_freq.getProgressDrawable().setColorFilter(
                        Color.parseColor("#F9A042"), Mode.SRC_IN);
                int general_value = local_sp_freq.getProgress();
                cc.savePrefString("local_freq_position", general_value + "");
                //cc.showToast(progress+"");

            }
        });

        // Choose audio alert for offer On or Off
        gen_st_audio.setChecked(cc.loadPrefBoolean("isaudio"));

        // Choose offer setting On or Off
        gen_st_offer.setChecked(cc.loadPrefBoolean("isoffer"));

        gen_st_audio.setOnCheckedChangeListener(this);
        gen_st_offer.setOnCheckedChangeListener(this);

    }

    // Adapter class for list view
    public class LocalSettingAdapter extends ArrayAdapter<local_setting> {
        private LayoutInflater mInflater;
        Context mContext;
        ArrayList<local_setting> alist;
        SQLiteWander controller;

        @SuppressWarnings("unchecked")
        public LocalSettingAdapter(Context paramContext, int paramInt,
                                   ArrayList<local_setting> list) {
            super(paramContext, paramInt, list);
            // TODO Auto-generated constructor stub

            this.mContext = paramContext;
            this.alist = ((ArrayList) list);
            controller = new SQLiteWander(mContext);
            mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Log.e("Restaurent_list", alist.size() + "");
        }

        private class Holder {

            public TextView tv_hotel;

            // /public Slider slider_sl_hotel;
            public SeekBar seekBar1;
            public CheckBox switches_cb1;

        }

        public int getCount() {
            return this.alist.size();
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final Holder holder;

            convertView = mInflater.inflate(R.layout.local_setting_row, parent,
                    false);
            if (convertView != null) {

                holder = new Holder();

                holder.tv_hotel = (TextView) convertView.findViewById(R.id.tv_hotel);

                holder.seekBar1 = (SeekBar) convertView.findViewById(R.id.seekBar1);

                convertView.setTag(holder);

            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.tv_hotel.setText(alist.get(position).getCategory_name());
            holder.seekBar1.setProgress(Integer.parseInt(alist.get(position).getSlider_value()));
            holder.seekBar1.getProgressDrawable().setColorFilter(Color.parseColor("#F9A042"), Mode.SRC_IN);

            // category details seekbar
            holder.seekBar1
                    .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar arg0) {
                            // TODO Auto-generated method stub
                            Log.d("onStopTrackingTouch", "onStopTrackingTouch");

                            if (btn_selected.equals("local")) {
                                // category data Update in db table and save in database
                                controller.updateLocalSetting(
                                        alist.get(position).getCategory_name(),
                                        String.valueOf(arg0.getProgress()));
                            } else {
                                controller.updateTravelSetting(
                                        alist.get(position).getCategory_name(),
                                        String.valueOf(arg0.getProgress()));
                            }
                            cc.savePrefBoolean("Counter", true);
                            cc.savePrefString("Category", alist.get(position).getCategory_name());
                            cc.showToast(alist.get(position).getCategory_name() + " has been successfully updated");

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onProgressChanged(SeekBar arg0, int arg1,
                                                      boolean arg2) {
                            // TODO Auto-generated method stub
                            Log.d("onProgressChanged", "onProgressChanged");
                            holder.seekBar1.getProgressDrawable().setColorFilter(Color.parseColor("#F9A042"), Mode.SRC_IN);

                        }
                    });

            return convertView;
        }

    }

    @Override
    public void onCheckedChanged(Switch view, boolean checked) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.gen_st_audio:
                if (checked) {
                    cc.savePrefBoolean("isaudio", true);
                } else {
                    cc.savePrefBoolean("isaudio", false);
                }

                break;
            case R.id.gen_st_offer:
                if (checked) {
                    cc.savePrefBoolean("isoffer", true);
                } else {
                    cc.savePrefBoolean("isoffer", false);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.setting_btn_travel:
                setTravelsetting();
                setting_btn_local.setBackgroundResource(R.drawable.switch_left);
                setting_btn_travel.setBackgroundResource(R.drawable.switch_right_active);
                btn_selected = "travel";
                break;
            case R.id.setting_btn_local:
                setLocalsetting();
                setting_btn_local.setBackgroundResource(R.drawable.switch_left_active);
                setting_btn_travel.setBackgroundResource(R.drawable.switch_right);
                btn_selected = "local";
                break;
            case R.id.setting_bt_radius:
                opendialog();
                break;
            case R.id.setting_bt_radiusgeneral:
                showAlertForGeneralbuss();
                break;
        }
    }


    public void setLocalsetting() {

        // Retrieve sub category data from db table with use of cursor
        Cursor c2 = controller.getLocalSetting();
        int localSet = c2.getCount();
        c2.moveToFirst();
        alist = new ArrayList<local_setting>();
        Log.e("Size", c2.getCount() + "");
        do {
            try {
                String d1 = c2.getString(1);
                String d2 = c2.getString(2);
                String d3 = c2.getString(3);

                local_setting data = new local_setting();

                data.setCategory_id(d1);
                data.setCategory_name(d2);
                data.setSlider_value(d3);


                alist.add(data);
                Log.e("alist size cursor", alist.size() + "");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // do what ever you want here
        } while (c2.moveToNext());

        LocalSettingAdapter adapter = new LocalSettingAdapter(
                getApplicationContext(), 0, alist);
        lv_list.setAdapter(adapter);
    }

    public void setTravelsetting() {

        // Retrieve sub category data from db table with use of cursor
        Cursor c2 = controller.getTravelSetting();
        int localSet = c2.getCount();
        c2.moveToFirst();
        alist = new ArrayList<local_setting>();
        Log.e("Size", c2.getCount() + "");
        do {
            try {
                String d1 = c2.getString(1);
                String d2 = c2.getString(2);
                String d3 = c2.getString(3);

                local_setting data = new local_setting();

                data.setCategory_id(d1);
                data.setCategory_name(d2);
                data.setSlider_value(d3);


                alist.add(data);
                Log.e("alist size cursor", alist.size() + "");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // do what ever you want here
        } while (c2.moveToNext());

        LocalSettingAdapter adapter = new LocalSettingAdapter(
                getApplicationContext(), 0, alist);
        lv_list.setAdapter(adapter);
    }

    private void opendialog() {
        // TODO Auto-generated method stub
        showAlert();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_row_list, null);
        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        ListView listView1 = (ListView) dialogView.findViewById(R.id.listView1);
        final ArrayList<String> radiusList = new ArrayList<String>();
        radiusList.add("10");
        radiusList.add("20");
        radiusList.add("30");
        radiusList.add("40");
        radiusList.add("50");
        radiusList.add("100");
        radiusList.add("150");
        radiusList.add("250");
        radiusList.add("500");

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, radiusList);
        listView1.setAdapter(arrayAdapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String s = radiusList.get(position);
                setting_bt_radius.setText(s);
                cc.savePrefString("local_radius", s);
                alert.dismiss();

            }
        });
        alert.show();
    }

    private void showAlertForGeneralbuss() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_row_list, null);
        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        ListView listView1 = (ListView) dialogView.findViewById(R.id.listView1);
        final ArrayList<String> radiusList = new ArrayList<String>();
        radiusList.add("0.25");
        radiusList.add("0.5");
        radiusList.add("1");
        radiusList.add("2");
        radiusList.add("3");
        radiusList.add("5");
        radiusList.add("10");
        radiusList.add("25");

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, radiusList);
        listView1.setAdapter(arrayAdapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String s = radiusList.get(position);
                setting_bt_radiusgeneral.setText(s);
                cc.savePrefString("miles", s);
                setting_bt_radiusgeneral.setText(cc.loadPrefString("miles"));

                alert.dismiss();

            }
        });
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("@@@Here","goToMainActivity");
//        new LoadMainScreen().execute();
        Intent mIntent = new Intent(SettingActivity.this,
                SideMenuActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }
}
