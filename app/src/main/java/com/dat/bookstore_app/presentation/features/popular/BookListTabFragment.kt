package com.dat.bookstore_app.presentation.features.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentBookListTabBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.adapter.SearchBookAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.booklist.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListTabFragment(
) : BaseFragment<FragmentBookListTabBinding>() {

    private val viewModel: BookListViewModel by viewModels()

    private val source by lazy { arguments?.getString(ARG_SOURCE) ?: "" }

    private val adapter by lazy {
        SearchBookAdapter { book ->
            val navController = requireActivity().findNavController(R.id.nav_host_main)
            when (source) {
                "Popular" -> navController.navigate(PopularFragmentDirections.actionPopularFragmentToDetailBookFragment(book))
                "New" -> navController.navigate(NewFragmentDirections.actionNewFragmentToDetailBookFragment(book))
            }
        }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        }
    }

    private val sortType: Sort by lazy {
        requireArguments().getSerializable(ARG_SORT_TYPE) as Sort
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookListTabBinding.inflate(inflater, container, false)

    override fun setUpView() = with(binding) {
        rvBooks.adapter = adapter
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookListFlow.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    companion object {
        private const val ARG_SORT_TYPE = "arg_sort_type"
        private const val ARG_SOURCE = "arg_source"

        fun newInstance(sort: Sort, source: String) = BookListTabFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_SORT_TYPE, sort)
                putString(ARG_SOURCE, source)
            }
        }
    }
}