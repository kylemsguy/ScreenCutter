package com.kylemsguy.screencutter.backend;

import android.graphics.Bitmap;

/**
 * Created by kyle on 21/01/17.
 */

public interface OcrDecoder {
    void decodeImageToTextAsync(Bitmap bitmap, Runnable callback);
    Object decodeImageToText(Bitmap bitmap);
}
