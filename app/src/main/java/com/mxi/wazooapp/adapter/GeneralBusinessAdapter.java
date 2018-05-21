package com.mxi.wazooapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mxi.wazooapp.R;
import com.mxi.wazooapp.ShowMapActivity;
import com.mxi.wazooapp.model.general_bus;
import com.mxi.wazooapp.network.CommonClass;
import com.mxi.wazooapp.network.GPSTracker;
import com.mxi.wazooapp.network.JsonGetData;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class GeneralBusinessAdapter extends ArrayAdapter<general_bus> {

	GPSTracker gps;
	private LayoutInflater mInflater;
	Context mContext;
	ArrayList<general_bus> generalArrayList;
	String lat, lng;
	// applied changes - 1
	CommonClass cc=new CommonClass(getContext());
	String distance;
	@SuppressWarnings("unchecked")
	public GeneralBusinessAdapter(Context paramContext, int paramInt,
			ArrayList<general_bus> list) {
		super(paramContext, paramInt, list);
		// TODO Auto-generated constructor stub

		this.mContext = paramContext;
		gps = new GPSTracker(mContext);
		this.generalArrayList = ((ArrayList) list);

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	private static class Holder {

		public TextView genbus_tv_name, genbus_tv_address,genbus_tv_tag,
		genbus_tv_distance,genbus_tv_phone;
		public ImageView genbus_iv_image;
		public RatingBar genbus_rb_rating;
		public LinearLayout row_ll_genbusin;
	}

	public int getCount() {
		return this.generalArrayList.size();
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		convertView = mInflater.inflate(R.layout.row_general_business, parent, false);
		if (convertView != null) {

			holder = new Holder();

			holder.genbus_tv_name = (TextView) convertView.findViewById(R.id.genbus_tv_name);
			holder.genbus_tv_address = (TextView) convertView.findViewById(R.id.genbus_tv_address);
			holder.genbus_tv_tag = (TextView) convertView.findViewById(R.id.genbus_tv_tag);
			holder.genbus_tv_distance = (TextView) convertView.findViewById(R.id.genbus_tv_dist);
			holder.genbus_tv_phone = (TextView) convertView.findViewById(R.id.genbus_tv_phone);

			holder.genbus_iv_image = (ImageView)convertView.findViewById(R.id.genbus_iv_image);
			holder.genbus_rb_rating = (RatingBar)convertView.findViewById(R.id.genbus_rb_rating);
			holder.row_ll_genbusin = (LinearLayout)convertView.findViewById(R.id.row_ll_genbusin);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.genbus_tv_name.setText(generalArrayList.get(position).getName());
		holder.genbus_tv_address.setText(generalArrayList.get(position).getAddress());
		holder.genbus_tv_tag.setText(generalArrayList.get(position).getTag());
		Log.d("Tag",generalArrayList.get(position).getTag());
		//changes applied - 1 condition for contrywise distance
		DecimalFormat precision = new DecimalFormat("0.00");
		double dist= Double.parseDouble(generalArrayList.get(position).getDistance());
		if(cc.loadPrefString("Country").equals("India")){

			dist=dist * 1.609344;

			distance=precision.format(dist)+" km away";
		}else{
			distance=precision.format(dist)+" m away";
		}
		holder.genbus_tv_distance.setText(distance);
		holder.genbus_tv_phone.setText("Phone : "+generalArrayList.get(position).getPhone());
		
		if (!generalArrayList.get(position).getImage().equals("")) {
			Picasso.with(mContext)
			.load(generalArrayList.get(position).getImage())
			.error(R.drawable.ni_image)
			.placeholder(R.drawable.ni_image)
			.into(holder.genbus_iv_image);
		}else {
			holder.genbus_iv_image.setImageResource(R.drawable.ni_image);
		}

		if (!generalArrayList.get(position).getRating().equals("")) {
			holder.genbus_rb_rating.setRating(Float.parseFloat(generalArrayList.get(position).getRating()));
		}else {
			holder.genbus_rb_rating.setRating(0);
		}
		
		holder.row_ll_genbusin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					boolean isAppInstalled =  cc.appInstalledOrNot(mContext, "com.google.android.apps.maps");
					Log.e("Dhaval ", "google map android app is installe = " + isAppInstalled);

					if(isAppInstalled){
						Intent directioIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse("http://maps.google.com/maps?daddr="+ generalArrayList.get(position).getPlace_lat() + "," + generalArrayList.get(position).getPlace_long()));
						directioIntent.setClassName("com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity");
						mContext.startActivity(directioIntent);
					} else {
						String baseURL = "http://maps.google.com/maps?";
						String fromLocation = "saddr=" + gps.getLatitude() + "," + gps.getLongitude();
						String toLocation = "&daddr=" + generalArrayList.get(position).getPlace_lat() + "," + generalArrayList.get(position).getPlace_long();
						String mapUrl = baseURL + fromLocation + toLocation;
						Log.e("Dhaval ", "Map URL = " + mapUrl);

						Intent mapIntent = new Intent(mContext, ShowMapActivity.class);
						mapIntent.putExtra("MAP_URL", mapUrl);
						mContext.startActivity(mapIntent);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		return convertView;
	}

}
