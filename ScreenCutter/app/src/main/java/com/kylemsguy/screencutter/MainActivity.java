package com.kylemsguy.screencutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Uri uriFile;
    private Bitmap imgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uriFile = getIntent().getData();
        File imgFile = new File(uriFile.getPath());

        imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        // Got screenshot. Send to Azure.

    }

}
