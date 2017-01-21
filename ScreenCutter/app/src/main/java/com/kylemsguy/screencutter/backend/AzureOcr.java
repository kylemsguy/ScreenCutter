package com.kylemsguy.screencutter.backend;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by kyle on 21/01/17.
 */

public class AzureOcr implements OcrDecoder {

    private VisionServiceClient client;
    private Bitmap bitmap;
    private OcrCallback callback;

    public void decodeImageToTextAsync(Bitmap bitmap, OcrCallback callback) {
        if (client == null) {
            client = new VisionServiceRestClient(AzureOcrKeyStore.key1);
        }

        this.bitmap = bitmap;
        this.callback = callback;

        // kick off OCR :D
        new DoSendToOCR().execute();

    }

    public Object decodeImageToText(Bitmap bitmap) throws VisionServiceException, IOException {
        return process();
    }

    private Object process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);
        return ocr;
    }

    private class DoSendToOCR extends AsyncTask<Void, Void, Object> {
        private Exception e;

        @Override
        protected Object doInBackground(Void... nothing) {
            try {
                return process();
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(Object data) {
            super.onPostExecute(data);
            OcrCallback toCall = callback;
            callback = null;
            bitmap = null;

            toCall.onResult(data);
        }
    }
}
