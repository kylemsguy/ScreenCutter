package com.kylemsguy.screencutter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kylemsguy.screencutter.backend.AzureOcr;
import com.kylemsguy.screencutter.backend.OcrDecoder;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri uriFile;
    private Bitmap imgBitmap;

    private OCR ocrData;

    private RelativeLayout progressBarLayout;
    private RelativeLayout mainIfLayout;
    private TextView debugTextView;
    private ImageView screenImageView;
    private TextView previewTextView;

    private List<View> textItems;
    private List<View> selectedItems = new ArrayList<View>();
    private int mShortAnimationDuration;

    // TODO abstract this to use tesseract
    private OcrDecoder azureDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarLayout = (RelativeLayout) findViewById(R.id.contentLoadingLayout);
        mainIfLayout = (RelativeLayout) findViewById(R.id.mainIfLayout);
        debugTextView = (TextView) findViewById(R.id.debugFullScreenDecoded);
        screenImageView = (ImageView) findViewById(R.id.screenImage);
        previewTextView = (TextView) findViewById(R.id.output_text);

        // should be already toggled by default
        toggleLoading();

        uriFile = getIntent().getData();
        File imgFile = new File(uriFile.getPath());

        imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        // Got screenshot. Send to Azure.
        azureDecoder = new AzureOcr();
        azureDecoder.decodeImageToTextAsync(imgBitmap, new OcrDecoder.OcrCallback() {
            @Override
            public void onResult(Object data) {
                onOcrReturn(data);
            }
        });

        screenImageView.setImageBitmap(imgBitmap);

        textItems = new ArrayList<>();
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    private void onOcrReturn(Object data){
        // decode response
        // deactivate progress bar
        toggleLoading();
        // show image with text & boxes around text (or MVP, just show the text)
        if(data instanceof String){
            debugTextView.setText((String) data);
            System.out.println(data);
            return;
        } else {
            ocrData = (OCR) data;
        }

        populateOcrData();

        // note: when box is tapped, open a messagebox that allows person to select text and copy
    }


    private void populateOcrData(){
        for(Region r : ocrData.regions){
            for(Line l : r.lines) {
                View v = new View(this);
                int[] boundingBox = getScaledBoundingBox(l.boundingBox);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(boundingBox[2], boundingBox[3]);
                params.leftMargin = boundingBox[0];
                params.topMargin = boundingBox[1];
                v.setLayoutParams(params);
                v.setBackgroundColor(0x8000FFFF);//0x8000FFFF

                StringBuilder sb = new StringBuilder();
                for(Word w : l.words){
                    sb.append(w.text);
                    sb.append(" ");
                }

                final String joinedLine = sb.toString();
                v.setTag(joinedLine);

                v.setOnClickListener(this);

                mainIfLayout.addView(v);
                textItems.add(v);
            }
        }
    }

    public void onClick(View v) {
        boolean newSelected = !v.isSelected();
        v.setSelected(newSelected);
        v.setBackgroundColor(newSelected? 0x400080ff: 0x8000ffff);
        if (newSelected) {
            selectedItems.add(v);
            addFlyOutViewAnimation(v);
        } else {
            selectedItems.remove(v);
        }
        updateTextbox();
    }

    private int[] getScaledBoundingBox(String boundingBox){
        double scaleFactor = (double) ((View) mainIfLayout.getParent()).getHeight() / imgBitmap.getHeight();
        String[] bbox = boundingBox.split(",");
        int[] intbox = new int[4];
        intbox[0] = (int)((Integer.parseInt(bbox[0]) - imgBitmap.getWidth() / 2) * scaleFactor + ((View) mainIfLayout.getParent()).getWidth() / 2);
        intbox[1] = (int)(Integer.parseInt(bbox[1]) * scaleFactor);
        intbox[2] = (int)(Integer.parseInt(bbox[2]) * scaleFactor);
        intbox[3] = (int)(Integer.parseInt(bbox[3]) * scaleFactor);
        return intbox;
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

    private void updateTextbox() {
        previewTextView.setText(getSelectedString());
    }

    private String getSelectedString() {
        Collections.sort(selectedItems, new Comparator<View>() {
            public int compare(View v1, View v2) {
                float y1 = v1.getY(); float y2 = v2.getY();
                if (y1 < y2) {
                    return -1;
                } else if (y1 > y2) {
                    return 1;
                } else {
                    float x1 = v1.getX(); float x2 = v2.getX();
                    return x1 == x2? 0: (x1 < x2? -1 : 1);
                }
            }
        });

        StringBuilder b = new StringBuilder();
        boolean already = false;
        for (View v: selectedItems) {
            if (!already) {
                already = true;
            } else {
                b.append('\n');
            }
            b.append((String)v.getTag());
        }
        return b.toString();
    }

    public void onCopyOutputClicked(View v) {
        ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(null, getSelectedString()));
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addFlyOutViewAnimation(View origV) {
        final TextView v = new TextView(this);
        v.setText((String)origV.getTag());
        v.setX(origV.getX());
        v.setY(origV.getY());
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(v, View.X, -mainIfLayout.getWidth()));
        set.setDuration(mShortAnimationDuration*2);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainIfLayout.removeView(v);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mainIfLayout.removeView(v);
            }
        });
        set.start();
        mainIfLayout.addView(v);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        finish();
    }
}
