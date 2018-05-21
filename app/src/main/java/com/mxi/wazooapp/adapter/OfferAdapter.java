package com.mxi.wazooapp.adapter;/*package com.mxi.wander.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mxi.wander.LocationDetectService;
import com.mxi.wander.R;
import com.mxi.wander.model.offers;

public class OfferAdapter extends ArrayAdapter<offers> {
	private LayoutInflater mInflater;
	Context mContext;
	ArrayList<offers> offerArrayList;
	String lat, lng;

	@SuppressWarnings("unchecked")
	public OfferAdapter(Context paramContext, int paramInt,
			ArrayList<offers> list) {
		super(paramContext, paramInt, list);
		// TODO Auto-generated constructor stub

		this.mContext = paramContext;
		this.offerArrayList = ((ArrayList) list);

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Log.e("offer_list", offerArrayList.size() + "");
	}

	private static class Holder {
		public WebView webView;
		public TextView tv_offer_desc, tv_offer_address, tv_offer_value;

	}

	public int getCount() {
		return this.offerArrayList.size();
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		convertView = mInflater.inflate(R.layout.offer_row, parent, false);
		if (convertView != null) {

			holder = new Holder();

			holder.webView = (WebView) convertView.findViewById(R.id.webView);

			holder.tv_offer_desc = (TextView) convertView
					.findViewById(R.id.tv_offer_desc);
			holder.tv_offer_address = (TextView) convertView
					.findViewById(R.id.tv_offer_address);
			holder.tv_offer_value = (TextView) convertView
					.findViewById(R.id.tv_offer_value);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_offer_desc.setText(LocationDetectService.offerArrayList.get(
				position).getDescriptions());
		holder.tv_offer_address.setText(LocationDetectService.offerArrayList
				.get(position).getAddress());

		String offer_value = LocationDetectService.offerArrayList.get(position)
				.getOffer_value();
		String name = LocationDetectService.offerArrayList.get(position)
				.getName();
		holder.tv_offer_value.setText("Offer :" + "  " + '"' + offer_value
				+ '"');
		String fontcolor = "#FFC90E";
		String bg2 = "#2c3c40";

		String time = "<body bgcolor =\"" + bg2 + "\"><font color=\""
				+ fontcolor + "\">" + name + "</font></body>";
		holder.webView.loadData(time, "text/html", "UTF-8");
		lat = LocationDetectService.offerArrayList.get(position).getLat();
		lng = LocationDetectService.offerArrayList.get(position).getLng();

		holder.tv_offer_address
				.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						Intent mIntent = new Intent(getContext(),
								LocationDetectService.class);

						mContext.stopService(mIntent);

						return false;
					}
				});
		holder.tv_offer_address.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent directioIntent = new Intent(
							android.content.Intent.ACTION_VIEW, Uri
									.parse("http://maps.google.com/maps?daddr="
											+ lat + "," + lng));
					directioIntent.setClassName("com.google.android.apps.maps",
							"com.google.android.maps.MapsActivity");
					mContext.startActivity(directioIntent);
				} catch (Exception e) {
					Log.e("MAP Error", e.getMessage());
				}

				// finish();

			}
		});

		return convertView;
	}

}
*/