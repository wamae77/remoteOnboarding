package com.cll.FingerPrintModule.depthcam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.cll.FingerPrintModule.FingerCaptureActivity;
import com.cll.FingerPrintModule.utils.Logger;

import java.util.Arrays;
import java.util.List;

import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

public class DepthCamera extends CameraDevice.StateCallback
{
    private static final String TAG = DepthCamera.class.getSimpleName();

    private static int m_fpsMin = 5;
    private static int m_fpsMax = 30;

    private Context m_context                = null;
    private CameraManager m_cameraManager          = null;
    private ImageReader m_previewImageReader     = null;
    private CaptureRequest.Builder      m_previewBuilder         = null;
    private DepthFrameAvailableListener m_imageAvailableListener = null;
    private String                      m_cameraId               = null;
    private CameraCaptureSession m_cameraCaptureSession   = null;


    public DepthCamera(Context context)
    {
        m_context = context;

        m_cameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        m_cameraId      = getFrontDepthCameraID();

        if (m_cameraId == null)
        {
            return;
        }

        m_imageAvailableListener = new DepthFrameAvailableListener();
        m_previewImageReader     = ImageReader.newInstance(DepthFrameAvailableListener.WIDTH,
                DepthFrameAvailableListener.HEIGHT,
                ImageFormat.DEPTH16,1);

        m_previewImageReader.setOnImageAvailableListener(m_imageAvailableListener, null);
    }

    // Open the front depth camera and start sending frames
    public void startCamera()
    {
        if (m_cameraId == null)
        {
            return;
        }

        openCamera(m_cameraId);
    }

    private String getFrontDepthCameraID()
    {
        try
        {
            for (String camera : m_cameraManager.getCameraIdList())
            {
                CameraCharacteristics chars = m_cameraManager.getCameraCharacteristics(camera);

                boolean     facingFront  = chars.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_FRONT;
                final int[] capabilities = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);

                boolean depthCapable = false;
                for (int capability : capabilities)
                {
                    if (capability == CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT)
                    {
                        depthCapable = true;
                        break;
                    }
                }

                SizeF sensorSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);  //SENSOR_INFO_PHYSICAL_SIZE
                Size sensor     = chars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

                Logger.addToLog(TAG, "Sensor size: " + sensorSize +
                                " for camera " + camera +
                                ", facing Front " + facingFront +
                                ", pixel size " + sensor,
                        FingerCaptureActivity.getLogFile());
/*
                float[] lensPose = chars.get(CameraCharacteristics.LENS_POSE_TRANSLATION);
                if (lensPose != null && lensPose.length > 2){
                    Logger.addToLog(TAG, "lensPose (" + lensPose[0] + "," + lensPose[1] + "," + lensPose[2] + ")", FingerCaptureActivity.getLogFile());
                } else {
                    Logger.addToLog(TAG,"lensPose not exist", FingerCaptureActivity.getLogFile());
                }
*/
                // Since sensor size doesn't actually match capture size and because it is
                // reporting an extremely wide aspect ratio, this FoV is bogus
                float[] focalLengths = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);

                if (focalLengths.length > 0)
                {
                    float  focalLength = focalLengths[0];
                    double fov         = 2 * Math.atan(sensorSize.getWidth() / (2 * focalLength));

                    Logger.addToLog(TAG, "Calculated FoV: " + fov +
                                    " focalLength " + focalLength,
                            FingerCaptureActivity.getLogFile());
                }

                if (depthCapable && !facingFront)
                {
                    // Note that the sensor size is much larger than the available capture size
                    T5AirSnap sdk = FingerCaptureActivity.getCellSdk();
                    sdk.initDepthCameraParameters(sensorSize.getWidth(), sensorSize.getHeight(),
                            focalLengths[0], 0.0f, 0.0f); //   1000.0f * lensPose[0], 1000.0f * lensPose[1]);
                    return camera;
                }
            }
        }
        catch (CameraAccessException e)
        {
            Logger.addToLog(TAG, "Could not initialize Camera Cache",
                    FingerCaptureActivity.getLogFile());
            e.printStackTrace();
        }

        return null;
    }

    private void openCamera(String cameraId)
    {
        Logger.addToLog(TAG,"Opening Camera " + cameraId,
                FingerCaptureActivity.getLogFile());

        try
        {
            int permission = ContextCompat.checkSelfPermission(m_context, Manifest.permission.CAMERA);

            if (PackageManager.PERMISSION_GRANTED == permission)
            {
                m_cameraManager.openCamera(cameraId, this, null);
            }
            else
            {
                Logger.addToLog(TAG,"Permission not available to open camera",
                        FingerCaptureActivity.getLogFile());
            }
        }
        catch (CameraAccessException | IllegalStateException | SecurityException e)
        {
            Logger.addToLog(TAG,"Opening Camera has an Exception " + e,
                    FingerCaptureActivity.getLogFile());

            e.printStackTrace();
        }
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera)
    {
        try
        {
            m_previewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL); // TEMPLATE_PREVIEW
            m_previewBuilder.set(CaptureRequest.JPEG_ORIENTATION, 0);
            Range<Integer> fpsRange = new Range<>(m_fpsMin, m_fpsMax);
            m_previewBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange);
            m_previewBuilder.addTarget(m_previewImageReader.getSurface());

            List<Surface> targetSurfaces = Arrays.asList(m_previewImageReader.getSurface());
            camera.createCaptureSession(targetSurfaces,
                    new CameraCaptureSession.StateCallback()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session)
                        {
                            onCaptureSessionConfigured(session);
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session)
                        {
                            Log.e(TAG,"!!! Creating Capture Session failed due to internal error ");
                        }
                    }, null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void onCaptureSessionConfigured(@NonNull CameraCaptureSession session)
    {
        Log.i(TAG,"Capture Session created");
        m_cameraCaptureSession = session;
        m_previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try
        {
            //session.capture(previewBuilder.build(), null, null);
            session.setRepeatingRequest(m_previewBuilder.build(), null, null);
            //session.setSingleRepeatingRequest(previewBuilder.build(), null, null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera)
    {

    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error)
    {

    }

    public void stopCapture()
    {
        if (m_cameraCaptureSession != null)
        {
            try
            {
                m_cameraCaptureSession.stopRepeating();
                m_cameraCaptureSession = null;
            }
            catch (CameraAccessException e)
            {
                e.printStackTrace();
                m_cameraCaptureSession = null;
            }
        }
    }

    public boolean isCapturing()
    {
        return m_cameraCaptureSession != null;
    }
}

