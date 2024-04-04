package com.oscarliang.zoobrowser.binding

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import com.oscarliang.zoobrowser.binding.FragmentBindingAdapters

class FragmentDataBindingComponent(fragment: Fragment) : DataBindingComponent {
    private val adapter = FragmentBindingAdapters(fragment)

    override fun getFragmentBindingAdapters() = adapter
}