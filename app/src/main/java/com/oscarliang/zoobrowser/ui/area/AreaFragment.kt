package com.oscarliang.zoobrowser.ui.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.binding.FragmentDataBindingComponent
import com.oscarliang.zoobrowser.databinding.FragmentAreaBinding
import com.oscarliang.zoobrowser.di.Injectable
import com.oscarliang.zoobrowser.ui.common.AnimalListAdapter
import com.oscarliang.zoobrowser.ui.common.RetryListener
import com.oscarliang.zoobrowser.util.autoCleared
import javax.inject.Inject

class AreaFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentAreaBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()
    private val viewModel: AreaViewModel by viewModels {
        viewModelFactory
    }
    private var adapter by autoCleared<AnimalListAdapter>()
    private val params by navArgs<AreaFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentAreaBinding>(
            inflater,
            R.layout.fragment_area,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.setQuery(params.area.name, 10)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        val rvAdapter = AnimalListAdapter(
            dataBindingComponent = dataBindingComponent,
            itemClickListener = {},
            bookmarkClickListener = {
                viewModel.toggleBookmark(it)
            }
        )
        binding.area = params.area
        binding.animals = viewModel.animals
        binding.retryListener = object : RetryListener {
            override fun retry() {
                viewModel.retry()
            }
        }
        binding.animalList.apply {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(this@AreaFragment.context,
                resources.getInteger(R.integer.columns_count))
            itemAnimator?.changeDuration = 0
        }
        this.adapter = rvAdapter
        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewModel.animals.observe(viewLifecycleOwner) { animals ->
            adapter.submitList(animals?.data)
        }
    }

}