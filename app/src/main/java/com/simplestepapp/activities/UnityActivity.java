package com.simplestepapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.sms.sma.UnityPlayerActivity;

public class UnityActivity extends UnityPlayerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent_Profile=new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent_Profile);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}