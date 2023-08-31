package com.cll.remoteonboarding.ui.featurePersonalInfo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cll.remoteonboarding.R
import com.cll.remoteonboarding.databinding.FragmentPersonalDetailsBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment() {

    private var _binding: FragmentPersonalDetailsBinding? = null

    private val binding get() = _binding!!
    private val viewModel: PersonalDetailsViewModel by viewModels()


    companion object {
        fun newInstance() = PersonalDetailsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)


        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide
            .with(requireContext())
            .load("https://cdn.cms-twdigitalassets.com/content/dam/about-twitter/x/brand-toolkit/logo-black.png.twimg.2560.png")
            .centerCrop()
            .placeholder(com.cll.resourcesmodule.R.drawable.round_account_circle_24)
            .into(binding.imageView);

        (binding.inputGender.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.gender)
        (binding.inputNationality.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.nationality)
        (binding.inputDocType.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.docTypes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}