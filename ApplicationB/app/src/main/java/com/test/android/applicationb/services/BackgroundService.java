package com.test.android.applicationb.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class BackgroundService extends IntentService {
    public static final String ACTION = "com.test.android.applicationb.receivers.ResponseBroadcastReceiver";
    private static final String TAG = "BACKGROUND_SERVICE";

    private final String EXTRA_RESULT_CODE = "result_code";

    public BackgroundService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG,"Service running");
        Bundle extras = intent.getExtras();
        int id;
        Intent toastIntent= new Intent(ACTION);
        toastIntent.putExtra(EXTRA_RESULT_CODE, Activity.RESULT_OK);
        if (extras != null){
            if(extras.get("EXTRA_LINK_ID")!=null){
                id =  extras.getInt("EXTRA_LINK_ID");
                toastIntent.putExtra("EXTRA_LINK_ID",id);
            }
        }
        sendBroadcast(toastIntent);
    }
}