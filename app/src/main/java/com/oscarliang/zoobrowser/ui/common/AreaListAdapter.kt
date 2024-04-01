package com.oscarliang.zoobrowser.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.databinding.LayoutAreaItemBinding
import com.oscarliang.zoobrowser.model.Area

class AreaListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val itemClickListener: ((Area) -> Unit)?
) : DataBoundListAdapter<Area, LayoutAreaItemBinding>(
    object : DiffUtil.ItemCallback<Area>() {
        override fun areItemsTheSame(oldItem: Area, newItem: Area): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Area, newItem: Area): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): LayoutAreaItemBinding {
        val binding = DataBindingUtil.inflate<LayoutAreaItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_area_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.area?.let {
                itemClickListener?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: LayoutAreaItemBinding, item: Area) {
        binding.area = item
    }

}
