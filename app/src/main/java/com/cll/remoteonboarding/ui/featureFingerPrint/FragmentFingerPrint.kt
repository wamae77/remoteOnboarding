package com.cll.remoteonboarding.ui.featureFingerPrint

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cll.FingerPrintModule.FingerCaptureActivity
import com.cll.FingerPrintModule.utils.ResultScan
import com.cll.core.ApplicationUtils
import com.cll.core.ApplicationUtilsImpl
import com.cll.remoteonboarding.R
import com.cll.remoteonboarding.databinding.FragmentFragmentFingerPrintBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FragmentFingerPrint : Fragment(), ApplicationUtils by ApplicationUtilsImpl() {

    private var _binding: FragmentFragmentFingerPrintBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: FragmentFingerPrintViewModel by viewModels()


    private lateinit var fingerPrintAdapter: FingerPrintAdapter
    private lateinit var popup: ListPopupWindow

    companion object {
        private const val username = "wamai"
        private const val livenessCheck = false
        private const val showEllipses = true
        private const val createTemplates = true
        private const val orientationCheck = false
        private const val saveSdkLog = false
        private const val detectorThreshold = 0.9f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragmentFingerPrintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCurrentContext = requireContext()
        createPopUpWindow()

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3);
        fingerPrintAdapter = FingerPrintAdapter(requireContext())
        binding.recyclerView.adapter = fingerPrintAdapter


        binding.btnNext.setOnClickListener {

//            findNavController().navigate(
//                R.id.action_fragmentFingerPrint_to_SecondFragment, bundleOf("userId" to 1)
//            )
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.list.isNullOrEmpty()) {
                        binding.txtStatus.visibility = View.VISIBLE
                    } else {
                        fingerPrintAdapter.updateList(it.list)
                        binding.txtStatus.visibility = View.GONE
                        binding.btnNext.visibility = View.VISIBLE
                    }
                }
            }
        }


        binding.btnCapture.setOnClickListener {
            popup.show()
        }
    }

    private fun createPopUpWindow() {
        popup =
            ListPopupWindow(requireContext(), null, androidx.appcompat.R.attr.listPopupWindowStyle)
        popup.anchorView = binding.btnCapture
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_popup_window_item,
            resources.getStringArray(R.array.fingerPositionIndex)
        )
        popup.setAdapter(adapter)


        popup.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val mFingerCaptureActivity = FingerCaptureActivity()
            mFingerCaptureActivity.setPositionCodeIndex(position);
            mFingerCaptureActivity.setUsername(username);
            mFingerCaptureActivity.setLivenessCheck(livenessCheck);
            mFingerCaptureActivity.setShowEllipses(showEllipses);
            mFingerCaptureActivity.setCreateTemplates(createTemplates);
            mFingerCaptureActivity.setOrientationCheck(orientationCheck);
            mFingerCaptureActivity.setSaveSdkLogFlag(saveSdkLog);
            mFingerCaptureActivity.setDetectorThreshold(detectorThreshold);

            val i = Intent(requireContext(), mFingerCaptureActivity::class.java)
            resultLauncher.launch(i)
            popup.dismiss()
        }

    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                // val resultFilePath = result.data!!.getStringExtra("resultFilePath")
                viewModel.addFingerPrints(ResultScan.getInstance().list)

            } else {

            }

        }


}