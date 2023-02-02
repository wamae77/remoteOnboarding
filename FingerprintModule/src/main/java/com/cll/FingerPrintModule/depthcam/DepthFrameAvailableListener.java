package com.cll.FingerPrintModule.depthcam;

import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import com.cll.FingerPrintModule.FingerCaptureActivity;

import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

public class DepthFrameAvailableListener implements ImageReader.OnImageAvailableListener
{
    private static final String TAG = DepthFrameAvailableListener.class.getSimpleName();

    public static int WIDTH = 320*2;
    public static int HEIGHT = 240*2;
    public static int SIZE = WIDTH * HEIGHT;

    public DepthFrameAvailableListener()
    {
    }

    @Override
    public void onImageAvailable(ImageReader reader)
    {/*
        boolean isDepthCapturing = FingerCaptureActivity.getDepthCamera().isCapturing();

        Logger.addToLog(TAG, "onImageAvailabe " +
                             " isDepthCapturing " + isDepthCapturing,
                             FingerCaptureActivity.getLogFile());

        if (!isDepthCapturing)
        {
            Logger.addToLog(TAG, "Cancel depth image processing ",
                    FingerCaptureActivity.getLogFile());
            return;
        }
*/
        try
        {
            Image image = reader.acquireNextImage();

            if ((image != null) &&
                    (image.getFormat() == ImageFormat.DEPTH16))
            {
                T5AirSnap sdk = FingerCaptureActivity.getCellSdk();
                int ret = sdk.updateDepthFrame(image);
            }

            image.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to acquireNextImage: " + e.getMessage());
        }
    }
}
