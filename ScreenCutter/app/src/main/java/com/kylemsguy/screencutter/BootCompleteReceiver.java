package com.kylemsguy.screencutter;

import android.content.*;

public class BootCompleteReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        NotificationMaker.showNotificationIfNeeded(context);
    }
}