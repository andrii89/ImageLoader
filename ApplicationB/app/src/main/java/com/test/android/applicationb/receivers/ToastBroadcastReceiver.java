package com.test.android.applicationb.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.test.android.applicationb.services.BackgroundService;

public class ToastBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int id;
        Intent serviceIntent= new Intent(context,BackgroundService.class);
        if (extras != null){
            if(extras.get("EXTRA_LINK_ID")!=null){
                id =  extras.getInt("EXTRA_LINK_ID");
                serviceIntent.putExtra("EXTRA_LINK_ID",id);
            }
        }
        context.startService(serviceIntent);

    }
}
