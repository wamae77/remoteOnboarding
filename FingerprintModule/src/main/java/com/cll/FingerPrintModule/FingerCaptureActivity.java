package com.cll.FingerPrintModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.interop.Camera2Interop;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cll.FingerPrintModule.depthcam.DepthCamera;
import com.cll.FingerPrintModule.runnables.CreateTemplateThread;
import com.cll.FingerPrintModule.runnables.GetFingerprintQualityThread;
import com.cll.FingerPrintModule.runnables.GetNistQualityThread;
import com.cll.FingerPrintModule.utils.BitmapConvertor;
import com.cll.FingerPrintModule.utils.FingerPrint;
import com.cll.FingerPrintModule.utils.Logger;
import com.cll.FingerPrintModule.utils.MyExceptionHandler;
import com.cll.FingerPrintModule.utils.ResultScan;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ai.tech5.sdk.abis.T5AirSnap.CaptureStatus;
import ai.tech5.sdk.abis.T5AirSnap.NistPosCode;
import ai.tech5.sdk.abis.T5AirSnap.RawImage;
import ai.tech5.sdk.abis.T5AirSnap.SgmRectImage;
import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;
import static ai.tech5.sdk.abis.T5AirSnap.StandardErrorCodes.SE_OK;
import static ai.tech5.sdk.abis.T5AirSnap.StandardErrorCodes.SE_TIME;

public class FingerCaptureActivity extends AppCompatActivity {

    private static final String TAG = FingerCaptureActivity.class.getSimpleName();

    private static T5AirSnap m_cellSdk = null;
    private BitmapConvertor m_bitmapConvertor = null;
    private static int m_poolSize = 4;
    private static ExecutorService m_service = null;

    private ImageView m_transparentImageView;
    private static SgmRectImage[] m_fingerImages = null;

    /**
     * Blocking camera operations are performed using this executor
     */
    private ExecutorService m_cameraExecutorService;
    private PreviewView m_viewFinder;

    private int m_lensFacing = CameraSelector.LENS_FACING_BACK;

    private Preview m_preview = null;
    private ImageAnalysis m_imageAnalyzer = null;
    private Camera m_camera = null;
    private CameraSelector m_cameraSelector = null;
    static DepthCamera m_depthCamera = null;

    private ProcessCameraProvider m_cameraProvider = null;

    static File m_logFile = null;
    static String m_rootDirectoryPath = "";
    static File m_rootDirectory = null;
    static String m_userName = "";
    static boolean m_livenessCheck = false;
    static boolean m_showEllipses = false;
    static boolean m_createTemplates = false;
    static boolean m_orientationCheck = false;
    static boolean m_saveSdkLog = false;
    static float m_detectorThreshold = 0.9f;

    private GraphicOverlay m_graphicOverlay;
    private TextView m_statusTextView;
    private ProgressDialog m_progressDialog;

    private byte[] m_nistQuality = new byte[4];
    private byte[] m_quality = new byte[4];
    private int[] m_minutiaesNumber = new int[4];

    public static int m_positionCode = NistPosCode.POS_CODE_U_FINGER;


    public SgmRectImage getFingerRect(int index) {
        if (m_fingerImages == null) return null;
        if ((index >= 0) && (index < m_fingerImages.length)) return m_fingerImages[index];
        else return null;
    }

    public static T5AirSnap getCellSdk() {
        return m_cellSdk;
    }

    public static DepthCamera getDepthCamera() {
        return m_depthCamera;
    }

    public void setUsername(String username) {
        m_userName = username;
    }

    public int getPositionCodeIndex(int positionCodeIndex) {
        // ph2
/*
        switch (positionCodeIndex)
        {
            case 1:
            {
               return NistPosCode.POS_CODE_R_INDEX_MIDDLE;
            }
            case 0:
            default:
            {
                 return NistPosCode.POS_CODE_L_INDEX_MIDDLE;
            }
        }

*/
        switch (positionCodeIndex) {
            case 1: {
                return NistPosCode.POS_CODE_PL_R_4F;
            }
            case 2: {
                return NistPosCode.POS_CODE_L_THUMB;
            }
            case 3: {
                return NistPosCode.POS_CODE_R_THUMB;
            }
            case 4: {
                return NistPosCode.POS_CODE_L_AND_R_THUMBS;
            }
            case 5: {
                return NistPosCode.POS_CODE_L_INDEX_F;
            }
            case 6: {
                return NistPosCode.POS_CODE_R_INDEX_F;
            }
            case 7: {
                return NistPosCode.POS_CODE_L_INDEX_MIDDLE;
            }
            case 8: {
                return NistPosCode.POS_CODE_R_INDEX_MIDDLE;
            }
            case 0:
            default: {
                return NistPosCode.POS_CODE_PL_L_4F;
            }
        }
    }

    public void setPositionCodeIndex(int positionCodeIndex) {
        m_positionCode = getPositionCodeIndex(positionCodeIndex);
    }

    public void setLivenessCheck(boolean livenessCheck) {
        m_livenessCheck = livenessCheck;
    }

    public void setShowEllipses(boolean showEllipses) {
        m_showEllipses = showEllipses;
    }

    public void setCreateTemplates(boolean createTemplates) {
        m_createTemplates = createTemplates;
    }

    public void setOrientationCheck(boolean orientationCheck) {
        m_orientationCheck = orientationCheck;
    }

    public void setSaveSdkLogFlag(boolean saveSdkLog) {
        m_saveSdkLog = saveSdkLog;
    }

    public void setDetectorThreshold(float detectorThreshold) {
        m_detectorThreshold = detectorThreshold;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));

        setContentView(R.layout.activity_finger_capture);
        setScreenBrightnessFull();

        m_service = Executors.newFixedThreadPool(m_poolSize);

        m_transparentImageView = findViewById(R.id.iv_transparent_view);
        m_viewFinder = findViewById(R.id.view_finder);
        m_graphicOverlay = findViewById(R.id.graphic_overlay);
        m_statusTextView = findViewById(R.id.txt_status);

        //m_logFile = new File(getExternalCacheDir(), "log.txt");

        m_logFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "log.txt");

        ////////

        boolean show_info = true;

        if (show_info) {
            String[] abis = Build.SUPPORTED_ABIS;

            Logger.addToLog(TAG, "Phone : " + Build.MANUFACTURER + " " + Build.MODEL, m_logFile);

            for (int i = 0; i < abis.length; i++) {
                Logger.addToLog(TAG, "ABI : " + abis[i], m_logFile);
            }

            Logger.addToLog(TAG, "Release " + Build.VERSION.RELEASE, m_logFile);
        }
        ////////

        m_cellSdk = new T5AirSnap(this);

        Logger.addToLog(TAG, "SDK version: " + m_cellSdk.getVersion(), m_logFile);
        Logger.addToLog(TAG, "Device ID: " + m_cellSdk.getDeviceIdentifier(), m_logFile);
        Logger.addToLog(TAG, "Liveness check: " + m_livenessCheck, m_logFile);
        Logger.addToLog(TAG, "Show ellipses: " + m_showEllipses, m_logFile);
        Logger.addToLog(TAG, "Create templates: " + m_createTemplates, m_logFile);
        Logger.addToLog(TAG, "Orientation check: " + m_orientationCheck, m_logFile);
        Logger.addToLog(TAG, "Position code: " + m_positionCode + " (" + getCaptureObjectName() + ")", m_logFile);

        String developersToken = "";//02102b02f02105803602702d02503302c06a02b02e02603100401805205905605f05c04204505e";

        int resultCode = SE_OK;
        String errorMessage = "OK";

        m_cellSdk.setCacheDir(getExternalCacheDir().toString());

        m_cellSdk.setCaptureId(m_userName + "_" + Build.MANUFACTURER + "_" + Build.MODEL + "_" + Build.VERSION.RELEASE);

        m_cellSdk.setSaveSdkLogFlag(m_saveSdkLog);

        resultCode = m_cellSdk.initSdk(developersToken);
        errorMessage = m_cellSdk.getErrorMessage();

        if (resultCode != SE_OK) {
            Logger.addToLog(TAG, "init SDK failed (code: " + resultCode + ") text = " + errorMessage, m_logFile);
            m_cellSdk = null;
            if (resultCode == SE_TIME) setResult(SE_TIME);
            else setResult(AppCompatActivity.RESULT_CANCELED);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        m_cellSdk.setPositionCode(m_positionCode);
        m_cellSdk.setLivenessCheck(m_livenessCheck);
        m_cellSdk.setOrientationCheck(m_orientationCheck);
        m_cellSdk.setDetectorThreshold(m_detectorThreshold);
        //m_cellSdk.setSaveFramesFlag(true);

        m_fingerImages = new SgmRectImage[10];
        for (int index = 0; index < m_fingerImages.length; index++)
            m_fingerImages[index] = new SgmRectImage();

        m_bitmapConvertor = new BitmapConvertor();

        // ph2
        //m_rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() +
        //                  File.separator + "T5FingerCapture" + File.separator+m_userName;
        //m_rootDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Documents" + File.separator + "T5-AirSnap";

        m_rootDirectoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "Documents" + File.separator + "T5-AirSnap";

        m_rootDirectory = new File(m_rootDirectoryPath);

        if (!m_rootDirectory.exists()) {
            m_rootDirectory.mkdirs();
        }

        // Initialize our background executor
        //  cameraExecutor = Executors.newSingleThreadExecutor();
        m_cameraExecutorService = Executors.newFixedThreadPool(1);

        m_viewFinder.post(this::setUpCamera);

        m_progressDialog = new ProgressDialog(FingerCaptureActivity.this);
        m_progressDialog.setMessage("Processing...");
    }

    /**
     * Initialize CameraX, and prepare to bind the camera use cases
     */
    private void setUpCamera() {
        Logger.addToLog(TAG, "setting up camera", m_logFile);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(FingerCaptureActivity.this);

        cameraProviderFuture.addListener(() -> {

            // CameraProvider
            try {
                m_cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException e) {
                Logger.addToLog(TAG, "get camera provider failed " + e.getLocalizedMessage(), m_logFile);
                Logger.logException(TAG, e, m_logFile);
            } catch (InterruptedException e) {
                Logger.addToLog(TAG, "get camera provider failed " + e.getLocalizedMessage(), m_logFile);
                Logger.logException(TAG, e, m_logFile);
            }

            // Build and bind the camera use cases
            bindCameraUseCases();

            @SuppressLint("RestrictedApi") Size size = m_imageAnalyzer.getAttachedSurfaceResolution();
            @SuppressLint("RestrictedApi") Size previewSize = m_preview.getAttachedSurfaceResolution();

            Logger.addToLog(TAG, "preview size " + previewSize, m_logFile);
            Logger.addToLog(TAG, "analyze size " + size, m_logFile);

            initBorder(size.getHeight(), size.getWidth());

        }, ContextCompat.getMainExecutor(FingerCaptureActivity.this));

        m_depthCamera = new DepthCamera(this);
        m_depthCamera.startCamera();
    }

    /**
     * Declare and bind preview, capture and analysis use cases
     */
    @SuppressLint({"RestrictedApi", "UnsafeExperimentalUsageError"})
    private void bindCameraUseCases() {
        int rotation = m_viewFinder.getDisplay().getRotation();

        m_cameraSelector = new CameraSelector.Builder().requireLensFacing(m_lensFacing).build();
        m_preview = new Preview.Builder().setTargetResolution(new Size(1080, 1920)).setTargetRotation(rotation).build();

        ImageAnalysis.Builder imageAnalysisBuilder = new ImageAnalysis.Builder().setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST).setTargetResolution(new Size(1080, 1920)).setTargetRotation(rotation);

        Camera2Interop.Extender extender = new Camera2Interop.Extender(imageAnalysisBuilder);
        extender.setCaptureRequestOption(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_OFF);

        m_imageAnalyzer = imageAnalysisBuilder.build();
        m_imageAnalyzer.setAnalyzer(m_cameraExecutorService, new FingerAnalyzer());

        // Must unbind the use-cases before rebinding them
        m_cameraProvider.unbindAll();

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            m_camera = m_cameraProvider.bindToLifecycle(this, m_cameraSelector, m_preview, m_imageAnalyzer);

            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : manager.getCameraIdList()) {
                //CameraCharacteristics characteristics
                CameraCharacteristics mCameraInfo = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = mCameraInfo.get(CameraCharacteristics.LENS_FACING);
                if ((facing != null) && (facing == CameraCharacteristics.LENS_FACING_FRONT)) {
                    continue;
                }

                Range<Integer> exposureCompensationRange = mCameraInfo.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
                int exposureCompensation = exposureCompensationRange.getLower() / 2;

                CameraControl cameraControl = m_camera.getCameraControl();
                cameraControl.setExposureCompensationIndex(exposureCompensation);

                float zoomRatio = 1.0f;

                CameraInfo cameraInfo = m_camera.getCameraInfo();
                ZoomState zoomState = cameraInfo.getZoomState().getValue();

                if (zoomState != null) {
                    zoomRatio = zoomState.getMaxZoomRatio();

                    Logger.addToLog(TAG, "maxZoomRatio " + zoomRatio, m_logFile);

                    /*
                    float targetZoomRatio = ((m_positionCode == NistPosCode.POS_CODE_PL_R_4F) ||
                                             (m_positionCode == NistPosCode.POS_CODE_PL_L_4F) ||
                                             (m_positionCode == NistPosCode.POS_CODE_L_AND_R_THUMBS)) ?
                                            1.5f : 2.0f;
                    */

                    // ph2
                    float targetZoomRatio = 2.0f;

                    if (zoomRatio > targetZoomRatio) zoomRatio = targetZoomRatio;
                    else if (zoomRatio < 1.0f) zoomRatio = 1.0f;
                }

                Logger.addToLog(TAG, "setZoomRatio " + zoomRatio, m_logFile);

                cameraControl.setZoomRatio(zoomRatio);

                SizeF sensorSize = mCameraInfo.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);  //SENSOR_INFO_PHYSICAL_SIZE
                Size sensor = mCameraInfo.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

                Logger.addToLog(TAG, "Sensor size: " + sensorSize + " for camera " + cameraId + ", Pixel size " + sensor, m_logFile);

                // Since sensor size doesn't actually match capture size and because it is
                // reporting an extremely wide aspect ratio, this FoV is bogus
                float[] focalLengths = mCameraInfo.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                if (focalLengths.length > 0) {
                    float focalLength = focalLengths[0];
                    double fov = 2 * Math.atan(sensorSize.getWidth() / (2 * focalLength));
                    Logger.addToLog(TAG, "Calculated FoV: " + fov + " focalLength " + focalLength, m_logFile);
                }

                m_cellSdk.initCameraParameters(sensorSize.getWidth(), sensorSize.getHeight(), focalLengths[0], zoomRatio);
                break;
            }

            // Attach the viewfinder's surface provider to preview use case
            m_preview.setSurfaceProvider(m_viewFinder.getSurfaceProvider());

            autoFocus();
            toggleFlash(true);
        } catch (Exception exc) {
            Logger.addToLog(TAG, "Use case binding failed" + exc.getLocalizedMessage(), m_logFile);
            Logger.logException(TAG, exc, m_logFile);
        }
    }

    class FingerAnalyzer implements ImageAnalysis.Analyzer {
        @SuppressLint("UnsafeExperimentalUsageError")
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            analyzeImage(imageProxy);
        }
    }

    private static void writeToFile(byte[] data, String path) {
        FileOutputStream fOut = null;

        try {
            File myFile = new File(path);

            if (myFile.exists()) {
                myFile.delete();
            }

            if (!myFile.createNewFile()) {
                return;
            }

            fOut = new FileOutputStream(myFile, false);
            fOut.write(data);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String writeToFile(Bitmap bm, String path) {
        FileOutputStream fOut = null;

        try {
            File myFile = new File(path);

            if (myFile.exists()) {
                myFile.delete();
            }

            //myFile.createNewFile();

            fOut = new FileOutputStream(myFile, false);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getCaptureObjectName() {
        if ((m_positionCode == NistPosCode.POS_CODE_R_INDEX_MIDDLE) || (m_positionCode == NistPosCode.POS_CODE_L_INDEX_MIDDLE)) {
            return ((m_positionCode == NistPosCode.POS_CODE_R_INDEX_MIDDLE) ? "Right" : "Left") + " index and middle fingers";
        }

        if ((m_positionCode == NistPosCode.POS_CODE_PL_R_4F) || (m_positionCode == NistPosCode.POS_CODE_PL_L_4F)) {
            return ((m_positionCode == NistPosCode.POS_CODE_PL_R_4F) ? "Right" : "Left") + " 4 fingers";
        }

        if (m_positionCode == NistPosCode.POS_CODE_L_AND_R_THUMBS) {
            return "Thumbs";
        }

        if ((m_positionCode == NistPosCode.POS_CODE_R_THUMB) || (m_positionCode == NistPosCode.POS_CODE_L_THUMB)) {
            return ((m_positionCode == NistPosCode.POS_CODE_R_THUMB) ? "Right" : "Left") + " thumb";
        }

        return (m_positionCode == NistPosCode.POS_CODE_R_INDEX_F) ? "Right index finger" : (m_positionCode == NistPosCode.POS_CODE_R_MIDDLE_F) ? "Right middle finger" : (m_positionCode == NistPosCode.POS_CODE_R_RING_F) ? "Right ring finger" : (m_positionCode == NistPosCode.POS_CODE_R_LITTLE_F) ? "Right little finger" : (m_positionCode == NistPosCode.POS_CODE_L_INDEX_F) ? "Left index finger" : (m_positionCode == NistPosCode.POS_CODE_L_MIDDLE_F) ? "Left middle finger" : (m_positionCode == NistPosCode.POS_CODE_L_RING_F) ? "Left ring finger" : (m_positionCode == NistPosCode.POS_CODE_L_LITTLE_F) ? "Left little finger" : "finger";
    }

    private void updateStatus(int captureStatus, ArrayList<SgmRectImage> rects) {
        Logger.addToLog(TAG, getCaptureObjectName() + ": captureStatus: " + captureStatus, m_logFile);

        if (captureStatus == CaptureStatus.frameSkipped) {
            return;
        }

        if (captureStatus == CaptureStatus.tooFewFingers) {
            setStatus("Frame " + getCaptureObjectName());
        } else if (captureStatus == CaptureStatus.tooManyFingers) {
            int expectedFingerCount = ((m_positionCode == NistPosCode.POS_CODE_PL_R_4F) || (m_positionCode == NistPosCode.POS_CODE_PL_L_4F)) ? 4 : ((m_positionCode == NistPosCode.POS_CODE_L_AND_R_THUMBS) || (m_positionCode == NistPosCode.POS_CODE_R_INDEX_MIDDLE) || (m_positionCode == NistPosCode.POS_CODE_L_INDEX_MIDDLE)) ? 2 : 1;

            setStatus("More than " + expectedFingerCount + ((expectedFingerCount == 1) ? " finger" : " fingers"));
        } else if (captureStatus == CaptureStatus.wrongAngle) {
            String wrongAngleStatus = ((m_positionCode == NistPosCode.POS_CODE_L_AND_R_THUMBS) || (m_positionCode == NistPosCode.POS_CODE_R_THUMB) || (m_positionCode == NistPosCode.POS_CODE_L_THUMB)) ? ("Hold " + getCaptureObjectName() + " vertically") : ("Hold " + getCaptureObjectName() + " horizontally");

            setStatus(wrongAngleStatus);
        } else if (captureStatus == CaptureStatus.tooFar) {
            setStatus("Please bring your hand closer");
        } else if (captureStatus == CaptureStatus.tooClose) {
            setStatus("Please move your hand further");
        } else if (captureStatus == CaptureStatus.lowFocus) {
            setStatus("Low focus. Try to move hand");
        } else if (captureStatus == CaptureStatus.goodFocus) {
            setStatus("Hold your hand steady");
        } else if (captureStatus == CaptureStatus.bestFrameChosen) {
            setStatus("");
        }

        int color = ((captureStatus == CaptureStatus.tooFewFingers) || (captureStatus == CaptureStatus.tooManyFingers) || (captureStatus == CaptureStatus.wrongHand)) ? 0xFFFF2020 : ((captureStatus == CaptureStatus.wrongAngle) || (captureStatus == CaptureStatus.tooFar) || (captureStatus == CaptureStatus.tooClose) || (captureStatus == CaptureStatus.lowFocus)) ? 0xFFDDDD00 : 0xFF00FF00;

        runOnUiThread(() -> m_graphicOverlay.drawBorderAndBoundBoxes(color, rects));
    }

    private static Bitmap createFlippedBitmap(Bitmap source, boolean xFlip, boolean yFlip) {
        Matrix matrix = new Matrix();
        matrix.postScale(xFlip ? -1 : 1, yFlip ? -1 : 1, source.getWidth() / 2f, source.getHeight() / 2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap createRotatedBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void saveFingerprints(long currentTime, byte[] previewImageBuffer, int previewWidth, int previewHeight, ArrayList<SgmRectImage> rects, ArrayList<FingerPrint> fingerprintFilePaths) {
        if (rects.isEmpty()) {
            return;
        }
/*
        byte[] frameBitmapBuffer = m_bitmapConvertor.CreateBitmapFile(previewImageBuffer,
                                                                      previewWidth, previewHeight);

        String frameFilePath     = m_rootDirectory + File.separator + currentTime + "_frame.bmp";

        writeToFile(frameBitmapBuffer, frameFilePath);
*/
        File[] rootDirectoryFiles = m_rootDirectory.listFiles();

        for (int i = 0; i < rects.size(); i++) {
            SgmRectImage rect = rects.get(i);
            int position = rect.pos - 1;
            m_fingerImages[position].pos = rect.pos;
            m_fingerImages[position].width = rect.width;
            m_fingerImages[position].height = rect.height;
            m_fingerImages[position].image = new byte[rect.image.length];
            System.arraycopy(rect.image, 0, m_fingerImages[position].image, 0, rect.image.length);
/*
            byte[] image512  = m_cellSdk.cropTo512x512(rect.image, rect.width, rect.height);
            int    width512  = 512;
            int    height512 = 512;

            String fingerWsqFilePath = m_rootDirectory + File.separator + currentTime +
                                       "_finger_" + rect.pos + ".wsq";

            byte[] fingerWsqImage = m_cellSdk.convertRawToWsq(image512, width512, height512);

            writeToFile(fingerWsqImage, fingerWsqFilePath);

            String fingerBmpFilePath = m_rootDirectory + File.separator + currentTime +
                                       "_finger_" + rect.pos + ".bmp";

            byte[] fingerBmpImage = m_cellSdk.convertRawToBmp(rect.image, rect.width, rect.height);

            writeToFile(fingerBmpImage, fingerBmpFilePath);
*/
            // ph2


            for (int setNumber = 1; ; setNumber++) {
                String fingerPngFileName = "T5-AirSnap_Subject-" + m_userName + "_Set-" + setNumber + "_Finger-" + rect.pos + ".png";

                boolean fileExists = false;

                for (File file : rootDirectoryFiles) {
                    if (file.isFile() && file.getName().equals(fingerPngFileName)) {
                        fileExists = true;
                        break;
                    }
                }

                if (!fileExists) {
                    byte[] fingerPngImage = m_cellSdk.convertRawToPng(rect.image, rect.width, rect.height);
                    String fingerPngFilePath = m_rootDirectoryPath + File.separator + fingerPngFileName;

                    writeToFile(fingerPngImage, fingerPngFilePath);
                    fingerprintFilePaths.add(new FingerPrint(fingerPngFilePath, m_positionCode, rect.pos, m_quality[i]));

                    break;
                }
            }
        }
    }

    private String saveResultBitmap(long currentTime, byte[] previewImageBuffer, int previewWidth, int previewHeight, ArrayList<SgmRectImage> rects, byte livenessScore, boolean reverseFlag) {
        if (rects.isEmpty()) {
            return "";
        }

        int maxRectWidth = 0;
        int maxRectHeight = 0;

        for (int i = 0; i < rects.size(); i++) {
            SgmRectImage rect = rects.get(i);

            if (rect.width > maxRectWidth) maxRectWidth = rect.width;
            if (rect.height > maxRectHeight) maxRectHeight = rect.height;
        }

        int borderSize = 50;
        int rectsWidth = rects.size() * maxRectWidth;

        int squareSize = (previewWidth + previewHeight) / 2;
        int resultPreviewWidth = (squareSize < previewWidth) ? squareSize : previewWidth;
        int resultPreviewHeight = (squareSize < previewHeight) ? squareSize : previewHeight;

        int resultWidth = (rectsWidth > resultPreviewWidth) ? rectsWidth : resultPreviewWidth;
        int resultHeight = resultPreviewHeight + maxRectHeight + 6 * borderSize;

        Bitmap previewBitmap = m_bitmapConvertor.CreateGrayBitmapArray(previewImageBuffer, previewWidth, previewHeight);

        Bitmap resultBitmap = Bitmap.createBitmap(resultWidth, resultHeight, previewBitmap.getConfig());
        resultBitmap.eraseColor(Color.WHITE);

        Canvas resultCanvas = new Canvas(resultBitmap);
        Paint paint = new Paint();

        paint.setColor(Color.BLUE);
        paint.setTextSize(resultWidth / 32);
        paint.setStyle(Paint.Style.FILL);

        int yOffset = (m_livenessCheck) ? (2 * borderSize) : 0;

        for (int i = 0; i < rects.size(); i++) {
            SgmRectImage rect = rects.get(i);

            Logger.addToLog(TAG, "---", m_logFile);
            Logger.addToLog(TAG, "Finger position: " + rect.pos, m_logFile);
            Logger.addToLog(TAG, "NFIQ: " + m_nistQuality[i], m_logFile);

            int fingerIndex = 0;

            if (rects.size() == 2) {
                fingerIndex = ((rect.pos == NistPosCode.POS_CODE_R_THUMB) || (rect.pos == NistPosCode.POS_CODE_R_MIDDLE_F) || (rect.pos == NistPosCode.POS_CODE_L_INDEX_F)) ? 0 : ((rect.pos == NistPosCode.POS_CODE_L_THUMB) || (rect.pos == NistPosCode.POS_CODE_R_INDEX_F) || (rect.pos == NistPosCode.POS_CODE_L_MIDDLE_F)) ? 1 : 0;
            } else if (rects.size() == 4) {
                fingerIndex = ((rect.pos == NistPosCode.POS_CODE_R_THUMB) || (rect.pos == NistPosCode.POS_CODE_R_LITTLE_F) || (rect.pos == NistPosCode.POS_CODE_L_INDEX_F)) ? 0 : ((rect.pos == NistPosCode.POS_CODE_L_THUMB) || (rect.pos == NistPosCode.POS_CODE_R_RING_F) || (rect.pos == NistPosCode.POS_CODE_L_MIDDLE_F)) ? 1 : ((rect.pos == NistPosCode.POS_CODE_R_MIDDLE_F) || (rect.pos == NistPosCode.POS_CODE_L_RING_F)) ? 2 : ((rect.pos == NistPosCode.POS_CODE_R_INDEX_F) || (rect.pos == NistPosCode.POS_CODE_L_LITTLE_F)) ? 3 : 0;
            }

            Rect fingerRect = new Rect(0, 0, rect.width, rect.height);

            int x = fingerIndex * (resultWidth / rects.size()) + ((resultWidth / rects.size()) - rect.width) / 2;

            Rect resultRect = new Rect(x, resultPreviewHeight + borderSize + maxRectHeight - rect.height, x + rect.width, resultPreviewHeight + borderSize + maxRectHeight);

            Bitmap fingerBitmap = m_bitmapConvertor.CreateGrayBitmapArray(rect.image, rect.width, rect.height);
            Bitmap flippedFingerBitmap = createFlippedBitmap(fingerBitmap, true, false);
            resultCanvas.drawBitmap(flippedFingerBitmap, fingerRect, resultRect, null);
/*
            String nistQualityText     = "NFIQ: " + m_nistQuality[i];
            Rect   nistQualityTextRect = new Rect();

            paint.getTextBounds(nistQualityText, 0, nistQualityText.length(), nistQualityTextRect);

            int width = nistQualityTextRect.width();

            if (m_createTemplates)
            {
                Logger.addToLog(TAG, "Quality: "          + m_quality[i],         m_logFile);
                Logger.addToLog(TAG, "Minutiaes number: " + m_minutiaesNumber[i], m_logFile);

                String qualityText     = "Quality: " + m_quality[i];
                Rect   qualityTextRect = new Rect();

                paint.getTextBounds(qualityText, 0, qualityText.length(), qualityTextRect);

                String minutiaesText     = "Minutiaes: " + m_minutiaesNumber[i];
                Rect   minutiaesTextRect = new Rect();

                paint.getTextBounds(minutiaesText, 0, minutiaesText.length(), minutiaesTextRect);

                if (width < qualityTextRect.width())    width = qualityTextRect.width();
                if (width < minutiaesTextRect.width())  width = minutiaesTextRect.width();

                resultCanvas.drawText(qualityText,
                                      resultRect.left + (resultRect.width() - width) / 2,
                                      yOffset + resultRect.bottom + 2 * borderSize,
                                      paint);

                resultCanvas.drawText(minutiaesText,
                                      resultRect.left + (resultRect.width() - width) / 2,
                                      yOffset + resultRect.bottom + 3 * borderSize,
                                      paint);
            }

            resultCanvas.drawText(nistQualityText,
                                  resultRect.left + (resultRect.width() - width) / 2,
                                  yOffset + resultRect.bottom + borderSize,
                                  paint);
*/
            String qualityText = "Quality: " + m_quality[i];
            Rect qualityTextRect = new Rect();

            paint.getTextBounds(qualityText, 0, qualityText.length(), qualityTextRect);

            int width = qualityTextRect.width();

            resultCanvas.drawText(qualityText, resultRect.left + (resultRect.width() - width) / 2, yOffset + resultRect.bottom + borderSize, paint);
        }

        if (m_livenessCheck) {
            String livenessScoreText = "Liveness score: " + livenessScore + "     Reverse: " + reverseFlag;
//            String livenessScoreText = "Liveness score: " + livenessScore;

            Rect livenessScoreTextRect = new Rect();

            paint.getTextBounds(livenessScoreText, 0, livenessScoreText.length(), livenessScoreTextRect);

            resultCanvas.drawText(livenessScoreText, (resultWidth - livenessScoreTextRect.width()) / 2, resultPreviewHeight + 2 * borderSize + maxRectHeight, paint);
        }

        if ((m_positionCode == NistPosCode.POS_CODE_R_INDEX_F) || (m_positionCode == NistPosCode.POS_CODE_R_MIDDLE_F) || (m_positionCode == NistPosCode.POS_CODE_R_RING_F) || (m_positionCode == NistPosCode.POS_CODE_R_LITTLE_F) || (m_positionCode == NistPosCode.POS_CODE_R_INDEX_MIDDLE) || (m_positionCode == NistPosCode.POS_CODE_PL_R_4F)) {
            previewBitmap = createFlippedBitmap(previewBitmap, true, true);
        } else if ((m_positionCode == NistPosCode.POS_CODE_R_THUMB) || (m_positionCode == NistPosCode.POS_CODE_L_THUMB) || (m_positionCode == NistPosCode.POS_CODE_L_AND_R_THUMBS)) {
            previewBitmap = createRotatedBitmap(previewBitmap, 90.0f);
        }

        Rect sourceRect = new Rect((previewBitmap.getWidth() - resultPreviewWidth) / 2, (previewBitmap.getHeight() - resultPreviewHeight) / 2, (previewBitmap.getWidth() - resultPreviewWidth) / 2 + resultPreviewWidth, (previewBitmap.getHeight() - resultPreviewHeight) / 2 + resultPreviewHeight);

        Rect destinationRect = new Rect((resultWidth - resultPreviewWidth) / 2, 0, (resultWidth - resultPreviewWidth) / 2 + resultPreviewWidth, resultPreviewHeight);

        resultCanvas.drawBitmap(previewBitmap, sourceRect, destinationRect, null);

        String resultFilePath = writeToFile(resultBitmap, new File(getExternalCacheDir(), currentTime + "_result.png").getAbsolutePath());

        return resultFilePath;
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void analyzeImage(ImageProxy imageProxy) {
        try {
            Logger.addToLog(TAG, "-------------------------------------------------------", m_logFile);
            Logger.addToLog(TAG, "Analyzing preview image " + " w:" + imageProxy.getWidth() + " h:" + imageProxy.getHeight() + " r:" + imageProxy.getImageInfo().getRotationDegrees() + " f:" + imageProxy.getFormat() + " p0:" + imageProxy.getPlanes()[0].getPixelStride() + " r0:" + imageProxy.getPlanes()[0].getRowStride() + " p1:" + imageProxy.getPlanes()[1].getPixelStride() + " r1:" + imageProxy.getPlanes()[1].getRowStride() + " p2:" + imageProxy.getPlanes()[2].getPixelStride() + " r2:" + imageProxy.getPlanes()[2].getRowStride(), m_logFile);

            long currentTime = System.currentTimeMillis();

            Image previewImage = imageProxy.getImage();
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();

            ArrayList<SgmRectImage> rects = new ArrayList<SgmRectImage>();

            int captureStatus = m_cellSdk.analyzeImage(previewImage, rotationDegrees, rects);

            if (!m_showEllipses) {
                rects.clear();
            }

            if (captureStatus == CaptureStatus.bestFrameChosen) {
                runOnUiThread(() -> {
                    m_cameraProvider.unbindAll();
                    m_graphicOverlay.drawBorderAndBoundBoxes(Color.GREEN, rects);
                    m_progressDialog.show();
                });

                m_depthCamera.stopCapture();

                setStatus("");

                int result = SE_OK;

                Boolean reverseFlag = new Boolean(false);
/*
                result = m_cellSdk.checkReverse(reverseFlag);

                Logger.addToLog(TAG, "Reverse check result: " + result +
                                     ", reverse flag: " + reverseFlag, m_logFile);

                if ((result == SE_OK) && reverseFlag)
                {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Reversed", Toast.LENGTH_LONG).show();
                    });
                }

                if ((m_positionCode == NistPosCode.POS_CODE_R_THUMB) ||
                    (m_positionCode == NistPosCode.POS_CODE_L_THUMB))
                {
                    Boolean wrongThumb = new Boolean(false);

                    result = m_cellSdk.checkThumb(wrongThumb);

                    Logger.addToLog(TAG, "Thumb check result: " + result +
                                    ", wrong thumb flag: " + wrongThumb, m_logFile);

                    if ((result == SE_OK) && wrongThumb)
                    {
                        runOnUiThread(() -> {
                            Toast.makeText(this,
                                           (m_positionCode == NistPosCode.POS_CODE_R_THUMB) ?
                                           "Wrong thumb (left instead of right)" :
                                           "Wrong thumb (right instead of left)",
                                           Toast.LENGTH_LONG).show();
                        });
                    }
                }
*/
                int previewWidth = previewImage.getWidth();
                int previewHeight = previewImage.getHeight();

                byte[] previewImageBuffer = new byte[previewWidth * previewHeight];

                ArrayList<SgmRectImage> segmentedRects = new ArrayList<>();

                Byte livenessScore = new Byte((byte) 0);

                result = m_cellSdk.getSegmentedFingers(previewImageBuffer, 0, 0, segmentedRects, livenessScore);

                Logger.addToLog(TAG, "Segmentation result: " + result + ", liveness score: " + livenessScore, m_logFile);

                if (m_livenessCheck) {
                    byte livenessScoreThreshold = 50;

                    if (livenessScore < livenessScoreThreshold) {
                        Logger.addToLog(TAG, "Liveness requirement is not met", m_logFile);
                    }
                }

                if (m_createTemplates) {
                    createTemplates(currentTime, segmentedRects);
                } else {
                    // ph2
                    //getNistQualityValues(segmentedRects);
                    getFingerprintQualityValues(segmentedRects);
                }

                ArrayList<FingerPrint> fingerprintFilePaths = new ArrayList<FingerPrint>();

                saveFingerprints(currentTime, previewImageBuffer, previewWidth, previewHeight, segmentedRects, fingerprintFilePaths);

                // String resultFilePath = saveResultBitmap(currentTime, previewImageBuffer, previewWidth, previewHeight, segmentedRects, livenessScore, reverseFlag);

                ResultScan.getInstance().setList(fingerprintFilePaths);
                hideProgress();

                runOnUiThread(() -> {
                    Intent intent = new Intent();

                    //  intent.putExtra("resultFilePath", resultFilePath);
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    finish();
                });
            }

            updateStatus(captureStatus, rects);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logException(TAG, e, m_logFile);
        } finally {
            imageProxy.close();
        }
    }

    private void createTemplates(long currentTime, ArrayList<SgmRectImage> rects) {
        int threadCount = rects.size();
        short defaultPpi = 500;

        CreateTemplateThread[] createTemplateThreads = null;

        try {
            createTemplateThreads = new CreateTemplateThread[threadCount];
            ArrayList<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                SgmRectImage rect = rects.get(threadIndex);

                RawImage fingerRawImage = new RawImage();

                fingerRawImage.m_finger = rect.pos;
                fingerRawImage.m_image = rect.image;
                fingerRawImage.m_width = rect.width;
                fingerRawImage.m_height = rect.height;
                fingerRawImage.m_ppi = defaultPpi;

                createTemplateThreads[threadIndex] = new CreateTemplateThread(m_cellSdk, fingerRawImage);

                Future future = m_service.submit(createTemplateThreads[threadIndex]);
                futures.add(future);
            }

            for (Future<Runnable> future : futures) {
                future.get();
            }

            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                CreateTemplateThread thread = createTemplateThreads[threadIndex];

                m_nistQuality[threadIndex] = thread.getNistQuality();
                m_quality[threadIndex] = thread.getQuality();
                m_minutiaesNumber[threadIndex] = thread.getMinutiaesNumber();

                int finger = thread.getFinger();
                byte[] fingerTemplate = thread.getTemplate();

                String templateFilePath = m_rootDirectoryPath + File.separator + currentTime + "_finger_" + finger + ".nist";

                writeToFile(fingerTemplate, templateFilePath);
            }
        } catch (Exception ex) {
            //Log.e(APP_TAG,ex.getMessage());
        }
    }

    private void getNistQualityValues(ArrayList<SgmRectImage> rects) {
        int threadCount = rects.size();
        short defaultPpi = 500;

        GetNistQualityThread[] getNistQualityThreads = null;

        try {
            getNistQualityThreads = new GetNistQualityThread[threadCount];
            ArrayList<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                SgmRectImage rect = rects.get(threadIndex);

                getNistQualityThreads[threadIndex] = new GetNistQualityThread(m_cellSdk, rect);

                Future future = m_service.submit(getNistQualityThreads[threadIndex]);
                futures.add(future);
            }

            for (Future<Runnable> future : futures) {
                future.get();
            }

            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                m_nistQuality[threadIndex] = getNistQualityThreads[threadIndex].getNistQuality();
            }
        } catch (Exception ex) {
            //Log.e(APP_TAG,ex.getMessage());
        }
    }

    private void getFingerprintQualityValues(ArrayList<SgmRectImage> rects) {
        int threadCount = rects.size();
        short defaultPpi = 500;

        GetFingerprintQualityThread[] getFingerprintQualityThreads = null;

        try {
            getFingerprintQualityThreads = new GetFingerprintQualityThread[threadCount];
            ArrayList<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                SgmRectImage rect = rects.get(threadIndex);

                getFingerprintQualityThreads[threadIndex] = new GetFingerprintQualityThread(m_cellSdk, rect);

                Future future = m_service.submit(getFingerprintQualityThreads[threadIndex]);
                futures.add(future);
            }

            for (Future<Runnable> future : futures) {
                future.get();
            }

            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                m_quality[threadIndex] = getFingerprintQualityThreads[threadIndex].getFingerprintQuality();
            }
        } catch (Exception ex) {
            //Log.e(APP_TAG,ex.getMessage());
        }
    }

    public static File getLogFile() {
        return m_logFile;
    }

    private void initBorder(int width, int height) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(m_transparentImageView.getWidth(), m_transparentImageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawColor(getResources().getColor(R.color.colorPrimaryTrans));
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            m_cellSdk.initBorder(width, height, m_graphicOverlay.getWidth(), m_graphicOverlay.getHeight());

            Integer borderRectLeft = new Integer(0);
            Integer borderRectTop = new Integer(0);
            Integer borderRectRight = new Integer(0);
            Integer borderRectBottom = new Integer(0);

            m_cellSdk.getBorderRectangle(borderRectLeft, borderRectTop, borderRectRight, borderRectBottom);

            Rect borderRect = new Rect(borderRectLeft, borderRectTop, borderRectRight, borderRectBottom);

            m_graphicOverlay.init(borderRect);

            Logger.addToLog(TAG, "Drawing rect with " + " left: " + borderRectLeft + " top: " + borderRectTop + " right: " + borderRectRight + " bottom: " + borderRectBottom, m_logFile);

            Logger.addToLog(TAG, "width: " + width + " height: " + height, m_logFile);

            canvas.drawRect(borderRect, paint);

            m_transparentImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoFocus() {
        try {
            MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(m_viewFinder.getWidth(), m_viewFinder.getHeight());

            int centerWidth = m_viewFinder.getWidth() / 2;
            int centreHeight = m_viewFinder.getHeight() / 2;

            MeteringPoint autoFocusPoint = factory.createPoint(centerWidth, centreHeight);

            FocusMeteringAction.Builder builder = new FocusMeteringAction.Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF | FocusMeteringAction.FLAG_AE);

            builder.setAutoCancelDuration(1, TimeUnit.SECONDS);
            m_camera.getCameraControl().startFocusAndMetering(builder.build());

        } catch (Exception e) {
            Logger.addToLog(TAG, "auto focus failed " + e.getLocalizedMessage(), m_logFile);
            Logger.logException(TAG, e, m_logFile);
        }
    }

    void toggleFlash(boolean enable) {
        if (m_camera == null) {
            return;
        }

        CameraInfo cameraInfo = m_camera.getCameraInfo();

        if (m_camera.getCameraInfo().hasFlashUnit() && cameraInfo.getTorchState().getValue() != null) {
            m_camera.getCameraControl().enableTorch(enable);
        }
    }

    private void setScreenBrightnessFull() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(params);
    }

    private void setStatus(String msg) {
        runOnUiThread(() -> {
            if (msg != null && !msg.equals("")) {
                m_statusTextView.setText(msg);
            } else {
                m_statusTextView.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleFlash(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleFlash(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideProgress();


        if (m_cellSdk != null) {
            m_cellSdk.closeSdk();
            m_cellSdk = null;
        }
    }

    private void hideProgress() {
        runOnUiThread(() -> {
            if (m_progressDialog != null && m_progressDialog.isShowing()) {
                m_progressDialog.dismiss();
            }
        });
    }
}
