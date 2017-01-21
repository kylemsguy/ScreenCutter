package com.kylemsguy.screencutter;

import android.content.Intent;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class ScreenshotTileService extends TileService {
    @Override
    public void onClick() {
        boolean isCurrentlyLocked = this.isLocked();
        if (isCurrentlyLocked) return;

        Intent intent = new Intent(getApplicationContext(), ScreenshotActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivityAndCollapse(intent);
	}
}