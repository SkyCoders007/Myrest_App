package com.mxi.wazooapp.network;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

public class CommonClass {

	private Context _context;
	SharedPreferences pref;
	public boolean isDebug;
	public static boolean isShowMap = false;

	public CommonClass(Context context) {
		this._context = context;

		pref = _context.getSharedPreferences("MyPrefWander",
				_context.MODE_PRIVATE);
		isDebug = false;
	}
	
	public boolean isConnectingToInternet() {
		
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
	
	// Toast method
	public void showToast(String text) {
		// TODO Auto-generated method stub
		Toast.makeText(_context, text, Toast.LENGTH_LONG).show();
	}

	public void showSnackbar(View coordinatorLayout, String text) {

		Snackbar
				.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
	}

	// Save String data in SharedPreferences
	public void savePrefString(String key, String value) {
		// TODO Auto-generated method stub
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	// Save Boolean data in SharedPreferences
	public void savePrefBoolean(String key, Boolean value) {
		// TODO Auto-generated method stub
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
  //REtrieve String data from SharedPreferences
	public String loadPrefString(String key) {
		// TODO Auto-generated method stub
		String strSaved = pref.getString(key, "");
		return strSaved;
	}
	
	//REtrieve boolean data from SharedPreferences
	public Boolean loadPrefBoolean(String key) {
		// TODO Auto-generated method stub
		boolean isbool = pref.getBoolean(key, false);
		return isbool;
	}
	
	// Save Int data in SharedPreferences
		public void savePrefInt(String key, int value) {
			// TODO Auto-generated method stub
			Editor editor = pref.edit();
			editor.putInt(key, value);
			editor.commit();
		}
		
	//REtrieve String data from SharedPreferences
		public int loadPrefInt(String key) {
			// TODO Auto-generated method stub
			int strSaved = pref.getInt(key, 0);
			return strSaved;
		}

	// Save Int data in SharedPreferences
	public void savePrefLong(String key, long value) {
		// TODO Auto-generated method stub
		Editor editor = pref.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	//REtrieve String data from SharedPreferences
	public long loadPrefLong(String key) {
		// TODO Auto-generated method stub
		long strSaved = pref.getLong(key, 0);
		return strSaved;
	}
		
	public void logoutapp() {
		// TODO Auto-generated method stub
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	public String MyText(String text){
		String s = "";
		try {
			s = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;

	}
	
	public long getLongAfterThreeMinute() {
		
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.MINUTE, 3);
		dt = c.getTime();
		Long date = dt.getTime();
		
		return date;
		
	}

	public boolean appInstalledOrNot(Context mContext, String uri) {
		PackageManager pm = mContext.getPackageManager();
		boolean app_installed;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	/**
	 * Is the screen of the device on.
	 * @param context the context
	 * @return true when (at least one) screen is on
	 */
	public boolean isScreenOn(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
			boolean screenOn = false;
			for (Display display : dm.getDisplays()) {
				if (display.getState() != Display.STATE_OFF) {
					screenOn = true;
				}
			}
			return screenOn;
		} else {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			//noinspection deprecation
			return pm.isScreenOn();
		}
	}
}
