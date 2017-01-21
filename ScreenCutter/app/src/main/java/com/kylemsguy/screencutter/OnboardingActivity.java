package com.kylemsguy.screencutter;

import android.app.*;
import android.os.*;
import android.view.*;

public class OnboardingActivity extends Activity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_onboarding);
        NotificationMaker.showNotificationIfNeeded(this);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			findViewById(R.id.onboarding_tile).setVisibility(View.GONE);
		}
    }
}