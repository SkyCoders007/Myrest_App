package com.mxi.wazooapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mxi.wazooapp.businesslogic.TimecheckService;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ThankYouActivity extends Activity {

    GPSTracker gps;
    CommonClass cc;
    SQLiteWander dbcon;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Button btn_next;
    LinearLayout ll_main;
    boolean isGotItClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        gps = new GPSTracker(ThankYouActivity.this);
        cc = new CommonClass(ThankYouActivity.this);
        dbcon = new SQLiteWander(ThankYouActivity.this);
        ll_main = (LinearLayout) findViewById(R.id.ll_main_thankyou);
        viewPager = (ViewPager) findViewById(R.id.vp_thankyou);

        viewPagerAdapter = new ViewPagerAdapter(ThankYouActivity.this);
        viewPager.setAdapter(viewPagerAdapter);
        btn_next = (Button) findViewById(R.id.btn_next);


        cc.savePrefString("home_lat", gps.getLatitude() + "");
        cc.savePrefString("home_lng", gps.getLongitude() + "");
        cc.savePrefString("local_radius", "50");

        cc.savePrefString("travel_freq_position", "3");
        cc.savePrefString("local_freq_position", "3");
        cc.savePrefLong("local_time_freq", 86400000);
        cc.savePrefLong("travel_time_freq", 86400000);
        cc.savePrefString("Category", "Restaurants");

        cc.savePrefString("last_offer_time", "0");
        cc.savePrefInt("hotel_pref", 3);
        cc.savePrefBoolean("Counter", false);
        cc.savePrefBoolean("First_time", true);

        cc.savePrefBoolean("isOfferFound", true);

        cc.savePrefString("miles", "3");
        cc.savePrefString("partner_miles", "2");
        dbcon.updateDefaultPref();
//        viewPager.setHorizontalScrollBarEnabled(false);
        if (isGotItClicked) {
//            viewPager.setHorizontalScrollBarEnabled(true);
        }
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in = new Intent(ThankYouActivity.this,
//                        SideMenuActivity.class);
                Intent in = new Intent(ThankYouActivity.this,
                        OfferActivity.class);
                startActivity(in);
                finish();

            }
        });
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGotItClicked = true;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                Log.e("Position_VP", position + "");

                if (position == 5) {

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            viewPager.setVisibility(View.INVISIBLE);
                            ll_main.setVisibility(View.VISIBLE);
                        }
                    }, 1500);
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class ViewPagerAdapter extends PagerAdapter {
        int[] mResources = {
                R.drawable.img_1,
                R.drawable.img_5,
                R.drawable.img_4,
                R.drawable.img_3,
                R.drawable.img_6,
                R.drawable.img_2
        };
        Context context;

        public ViewPagerAdapter(Context mContext) {
            this.context = mContext;

        }

        @Override
        public int getCount() {
            Log.e("TotalImages", mResources.length + "");
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup parent, final int position) {


            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_row, parent, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_view_pager_row);
            ImageView imageView1 = (ImageView) itemView.findViewById(R.id.iv_swip_left);
            Log.e("Image", mResources[position] + "");
            if(position == 0){
                imageView1.setVisibility(View.VISIBLE);
            } else {
                imageView1.setVisibility(View.GONE);
            }
            Picasso.with(context)
                    .load(mResources[position])
                    .into(imageView);
            ((ViewPager) parent).addView(itemView, 0);
            return itemView;
        }

        public void destroyItem(ViewGroup parent, int position, Object object) {

        }

    }

}
