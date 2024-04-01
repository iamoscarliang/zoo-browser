package com.oscarliang.zoobrowser.ui.zoo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.binding.FragmentDataBindingComponent
import com.oscarliang.zoobrowser.databinding.FragmentZooBinding
import com.oscarliang.zoobrowser.di.Injectable
import com.oscarliang.zoobrowser.ui.common.AreaListAdapter
import com.oscarliang.zoobrowser.ui.common.RetryListener
import com.oscarliang.zoobrowser.util.autoCleared
import javax.inject.Inject

class ZooFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentZooBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()
    private val viewModel: ZooViewModel by viewModels {
        viewModelFactory
    }
    private var adapter by autoCleared<AreaListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentZooBinding>(
            inflater,
            R.layout.fragment_zoo,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.refresh()
        }

        binding.lifecycleOwner = viewLifecycleOwner
        val rvAdapter = AreaListAdapter(
            dataBindingComponent = dataBindingComponent,
            itemClickListener = {
//                findNavController()
//                    .navigate(
//                        BreakingNewsFragmentDirections.actionToNewsDetailFragment(
//                            it.id
//                        )
//                    )
            }
        )
        binding.areas = viewModel.areas
        binding.listener = object : RetryListener {
            override fun retry() {
                viewModel.refresh()
            }
        }
        binding.areaList.apply {
            adapter = rvAdapter
            itemAnimator?.changeDuration = 0
        }
        this.adapter = rvAdapter
        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewModel.areas.observe(viewLifecycleOwner) { areas ->
            adapter.submitList(areas?.data)
        }
    }

}