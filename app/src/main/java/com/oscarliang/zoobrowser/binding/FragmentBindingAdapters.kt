package com.oscarliang.zoobrowser.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.oscarliang.zoobrowser.R
import java.lang.StringBuilder

class FragmentBindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"])
        fun bindImage(imageView: ImageView, url: String?) {
            // Convert http to https
            var formatUrl = url
            if (url?.get(4) != 's') {
                formatUrl = StringBuilder(url).apply { insert(4, 's') }.toString()
            }
            Glide.with(imageView.context)
                .load(formatUrl)
                .placeholder(R.drawable.ic_zoo_gray)
                .error(R.drawable.ic_error)
                .into(imageView)
        }
    }

}
