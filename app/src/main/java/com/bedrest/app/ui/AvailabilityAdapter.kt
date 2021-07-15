package com.bedrest.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bedrest.app.data.model.Availability
import com.bedrest.app.databinding.ItemAvailabilityBinding

class AvailabilityAdapter(
    private val onDirectionListener: () -> Unit
) :
    ListAdapter<Availability, AvailabilityAdapter.AvailabilityViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailabilityViewHolder {
        return AvailabilityViewHolder(
            ItemAvailabilityBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AvailabilityViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class AvailabilityViewHolder(
        private val binding: ItemAvailabilityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(dataItem: Availability) {
            binding.data = dataItem
            binding.executePendingBindings()
            binding.tvDirection.setOnClickListener {
                onDirectionListener()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Availability>() {
        override fun areItemsTheSame(oldItem: Availability, newItem: Availability): Boolean {
            return oldItem.hospital_code == newItem.hospital_code
        }

        override fun areContentsTheSame(oldItem: Availability, newItem: Availability): Boolean {
            return oldItem == newItem
        }
    }
}