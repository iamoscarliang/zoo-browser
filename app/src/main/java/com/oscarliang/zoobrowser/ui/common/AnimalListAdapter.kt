package com.oscarliang.zoobrowser.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.databinding.LayoutAnimalItemBinding
import com.oscarliang.zoobrowser.model.Animal

class AnimalListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val itemClickListener: ((Animal) -> Unit)?,
    private val bookmarkClickListener: ((Animal) -> Unit)?
) : DataBoundListAdapter<Animal, LayoutAnimalItemBinding>(
    object : DiffUtil.ItemCallback<Animal>() {
        override fun areItemsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): LayoutAnimalItemBinding {
        val binding = DataBindingUtil.inflate<LayoutAnimalItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_animal_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.animal?.let {
                itemClickListener?.invoke(it)
            }
        }
        binding.btnBookmark.setOnClickListener {
            binding.animal?.let {
                bookmarkClickListener?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: LayoutAnimalItemBinding, item: Animal) {
        binding.animal = item
    }

}