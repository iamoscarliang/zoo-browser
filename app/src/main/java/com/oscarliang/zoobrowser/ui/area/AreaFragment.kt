package com.oscarliang.zoobrowser.ui.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.binding.FragmentDataBindingComponent
import com.oscarliang.zoobrowser.databinding.FragmentAreaBinding
import com.oscarliang.zoobrowser.di.Injectable
import com.oscarliang.zoobrowser.ui.common.AnimalListAdapter
import com.oscarliang.zoobrowser.ui.common.ClickListener
import com.oscarliang.zoobrowser.util.autoCleared
import javax.inject.Inject

class AreaFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentAreaBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val viewModel: AreaViewModel by viewModels {
        viewModelFactory
    }
    private var adapter by autoCleared<AnimalListAdapter>()
    private val params by navArgs<AreaFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = DataBindingUtil.inflate<FragmentAreaBinding>(
            inflater,
            R.layout.fragment_area,
            container,
            false,
            dataBindingComponent
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.setQuery(params.area.name, 10)
        }

        val rvAdapter = AnimalListAdapter(
            dataBindingComponent = dataBindingComponent,
            itemClickListener = {
                findNavController().navigate(
                    AreaFragmentDirections.actionAreaFragmentToAnimalFragment(
                        it.id
                    )
                )
            },
            bookmarkClickListener = {
                viewModel.toggleBookmark(it)
            }
        )
        this.adapter = rvAdapter

        binding.lifecycleOwner = viewLifecycleOwner
        binding.area = params.area
        binding.animals = viewModel.animals
        binding.backListener = object : ClickListener {
            override fun onClick() {
                NavHostFragment.findNavController(this@AreaFragment).navigateUp()
            }
        }
        binding.retryListener = object : ClickListener {
            override fun onClick() {
                viewModel.retry()
            }
        }
        binding.animalList.apply {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(
                this@AreaFragment.context,
                resources.getInteger(R.integer.columns_count)
            )
            itemAnimator?.changeDuration = 0
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            // Check is scroll to bottom
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                viewModel.loadNextPage()
            }
        }
        viewModel.animals.observe(viewLifecycleOwner) { animals ->
            adapter.submitList(animals?.data)
        }
        viewModel.loadMoreStatus.observe(viewLifecycleOwner) { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

}