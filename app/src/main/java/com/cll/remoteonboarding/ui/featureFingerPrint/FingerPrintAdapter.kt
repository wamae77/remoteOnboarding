package com.cll.remoteonboarding.ui.featureFingerPrint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cll.remoteonboarding.R
import com.cll.remoteonboarding.databinding.ItemFingerprintBinding
import com.cll.remoteonboarding.model.FingerPrint
import dagger.hilt.android.qualifiers.ApplicationContext

class FingerPrintAdapter(private val context: Context) :
    RecyclerView.Adapter<FingerPrintAdapter.ViewHolder>() {

    private val list: ArrayList<FingerPrint> = arrayListOf()

    private var _binding: ItemFingerprintBinding? = null
    private val binding get() = _binding!!

    class ViewHolder(
        private val binding: ItemFingerprintBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            ItemFingerprintBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isNotEmpty()) {
            with(list[position]) {
                Glide.with(context).load(this.imagePath).circleCrop().into(binding.image)
                binding.tvPosition.text = this.nistPosCode

                val text = context.getString(R.string.quality) + this.quality.toString()
                binding.tvQuality.text = text
            }
        }
    }

    fun updateList(newList: List<FingerPrint>) {
        list.clear()
        list.addAll(newList)
        this.notifyDataSetChanged()
    }
}