package com.kylemsguy.screencutter;

import android.app.*;
import android.os.*;

public class OnboardingActivity extends Activity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        NotificationMaker.showNotificationIfNeeded(this);
        finish();
    }
}