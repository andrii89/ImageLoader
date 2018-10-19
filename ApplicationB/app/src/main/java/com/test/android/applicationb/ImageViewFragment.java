package com.test.android.applicationb;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.test.android.applicationb.receivers.ResponseBroadcastReceiver;
import com.test.android.applicationb.receivers.ToastBroadcastReceiver;
import com.test.android.applicationb.services.BackgroundService;
import com.test.android.applicationb.utils.ImageDownloader;

import java.util.Date;

public class ImageViewFragment extends Fragment {

    ResponseBroadcastReceiver broadcastReceiver;

    private static final String TAG = "ImageViewFragment";
    public static final Uri CONTENT_URI = Uri.parse("content://com.test.android.applicationa.database/link");

    public String imageUrl;
    public int linkId;
    public int status;

    private ImageView mItemImageView;

    private ImageDownloader<ImageView> mImageDownloader;

    ContentResolver resolver;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        resolver = getContext().getContentResolver();

        if(getActivity().getIntent().getSerializableExtra(MainActivity.EXTRA_LINK_URL) != null){
            ContentValues mContentValues = new ContentValues();
            imageUrl = (String) getActivity().getIntent().getSerializableExtra(MainActivity.EXTRA_LINK_URL);
            status = 3;
            long timeStamp = new Date().getTime();
            mContentValues.put("link", imageUrl);
            mContentValues.put("status", Integer.toString(status));
            mContentValues.put("open_time", Long.toString(timeStamp));
            Uri uri = resolver.insert(CONTENT_URI, mContentValues);
            long longId = ContentUris.parseId(uri);
            linkId = Integer.valueOf(Long.toString(longId));

        }
        if(getActivity().getIntent().getSerializableExtra(MainActivity.EXTRA_LINK_ID) != null){
            linkId = (Integer) getActivity().getIntent().getSerializableExtra(MainActivity.EXTRA_LINK_ID);
            Uri uri = CONTENT_URI;
            uri = uri.buildUpon().appendPath(Integer.toString(linkId)).build();
            cursor = resolver.query(uri, null, null, null, null);

            while (cursor.moveToNext()){
                imageUrl = cursor.getString(cursor.getColumnIndex("link"));
                status = cursor.getInt(cursor.getColumnIndex("status"));
            }
            if (status == 1){
                broadcastReceiver = new ResponseBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BackgroundService.ACTION);
                getContext().registerReceiver(broadcastReceiver,intentFilter);
                scheduleAlarm(linkId);
            }
        }

        Handler responseHandler = new Handler();
        mImageDownloader = new ImageDownloader<>(responseHandler);
        mImageDownloader.setImageDownloadListener(
                new ImageDownloader.ImageDownloadListener<ImageView>(){
                    @Override
                    public void onImageDownloaded(ImageView imageView, Bitmap bitmap){
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        setDrawable(drawable);
                    }
                }
        );
        mImageDownloader.start();
        mImageDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.image_view,container, false);

        mItemImageView = view.findViewById(R.id.imageview);
        setDrawable(null);
        mImageDownloader.queueImage(mItemImageView, imageUrl);

        return view;
    }

    private void scheduleAlarm(int id) {
        Intent toastIntent = new Intent(getContext(), ToastBroadcastReceiver.class);
        toastIntent.putExtra("EXTRA_LINK_ID", id);
        PendingIntent toastAlarmIntent = PendingIntent.getBroadcast(getContext(), 0, toastIntent,0);
        AlarmManager backupAlarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        backupAlarmMgr.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + 15000, toastAlarmIntent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mImageDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mImageDownloader.clearQueue();
    }

    private void setDrawable(Drawable drawable){
        if(drawable == null){
            mItemImageView.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
            status = 2;
        } else {
            mItemImageView.setImageDrawable(drawable);
            status = 1;
        }
        update(linkId, imageUrl, status);
    }

    private void update(int id, String link, int status){
        ContentValues mContentValues = new ContentValues();
        long timeStamp = new Date().getTime();
        mContentValues.put("link", link);
        mContentValues.put("status", Integer.toString(status));
        mContentValues.put("open_time", Long.toString(timeStamp));
        Uri uri = CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(id)).build();
        resolver.update(uri, mContentValues, null, null);
    }
}
