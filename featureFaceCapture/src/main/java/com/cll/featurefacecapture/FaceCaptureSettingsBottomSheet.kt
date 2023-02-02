package com.cll.featurefacecapture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cll.featurefacecapture.databinding.SelfieSettingsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

data class FaceCaptureSettings(
    var useFrontCamera: Boolean = true,
    var useAutoCapture: Boolean = true,
    var useFrameCapture: Boolean = true
)

class FaceCaptureSettingsBottomSheet : BottomSheetDialogFragment() {


    private var _binding: SelfieSettingsBottomSheetBinding? = null

    private val binding get() = _binding!!


    private var mCallback: OnChangedSettings? = null

    private lateinit var faceCaptureSettings: FaceCaptureSettings


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val useFrontCamera = arguments?.getBoolean("setUseBackCamera") ?: true
        val useAutoCapture = arguments?.getBoolean("useAutoCapture") ?: true
        val useFrameCapture = arguments?.getBoolean("useFrameCapture") ?: true
        faceCaptureSettings = FaceCaptureSettings(useFrontCamera, useAutoCapture, useFrameCapture)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = SelfieSettingsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (faceCaptureSettings.useFrontCamera) {
            binding.radGroupCameraSelector.check(R.id.rad_id_front_cam)
        } else {
            binding.radGroupCameraSelector.check(R.id.rad_id_back_cam)
        }

        if (faceCaptureSettings.useAutoCapture) {
            binding.radGroupCaptureType.check(R.id.rad_btn_auto_capture)
        } else {
            binding.radGroupCaptureType.check(R.id.rad_btn_manual_capture)
        }
        if (faceCaptureSettings.useFrameCapture) {
            binding.radGroupCaptureMode.check(R.id.rad_btn_capture_mode_fast)
        } else {
            binding.radGroupCaptureMode.check(R.id.rad_btn_capture_mode_quaity)
        }

        if (mCallback == null) {
            this.dismiss()
            return
        }
        setOnCheckedListeners()
    }


    private fun setOnCheckedListeners() {
        binding.radGroupCameraSelector.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rad_id_front_cam -> {
                    mCallback!!.setUseBackCamera(false)
                }
                R.id.rad_id_back_cam -> {
                    mCallback!!.setUseBackCamera(true)
                }
            }
        }
        binding.radGroupCaptureType.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rad_btn_auto_capture -> {
                    mCallback!!.useAutoCapture(true)
                }
                R.id.rad_btn_manual_capture -> {
                    mCallback!!.useAutoCapture(false)
                }
            }
        }
        binding.radGroupCaptureMode.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rad_btn_capture_mode_fast -> {
                    mCallback!!.useFrameCapture(true)
                }
                R.id.rad_btn_capture_mode_quaity -> {
                    mCallback!!.useFrameCapture(false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance(callback: OnChangedSettings) = FaceCaptureSettingsBottomSheet().apply {
            mCallback = callback
        }

        const val TAG: String = "FaceCaptureSettingsBottomSheet"
    }

}

interface OnChangedSettings {
    fun setUseBackCamera(boolean: Boolean)
    fun useAutoCapture(boolean: Boolean)
    fun useFrameCapture(boolean: Boolean)
}