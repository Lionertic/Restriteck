package com.restri_tech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.restri_tech.WelcomeActivity.screen_on;


public class LockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screen_on=true;
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screen_on=false;
            }
            else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                screen_on=true;
            }
        }
    }
}
