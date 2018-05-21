package com.mxi.wazooapp.network;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mxi.wazooapp.OffersInserService;
import com.mxi.wazooapp.businesslogic.TimecheckService;

/**
 * Created by parth on 17/6/16.
 */
public class NetworkChangeReceiverWazoo extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        CommonClass cc = new CommonClass(context);

      //  cc.showToast("receiver call");

        try {
            if (isOnline(context)){

                if (!isMyServiceRunning(TimecheckService.class,context)) {
                    Intent mIntent = new Intent(context, TimecheckService.class);
                    context.startService(mIntent);
//                context.startService(new Intent(context, OffersInserService.class));
                    // cc.showToast("seervice started");
                }else {
//                context.startService(new Intent(context, OffersInserService.class));
                }
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
