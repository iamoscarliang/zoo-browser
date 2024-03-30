package com.oscarliang.zoobrowser.binding

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("showHide")
    fun showHide(view: View, show: Boolean) {
        view.isVisible = show
    }

}
