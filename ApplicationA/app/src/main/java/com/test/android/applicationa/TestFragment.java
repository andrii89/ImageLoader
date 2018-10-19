package com.test.android.applicationa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.android.applicationa.database.AppDatabase;

public class TestFragment extends Fragment {

    private final String EXTRA_LINK_URL = "image_link";

    EditText mLinkEditText;
    Button mButton;
    private String mImageLink;

    private AppDatabase mDb;

    public static TestFragment newInstance(){

        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.test_tab, container, false);

        mDb = AppDatabase.getInstance(getContext());

        mLinkEditText = view.findViewById(R.id.link_edit_text);
        mLinkEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    InputMethodManager imm = (InputMethodManager)   getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mLinkEditText.getWindowToken(), 0);
                }
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager)   getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mLinkEditText, InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });

        mButton = view.findViewById(R.id.button);
        mButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                mImageLink = mLinkEditText.getText().toString();

                ComponentName cn = new ComponentName("com.test.android.applicationb", "com.test.android.applicationb.MainActivity");
                Intent intent = new Intent();
                intent.setComponent(cn);
                intent.putExtra(EXTRA_LINK_URL, mImageLink);
                try{
                    getContext().startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Unable to launch App B", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
