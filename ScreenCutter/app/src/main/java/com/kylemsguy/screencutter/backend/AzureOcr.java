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

    public String decodeImageToText(Bitmap bitmap) throws VisionServiceException, IOException {
        return process();
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        String result = gson.toJson(ocr);
        Log.d("result", result);

        return result;
    }

    private class DoSendToOCR extends AsyncTask<Void, Void, String> {
        private Exception e;

        @Override
        protected String doInBackground(Void... nothing) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            OcrCallback toCall = callback;
            callback = null;
            bitmap = null;

            toCall.onResult(data);
        }
    }
}
