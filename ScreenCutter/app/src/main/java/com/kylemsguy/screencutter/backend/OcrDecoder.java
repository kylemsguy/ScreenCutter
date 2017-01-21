package com.kylemsguy.screencutter.backend;

import android.graphics.Bitmap;

/**
 * Created by kyle on 21/01/17.
 */

public interface OcrDecoder {
    void decodeImageToTextAsync(Bitmap bitmap, OcrCallback callback);
    String decodeImageToText(Bitmap bitmap) throws Exception;

    interface OcrCallback{
        // data is a JSON string I think...
        void onResult(String data);
    }
}
