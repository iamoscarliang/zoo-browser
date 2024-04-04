package com.oscarliang.zoobrowser.ui.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.databinding.FragmentBrowserBinding
import com.oscarliang.zoobrowser.util.autoCleared

class BrowserFragment : Fragment() {

    var binding by autoCleared<FragmentBrowserBinding>()

    private val params by navArgs<BrowserFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = DataBindingUtil.inflate<FragmentBrowserBinding>(
            inflater,
            R.layout.fragment_browser,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.loadUrl(params.url)
    }

}