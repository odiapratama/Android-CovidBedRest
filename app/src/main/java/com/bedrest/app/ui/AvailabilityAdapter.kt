package com.bedrest.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bedrest.app.R
import com.bedrest.app.data.model.Availability
import com.bedrest.app.databinding.ItemAvailabilityBinding

class AvailabilityAdapter(
    private val onDirectionListener: (String, String, String) -> Unit,
    private val onDetailListener: (String) -> Unit,
    private val onPhoneListener: (String) -> Unit
) : ListAdapter<Availability, AvailabilityAdapter.AvailabilityViewHolder>(DiffCallback()) {

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
            if (dataItem.available_bed == "0") {
                binding.clParent.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_rounded_grey_red_line)
                binding.clParent.alpha = 0.5f
            } else {
                binding.clParent.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_rounded_white)
                binding.clParent.alpha = 1f
            }
            binding.executePendingBindings()
            binding.tvDirection.setOnClickListener {
                onDirectionListener(dataItem.lat, dataItem.lon, dataItem.name)
            }
            binding.tvDetail.setOnClickListener {
                onDetailListener(dataItem.bed_detail_link)
            }
            binding.tvPhone.setOnClickListener {
                onPhoneListener(dataItem.hotline)
            }
            if (dataItem.hotline.isEmpty()) {
                binding.tvPhone.isClickable = false
                binding.tvPhone.alpha = 0.5f
            } else {
                binding.tvPhone.isClickable = true
                binding.tvPhone.alpha = 1f
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