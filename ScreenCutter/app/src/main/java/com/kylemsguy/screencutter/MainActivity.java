package com.kylemsguy.screencutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kylemsguy.screencutter.backend.AzureOcr;
import com.kylemsguy.screencutter.backend.OcrDecoder;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Uri uriFile;
    private Bitmap imgBitmap;

    // TODO abstract this to use tesseract
    private OcrDecoder azureDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uriFile = getIntent().getData();
        File imgFile = new File(uriFile.getPath());

        imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        // Got screenshot. Send to Azure.
        azureDecoder = new AzureOcr();
        azureDecoder.decodeImageToTextAsync(imgBitmap, new OcrDecoder.OcrCallback() {
            @Override
            public void onResult(String data) {
                onOcrReturn(data);
            }
        });	
    }

    private void onOcrReturn(String data){
        // decode response
        // deactivate progress bar
        // show image with text & boxes around text (or MVP, just show the text
    }

    

}
