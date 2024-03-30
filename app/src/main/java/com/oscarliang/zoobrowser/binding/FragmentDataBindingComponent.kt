package com.oscarliang.zoobrowser.binding

import androidx.databinding.DataBindingComponent
import com.oscarliang.zoobrowser.binding.FragmentBindingAdapters

class FragmentDataBindingComponent : DataBindingComponent {
    private val adapter = FragmentBindingAdapters()

    override fun getFragmentBindingAdapters() = adapter
}