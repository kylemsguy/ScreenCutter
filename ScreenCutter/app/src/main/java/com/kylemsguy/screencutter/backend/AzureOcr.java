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

    public void decodeImageToTextAsync(Bitmap bitmap, Runnable callback){
        if(client == null) {
            client = new VisionServiceRestClient(AzureOcrKeyStore.key1);
        }

        this.bitmap = bitmap;

        // kick off OCR :D

    }

    public Object decodeImageToText(Bitmap bitmap){
        throw new UnsupportedOperationException();
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

    private class doRequest extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
