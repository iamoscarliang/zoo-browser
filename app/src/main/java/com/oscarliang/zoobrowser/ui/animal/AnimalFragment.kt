package com.oscarliang.zoobrowser.ui.animal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.oscarliang.zoobrowser.R
import com.oscarliang.zoobrowser.binding.FragmentDataBindingComponent
import com.oscarliang.zoobrowser.databinding.FragmentAnimalBinding
import com.oscarliang.zoobrowser.di.Injectable
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.ui.common.BookmarkListener
import com.oscarliang.zoobrowser.ui.common.ClickListener
import com.oscarliang.zoobrowser.util.autoCleared
import javax.inject.Inject

class AnimalFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentAnimalBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()

    private val viewModel: AnimalViewModel by viewModels() {
        viewModelFactory
    }
    private val params by navArgs<AnimalFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentAnimalBinding>(
            inflater,
            R.layout.fragment_animal,
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
            viewModel.setAnimalId(params.animalId)
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.animal = viewModel.animal
        binding.backListener = object : ClickListener {
            override fun onClick() {
                NavHostFragment.findNavController(this@AnimalFragment).navigateUp()
            }
        }
        binding.bookmarkListener = object : BookmarkListener {
            override fun onBookmark(animal: Animal) {
                viewModel.toggleBookmark(animal)
            }
        }
    }

}