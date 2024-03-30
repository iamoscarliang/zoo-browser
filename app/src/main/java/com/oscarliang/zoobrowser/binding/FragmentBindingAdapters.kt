package com.oscarliang.zoobrowser.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.oscarliang.zoobrowser.R

class FragmentBindingAdapters {

    @BindingAdapter(value = ["imageUrl"])
    fun bindImage(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_zoo_gray)
            .error(R.drawable.ic_error)
            .into(imageView)
    }

}
