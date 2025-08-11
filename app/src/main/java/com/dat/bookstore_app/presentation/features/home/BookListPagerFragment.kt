package com.dat.bookstore_app.presentation.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentBookListPagerBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.adapter.BookAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.BottomNavFragmentDirections
import com.dat.bookstore_app.presentation.features.popular.BookListTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListPagerFragment : BaseFragment<FragmentBookListPagerBinding>() {

    private val viewModel: BookPagerViewModel by viewModels()
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        BookAdapter(true) { book ->
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToDetailBookFragment(book))
        }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        }
    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookListPagerBinding {
        return FragmentBookListPagerBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        rvBooks.adapter = adapter
        rvBooks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest {
                        if (it.bookList.isNotEmpty()){
                            adapter.submitList(it.bookList)
                        }
                    }
                }
                launch {
                    viewModel.errorsState.errors.collect {
                        if (it.message != null) {
                            showToast(it.message.toString())
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_SORT_TYPE = "arg_sort_type"

        fun newInstance(sort: Sort) = BookListPagerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_SORT_TYPE, sort)
            }
        }
    }

}