package com.cll.featurefacecapture

import ai.tech5.pheonix.capture.controller.AirsnapFaceThresholds
import ai.tech5.pheonix.capture.controller.FaceCaptureController
import ai.tech5.pheonix.capture.controller.FaceCaptureListener
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.cll.core.ApplicationUtils
import com.cll.core.ApplicationUtilsImpl
import com.cll.featurefacecapture.databinding.FragmentFaceBinding
import com.phoenixcapture.camerakit.FaceBox
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FaceFragment : Fragment(), ApplicationUtils by ApplicationUtilsImpl(), FaceCaptureListener,
    OnChangedSettings {
    private var _binding: FragmentFaceBinding? = null

    private val binding get() = _binding!!
    private var controller: FaceCaptureController? = null
    private val viewModel: FaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCurrentContext = requireContext()


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect() { ui ->
                    ui.image?.let { showImage(ui.image.path) }
                }
            }
        }
        binding.btnSettings.setOnClickListener {
            val modal = FaceCaptureSettingsBottomSheet.newInstance(this)
            modal.show(childFragmentManager, FaceCaptureSettingsBottomSheet.TAG)
        }
        binding.btnCapture.setOnClickListener {
            faceExtraction();
        }
    }

    private fun showImage(filename: String) {
        val file = File(requireContext().filesDir, filename)
        Glide.with(requireContext()).load(file).circleCrop().into(binding.faceImage)
    }


    private fun faceExtraction() {
        controller = FaceCaptureController.getInstance()
        controller!!.setUseBackCamera(USE_BACK_CAMERA)
        controller!!.setAutoCapture(USE_AUTO_CAPTURE)
        controller!!.setOcclusionEnabled(DEFAULT_IS_OCCLUSION_ENABLED)
        controller!!.setEyeClosedEnabled(DEFAULT_IS_EYE_CLOSED_ENABLED)
        controller!!.setMessagesFrequency(6)
        controller!!.setFontSize(23)
        controller!!.setCaptureTimeoutInSecs(DEFAULT_CAPTURE_TIMEOUT)
        controller!!.setShowBackButton(true)
        controller!!.setFrameCapture(USE_VIDEO_CAPTURE)
        controller!!.setCompression(DEFAULT_COMPRESS_FACE_IMAGE)
        controller!!.setCompressionQuality(DEFAULT_COMPRESSION_QUALITY)
        controller!!.setIsISOEnabled(DEFAULT_ISO_ENABLED)
        controller!!.setIsGetFullFrontalCrop(DEFAULT_ISO_FULL_FRONTAL_CROP)

        val thresholds = AirsnapFaceThresholds()
        thresholds.pitcH_THRESHOLD = DEFAULT_PITCH_THRESHOLD
        thresholds.yaW_THRESHOLD = DEFAULT_YAW_THRESHOLD
        thresholds.rollThreshold = DEFAULT_ROLL_THRESHOLD
        thresholds.brisquE_THRESHOLD = DEFAULT_BRISQUE_THRESHOLD
        thresholds.masK_THRESHOLD = DEFAULT_MASK_THRESHOLD.toDouble()
        thresholds.sunglasS_THRESHOLD = DEFAULT_SUNGLASS_THRESHOLD.toDouble()
        thresholds.eyE_CLOSE_THRESHOLD = DEFAULT_EYE_CLOSE_THRESHOLD.toDouble()
        thresholds.livenesS_THRESHOLD = DEFAULT_LIVENESS_THRESHOLD.toDouble()
        thresholds.faceCentreToImageCentreTolerance = DEFAULT_IMAGE_CENTRE_TO_FACE_CENTRE_TOLERANCE
        thresholds.faceWidthToImageWidthRatioTolerance = DEFAULT_FACE_WIDTH_TOLERANCE
        controller!!.setAirsnapFaceThresholds(thresholds)
        controller!!.setEnableCaptureAfter(DEFAULT_ENABLE_CAPTURE_AFTER)
        controller!!.startFaceCapture("", requireContext(), this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        if (controller != null) {
            controller = null
        }
    }


    override fun onFaceCaptured(p0: ByteArray?, p1: FaceBox?) {
        if (p0 != null && p0.isNotEmpty()) {
            if (queryForFreeSpace() < p0.size.toLong()) {
                showDialog(getString(R.string.out_of_memory), getString(R.string.free_memory_msg))
            } else {
                saveFile(p0)
            }
        }

    }

    private fun saveFile(p0: ByteArray) {
        val filename = FILENAME + System.currentTimeMillis()
        try {
            requireContext().openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(p0)
            }
            viewModel.updateSelfieImage(filename)
        } catch (e: java.lang.Exception) {
            appToast(e.printStackTrace().toString(), Toast.LENGTH_LONG)
        }
    }

    override fun OnFaceCaptureFailed(p0: String?) {
        Toast.makeText(requireContext(), p0, Toast.LENGTH_LONG).show()
    }

    override fun onCancelled() {
        Toast.makeText(requireContext(), R.string.face_capture_cancelled, Toast.LENGTH_LONG).show();
    }

    override fun onTimedout(p0: ByteArray?) {
        Toast.makeText(requireContext(), R.string.face_capture_timedout, Toast.LENGTH_LONG).show();
    }


    override fun setUseBackCamera(boolean: Boolean) {
        USE_BACK_CAMERA = boolean
    }

    override fun useAutoCapture(boolean: Boolean) {
        USE_AUTO_CAPTURE = boolean
    }

    override fun useFrameCapture(boolean: Boolean) {
        USE_VIDEO_CAPTURE = boolean
    }

    companion object {
        private const val DEFAULT_FACE_WIDTH_TOLERANCE = 10f
        private const val DEFAULT_PITCH_THRESHOLD = 15
        private const val DEFAULT_YAW_THRESHOLD = 15
        private const val DEFAULT_ROLL_THRESHOLD = 10
        private const val DEFAULT_MASK_THRESHOLD = 0.5f
        private const val DEFAULT_SUNGLASS_THRESHOLD = 0.5f
        private const val DEFAULT_BRISQUE_THRESHOLD = 60
        private const val DEFAULT_LIVENESS_THRESHOLD = 0.5f
        private const val DEFAULT_EYE_CLOSE_THRESHOLD = 0.4f
        private const val DEFAULT_ENABLE_CAPTURE_AFTER = 6
        private const val DEFAULT_DELAY_BETWEEN_FRAMES = 0
        private const val DEFAULT_COMPRESSION_QUALITY = 80
        private const val DEFAULT_MIN_BLUR = 0.0f
        private const val DEFAULT_MAX_BLUR = 0.5f
        private const val DEFAULT_MIN_EXPOSURE = 0.2f
        private const val DEFAULT_MAX_EXPOSURE = 0.7f
        private const val DEFAULT_MIN_BRIGHTNESS = 0.2f
        private const val DEFAULT_MAX_BRIGHTNESS = 0.7f
        private const val DEFAULT_SKINTONE = 0.5f
        private const val DEFAULT_HOTSPOTS = 0.5f
        private const val DEFAULT_RED_EYES = 0.5f
        private const val DEFAULT_MOUTH_OPEN = 0.5f
        private const val DEFAULT_LAUGH = 0.5f
        private const val DEFAULT_UNIFORM_BACKGROUND = 0.5f
        private const val DEFAULT_UNIFORM_BACKGROUND_COLOR = 0.5f
        private const val DEFAULT_UNIFORM_ILLUMINATION = 0.5f
        private const val DEFAULT_IMAGE_CENTRE_TO_FACE_CENTRE_TOLERANCE = 10f
        private const val DEFAULT_IS_OCCLUSION_ENABLED = true
        private const val DEFAULT_IS_EYE_CLOSED_ENABLED = true
        private const val DEFAULT_IS_LIVENESS_ENABLED = false
        private const val DEFAULT_CAPTURE_TIMEOUT = 50
        private const val DEFAULT_ISO_ENABLED = true
        private const val DEFAULT_COMPRESS_FACE_IMAGE = false
        private const val DEFAULT_ISO_FULL_FRONTAL_CROP = false

        private var USE_BACK_CAMERA = false
        private var USE_AUTO_CAPTURE = true
        private var USE_VIDEO_CAPTURE = true

        private const val FILENAME = "t5_AirSpace_face"

    }

}