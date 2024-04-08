package com.oscarliang.zoobrowser.binding

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.oscarliang.zoobrowser.R

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("showHide")
    fun showHide(view: View, show: Boolean) {
        view.isVisible = show
    }

    @JvmStatic
    @BindingAdapter("refreshHide")
    fun refreshHide(view: SwipeRefreshLayout, isRefreshing: Boolean) {
        if (view.isRefreshing) {
            view.isRefreshing = isRefreshing
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrl"])
    fun loadImage(imageView: ImageView, url: String?) {
        // Convert http to https
        var formatUrl = url
        url?.let {
            if (url.length > 4 && url[4] != 's') {
                formatUrl = StringBuilder(url).apply { insert(4, 's') }.toString()
            }
        }

        Glide.with(imageView.context)
            .load(formatUrl)
            .placeholder(R.drawable.ic_zoo_gray)
            .error(R.drawable.ic_error)
            .into(imageView)
    }

}
