package com.mxi.wazooapp.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mxi.wazooapp.R;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.scategory_list;

public class MenuListAdapter extends ArrayAdapter<scategory_list> {
	private LayoutInflater mInflater;
	Context mContext;
	ArrayList<scategory_list> subcatlistarraylist;
	SQLiteWander controller;

	@SuppressWarnings("unchecked")
	public MenuListAdapter(Context paramContext, int paramInt,
			ArrayList<scategory_list> list) {
		super(paramContext, paramInt, list);
		// TODO Auto-generated constructor stub

		this.mContext = paramContext;
		this.subcatlistarraylist = ((ArrayList) list);

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		controller = new SQLiteWander(mContext);
	}

	private static class Holder {

		public TextView tv_pizza;
		// public Slider slider_sl_res;
		public SeekBar seekBar1;
		public CheckBox switches_cb1;

	}

	public int getCount() {
		return this.subcatlistarraylist.size();
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		convertView = mInflater.inflate(R.layout.restautent_row, parent, false);
		
		if (convertView != null) {
			
			holder = new Holder();

			holder.tv_pizza = (TextView) convertView
					.findViewById(R.id.tv_pizza);
			holder.seekBar1 = (SeekBar) convertView.findViewById(R.id.seekBar1);

			holder.switches_cb1 = (CheckBox) convertView.findViewById(R.id.switches_cb1);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_pizza.setText(subcatlistarraylist.get(position).getSubcat_name());
		
		// REtrieve category prefer value 
		holder.seekBar1.setProgress(Integer.parseInt(controller.getPrefBySubcatId(subcatlistarraylist.get(position).getSubcat_id())));
		
		holder.seekBar1.getProgressDrawable().setColorFilter(
				Color.parseColor("#F9A042"), Mode.SRC_IN);
		
		holder.seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			// Update category prefer value
				controller.updatePref(subcatlistarraylist.get(position).getSubcat_id(), String.valueOf(arg0.getProgress()));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				holder.seekBar1.getProgressDrawable().setColorFilter(
						Color.parseColor("#F9A042"), Mode.SRC_IN);
				holder.seekBar1.getProgress();
			}
		});
		  /*Intent in = new Intent(getContext(), SecondService.class);
		  in.putExtra("position", position);
		     getContext().startService(in);*/
		
		return convertView;
	}

}
