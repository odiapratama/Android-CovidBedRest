package com.bedrest.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bedrest.app.databinding.ItemProvinceSuggestionBinding

class ProvinceSuggestionAdapter(
    private val listProvince: Array<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ProvinceSuggestionAdapter.SuggestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        return SuggestionViewHolder(
            ItemProvinceSuggestionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.binTo(listProvince[position])
    }

    override fun getItemCount() = listProvince.size

    inner class SuggestionViewHolder(
        private val binding: ItemProvinceSuggestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun binTo(item: String) {
            binding.data = item
            binding.executePendingBindings()
            binding.tvSuggestion.setOnClickListener {
                onClick(item)
            }
        }
    }
}