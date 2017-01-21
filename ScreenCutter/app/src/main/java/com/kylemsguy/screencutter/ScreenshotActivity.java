package com.kylemsguy.screencutter;

import java.io.*;
import java.nio.*;

import android.app.Activity;
import android.content.*;
import android.net.*;
import android.os.*;
import android.graphics.*;
import android.media.*;
import android.media.projection.*;
import android.hardware.display.*;
import android.util.*;


public class ScreenshotActivity extends Activity {
    public static final int SCREEN_CAPTURE_REQUEST = 2017;
    public MediaProjection mediaProjection;
    public ImageReader captureSurface;
    private Handler mHandler;
    private VirtualDisplay mVirtualDisplay;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        startRecorder();
    }

    private void startRecorder() {
        Intent intent = ((MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent();
        startActivityForResult(intent, SCREEN_CAPTURE_REQUEST);
    }

    protected void onActivityResult(int request, int result, Intent data) {
        if (request == SCREEN_CAPTURE_REQUEST) {
            if (result == RESULT_OK) {
                this.mediaProjection = ((MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE)).getMediaProjection(result, data);
                startMediaProjection();
            } else {
                finish();
            }
        } else {
            super.onActivityResult(request, result, data);
        }
    }

    private void startMediaProjection() {
        takeTheScreenshot();
    }

    private void takeTheScreenshot() {
        new Thread(new Runnable() {
            public void run() {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                captureSurface = ImageReader.newInstance(displayMetrics.widthPixels, displayMetrics.heightPixels, PixelFormat.RGBA_8888,
                    1);

                mVirtualDisplay = mediaProjection.createVirtualDisplay("OCR Capture Screen", displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, captureSurface.getSurface(), null, null);

                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                }

                System.err.println("Taking: " + Thread.currentThread());
                Image image = captureSurface.acquireLatestImage();
                Image.Plane plane = image.getPlanes()[0];
                System.out.println(image.getPlanes()[0].getBuffer().capacity());
                ByteBuffer buf = plane.getBuffer();
                ByteBuffer secondBuf = ByteBuffer.allocate(image.getWidth() * image.getHeight() * 4);
                int stride = plane.getRowStride();
                int widthLim = image.getWidth() * 4;
                int yoff = 0;
                for (int y = 0; y < image.getHeight(); y++) {
                    buf.limit(yoff + widthLim);
                    buf.position(yoff);
                    secondBuf.put(buf);
                    yoff += stride;
                }
                buf = null;

                Bitmap bmp = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
                secondBuf.rewind();
                bmp.copyPixelsFromBuffer(secondBuf);
                secondBuf = null;
                image.close();
                File outputFile = null;
                try {
                    outputFile = File.createTempFile("screenshot", ".png", null);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }

                mVirtualDisplay.release();
                captureSurface.close();
                mVirtualDisplay = null;
                captureSurface = null;
				mediaProjection.stop();

                final File of = outputFile;
                mHandler.post(new Runnable() {
                    public void run() {
                        screenshotTaken(of);
                    }
                });
            }
        }).start();
    }

    private void screenshotTaken(File outputFile) {
        if (outputFile.exists()) {
            Intent intent = new Intent(this, MainActivity.class);
			intent.setDataAndType(Uri.fromFile(outputFile), "image/png");
            startActivity(intent);
        }
        finish();
    }
}