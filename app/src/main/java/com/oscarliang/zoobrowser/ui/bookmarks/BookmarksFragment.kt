package com.oscarliang.zoobrowser.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.binding.FragmentDataBindingComponent
import com.oscarliang.zoobrowser.databinding.FragmentBookmarksBinding
import com.oscarliang.zoobrowser.di.Injectable
import com.oscarliang.zoobrowser.ui.common.AnimalListAdapter
import com.oscarliang.zoobrowser.util.autoCleared
import javax.inject.Inject

class BookmarksFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentBookmarksBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val viewModel: BookmarksViewModel by viewModels {
        viewModelFactory
    }
    private var adapter by autoCleared<AnimalListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = DataBindingUtil.inflate<FragmentBookmarksBinding>(
            inflater,
            R.layout.fragment_bookmarks,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvAdapter = AnimalListAdapter(
            dataBindingComponent = dataBindingComponent,
            itemClickListener = {
                findNavController().navigate(
                    BookmarksFragmentDirections.actionBookmarksFragmentToAnimalFragment(
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
        binding.bookmarks = viewModel.bookmarks
        binding.repoList.apply {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(
                this@BookmarksFragment.context,
                resources.getInteger(R.integer.columns_count)
            )
            itemAnimator?.changeDuration = 0
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewModel.bookmarks.observe(viewLifecycleOwner) { repos ->
            adapter.submitList(repos)
        }
    }

}