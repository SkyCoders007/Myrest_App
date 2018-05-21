package com.mxi.wazooapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HomeActivity extends Fragment implements OnClickListener {

	LinearLayout  ll_local, ll_customer,  ll_offers,
			ll_business,ll_recept;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.home_listview, container,
				false);
		ll_customer = (LinearLayout) rootView.findViewById(R.id.ll_customer);
		ll_local = (LinearLayout) rootView.findViewById(R.id.ll_local);
		ll_offers = (LinearLayout) rootView.findViewById(R.id.ll_offers);
		ll_business = (LinearLayout) rootView.findViewById(R.id.ll_business);
		ll_recept= (LinearLayout)rootView.findViewById(R.id.ll_recept);
		
		ll_customer.setOnClickListener(this);
		ll_local.setOnClickListener(this);
		ll_offers.setOnClickListener(this);
		ll_business.setOnClickListener(this);
		ll_recept.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent mIntent = null;
		switch (v.getId()) {

		case R.id.ll_local:
			mIntent = new Intent(getActivity(), SettingActivity.class);
			break;
		
		case R.id.ll_offers:
			mIntent = new Intent(getActivity(), OfferActivity.class);
			break;
		case R.id.ll_business:
			mIntent = new Intent(getActivity(), GeneralBusiness.class);
			break;
		case R.id.ll_recept:
			mIntent = new Intent(getActivity(), MyRecepts.class);
			break;
			
		}
		startActivity(mIntent);
	}

}
