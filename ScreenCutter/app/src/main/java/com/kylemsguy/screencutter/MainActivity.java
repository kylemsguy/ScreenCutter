package com.kylemsguy.screencutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kylemsguy.screencutter.backend.AzureOcr;
import com.kylemsguy.screencutter.backend.OcrDecoder;

import org.w3c.dom.Text;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Uri uriFile;
    private Bitmap imgBitmap;

    private RelativeLayout progressBarLayout;
    private RelativeLayout mainIfLayout;
    private TextView debugTextView;

    // TODO abstract this to use tesseract
    private OcrDecoder azureDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarLayout = (RelativeLayout) findViewById(R.id.contentLoadingLayout);
        mainIfLayout = (RelativeLayout) findViewById(R.id.mainIfLayout);
        debugTextView = (TextView) findViewById(R.id.debugFullScreenDecoded);

        // should be already toggled by default
        toggleLoading();

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
        toggleLoading();
        // show image with text & boxes around text (or MVP, just show the text)
        debugTextView.setText(data);
        System.out.println(data);
        // note: when box is tapped, open a messagebox that allows person to select text and copy
    }

    private void toggleLoading(){
        if(progressBarLayout.getVisibility() == View.GONE){
            progressBarLayout.setVisibility(View.VISIBLE);
        } else {
            progressBarLayout.setVisibility(View.GONE);
        }

        if(mainIfLayout.getVisibility() == View.GONE){
            mainIfLayout.setVisibility(View.VISIBLE);
        } else {
            mainIfLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
