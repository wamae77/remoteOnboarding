package com.cll.remoteonboarding.ui.featureLogin

import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cll.remoteonboarding.R
import com.cll.remoteonboarding.databinding.FragmentFragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentLogin : Fragment() {

    private var _binding: FragmentFragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: FragmentLoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val regex = "^(2547|254|07)[0-9]{7,10}$|^\+254[0-9]{9,10}$"
        setSpannableText()

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLogin_to_FragmentFingerPrint)
        }
    }

    private fun setSpannableText() {
        var originalText = getString(R.string.forgot_your_password)

        var startIndex: Int = originalText.indexOf(originalText)
        var endIndex = startIndex + originalText.length

        createSpannableText({
            Log.d("TAG", "onViewCreated:clicked")
        }, originalText, startIndex, endIndex).let {
            binding.txtForgotPass.text = it;
            binding.txtForgotPass.movementMethod = LinkMovementMethod.getInstance()
        }

        val subString = "Sign up"
        originalText = getString(R.string.sign_up_instead)
        startIndex = originalText.indexOf(subString)
        endIndex = startIndex + subString.length

        createSpannableText({}, originalText, startIndex, endIndex).let {
            binding.tvSignUp.text = it;
            binding.tvSignUp.movementMethod = LinkMovementMethod.getInstance()
        }
    }


    private fun createSpannableText(
        onClicked: () -> Unit, originalText: String, startIndex: Int, endIndex: Int
    ): SpannableString {
        val spannableString = SpannableString(originalText)
        val linkSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClicked.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = resources.getColor(
                    com.cll.resourcesmodule.R.color.blue_400, resources.newTheme()
                )
            }
        }
        spannableString.setSpan(linkSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}