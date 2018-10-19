package com.test.android.applicationb.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.test.android.applicationb.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.test.android.applicationb.ImageViewFragment.CONTENT_URI;

public class ResponseBroadcastReceiver extends BroadcastReceiver {

    private final String EXTRA_RESULT_CODE = "result_code";

    ContentResolver resolver;

    @Override
    public void onReceive(Context context, Intent intent) {
        resolver = context.getContentResolver();
        int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE,RESULT_CANCELED);
        int linkId = intent.getIntExtra("EXTRA_LINK_ID", 0);
        if (resultCode == RESULT_OK){

            Uri uri = CONTENT_URI;
            uri = uri.buildUpon().appendPath(Integer.toString(linkId)).build();
            resolver.delete(uri, null, null);
            String message = context.getString(R.string.link_deleted_message, linkId);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
