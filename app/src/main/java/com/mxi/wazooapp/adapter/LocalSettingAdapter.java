package com.mxi.wazooapp.adapter;/*package com.mxi.wander.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mxi.wander.GeneralSetActivity;
import com.mxi.wander.R;
import com.mxi.wander.db.SQLiteWander;
import com.mxi.wander.model.categorya_list;
import com.mxi.wander.network.JsonGetData;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Slider;

public class LocalSettingAdapter extends ArrayAdapter<categorya_list> {
	private LayoutInflater mInflater;
	Context mContext;
	ArrayList<categorya_list> alist;
	SQLiteWander controller;

	@SuppressWarnings("unchecked")
	public LocalSettingAdapter(Context paramContext, int paramInt,
			ArrayList<categorya_list> list) {
		super(paramContext, paramInt, list);
		// TODO Auto-generated constructor stub

		this.mContext = paramContext;
		this.alist = ((ArrayList) list);

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Log.e("Restaurent_list", alist.size() + "");
	}

	private static class Holder {

		public TextView tv_hotel;
		public Slider slider_sl_hotel;
		public CheckBox switches_cb1;

	}

	public int getCount() {
		return this.alist.size();
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		convertView = mInflater.inflate(R.layout.local_setting_row, parent,
				false);
		if (convertView != null) {
			controller = new SQLiteWander(mContext);
			holder = new Holder();

			holder.tv_hotel = (TextView) convertView
					.findViewById(R.id.tv_hotel);
			holder.slider_sl_hotel = (Slider) convertView
					.findViewById(R.id.slider_sl_hotel);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_hotel.setText(alist.get(position).getCategory_name());
		Log.e("local_list", alist.get(position).getCategory_name());
		Log.e("local_list size", alist.size() + "");

		holder.slider_sl_hotel.getValue();
		holder.slider_sl_hotel
				.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {

					String slider_value;

					@Override
					public void onPositionChanged(Slider view,
							boolean fromUser, float oldPos, float newPos,
							int oldValue, int newValue) {

						slider_value = Integer.toString(newValue);

						Cursor cur = null;
						cur = controller.getLocalSetting();
						String category_id = null;
						String cat_id = null;
						if (!cur.equals(0)) {

							if (cur.moveToFirst()) {
								do {
									if (!category_id.equals(0)) {
										category_id = cur.getString(cur
												.getColumnIndex("category_id"));
										cat_id = cur.getString(cur
												.getColumnIndex("cat_id"));
									} else {

										controller.insertLocalSetting(
												category_id, cat_id,
												slider_value);

									}

									// do what ever you want here
								} while (cur.moveToNext());
							} else {
								controller.insertLocalSetting(category_id,
										cat_id, slider_value);
							}
						}
						cur.close();

						Log.e("insert database local value", slider_value);
						// Log.e("insert database local valuehhh", cat_id);

					}
				});
		Intent in = new Intent(getContext(), GeneralSetActivity.class);
		in.putExtra("position", position);
		// getContext().startActivity(in);

		return convertView;
	}

}
*/