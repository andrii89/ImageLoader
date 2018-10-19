package com.test.android.applicationb;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_LINK_URL = "image_link";
    public static final String EXTRA_LINK_ID = "link_id";
    String link;
    int id;

    TextView mLinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinkTextView = findViewById(R.id.link_text_view);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            if(extras.get(EXTRA_LINK_URL)!=null){
                link = (String) extras.get(EXTRA_LINK_URL);
                startFragment();
            }
            if(extras.get(EXTRA_LINK_ID)!=null){
                id = extras.getInt(EXTRA_LINK_ID);
                startFragment();
            }
            if ((extras.get(EXTRA_LINK_URL) == null)&&(extras.get(EXTRA_LINK_ID) == null)){
                closeApp();
            }
        } else {
            closeApp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void startFragment(){
        mLinkTextView.setVisibility(View.INVISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = new ImageViewFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }

    private void closeApp(){
        String message = getString(R.string.independent_launch_message, 10);
        mLinkTextView.setText(message);
        new CountDownTimer(10000, 1000){

            public void onTick(long millisUntilFinished){
                String message = getString(R.string.independent_launch_message, millisUntilFinished/1000);
                mLinkTextView.setText(message);
            }

            public void onFinish(){
                finishAndRemoveTask();
            }
        }.start();
    }
}
