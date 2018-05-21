package com.mxi.wazooapp.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mxi.wazooapp.R;
import com.mxi.wazooapp.model.downloads;


public class ReceptsAdapter extends ArrayAdapter<downloads> {
	private LayoutInflater mInflater;
	Context mContext;
	ArrayList<downloads> downloadArrayList;

	@SuppressWarnings("unchecked")
	public ReceptsAdapter(Context paramContext, int paramInt,
			ArrayList<downloads> list) {
		super(paramContext, paramInt, list);
		// TODO Auto-generated constructor stub

		this.mContext = paramContext;
		this.downloadArrayList = ((ArrayList) list);

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	private static class Holder {

		public TextView row_recept_tv_name,row_recept_tv_description,tv_redeem;

	}

	public int getCount() {
		return this.downloadArrayList.size();
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		convertView = mInflater.inflate(R.layout.row_recept, parent, false);
		if (convertView != null) {

			holder = new Holder();
			holder.row_recept_tv_name = (TextView) convertView.findViewById(R.id.row_recept_tv_name);
			holder.row_recept_tv_description = (TextView) convertView.findViewById(R.id.row_recept_tv_description);
			holder.tv_redeem = (TextView) convertView.findViewById(R.id.tv_redeem);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.row_recept_tv_name.setText(downloadArrayList.get(position).name);
		holder.row_recept_tv_description.setText(downloadArrayList.get(position).description);

		if (downloadArrayList.get(position).redeem.equals("1")) {
			holder.tv_redeem.setText("REDEEMED");
			holder.tv_redeem.setTextColor(Color.RED);
		} else {
			holder.tv_redeem.setTextColor(Color.GREEN);
			holder.tv_redeem.setText("NOT VISITED");
		}

		return convertView;
	}

}
