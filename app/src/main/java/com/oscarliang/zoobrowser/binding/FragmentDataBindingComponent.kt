package com.oscarliang.zoobrowser.binding

import androidx.databinding.DataBindingComponent
import com.oscarliang.zoobrowser.binding.FragmentBindingAdapters

class FragmentDataBindingComponent : DataBindingComponent {
    private val adapter = FragmentBindingAdapters()

    fun getFragmentBindingAdapters() = adapter
}