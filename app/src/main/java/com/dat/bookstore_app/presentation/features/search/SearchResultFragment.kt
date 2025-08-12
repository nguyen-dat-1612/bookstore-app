package com.dat.bookstore_app.presentation.features.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.paging.PagingData
import com.dat.bookstore_app.databinding.FragmentSearchResultBinding
import com.dat.bookstore_app.presentation.common.adapter.SearchBookAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.presentation.common.adapter.CommonLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentSearchResultBinding>() {

    private val viewModel: SearchResultViewModel by activityViewModels()
    var isFirstLoad = true
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        SearchBookAdapter(
            onBookClick = {
                navController.navigate(SearchResultFragmentDirections.actionSearchResultFragmentToDetailBookFragment(it))
            }
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchResultBinding {
        return FragmentSearchResultBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        viewModel.query = arguments?.getString("query") ?: ""
        rvSearchBook.adapter = adapter
        header.searchInput.setText(viewModel.query)
        viewModel.onSearchTriggered()

        rvSearchBook.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommonLoadStateAdapter(adapter::retry),
            footer = CommonLoadStateAdapter(adapter::retry)
        )

        header.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnSort.setOnClickListener {
            showSortPopup()
        }
        btnFilter.setOnClickListener {
            showFilterOverlay()
        }
        binding.header.searchInput.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            inputType = 0
            setOnClickListener {
                navController.navigate(
                    SearchResultFragmentDirections
                        .actionSearchResultFragmentToSearchInputFragment(viewModel.query)
                )
            }
        }
        loadingOverlay.visibility = if (viewModel.loadingState.loading.value) View.VISIBLE else View.GONE
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<String>("search_query")
                    ?.observe(viewLifecycleOwner) { query ->
                        viewModel.query = query
                        viewModel.onSearchTriggered()
                    }

                launch {
                    viewModel.booksFlow.collectLatest { pagingData ->
                        adapter.submitData(pagingData)
                        binding.rvSearchBook.scrollToPosition(0)
                        val snapshot = adapter.snapshot()
                        if (snapshot.isEmpty()) {
                            binding.filterDialog.root.visibility = View.VISIBLE
                        } else {
                            binding.filterDialog.root.visibility = View.GONE
                        }
                    }
                }
                launch {
                    viewModel.uiState.collectLatest {
                        binding.header.searchInput.setText(viewModel.query)
                    }
                }
                launch {
                    viewModel.errorsState.errors.collect {
                        showToast(it.message.toString())
                    }
                }

            }
        }
    }

    private fun showSortPopup() {
        val popup = PopupMenu(requireContext(), binding.btnSort)
        popup.menu.add(0, R.id.sort_sold_desc, 0, "Bán chạy (Giảm dần)")
        popup.menu.add(0, R.id.sort_sold_asc, 1, "Bán chạy (Tăng dần)")
        popup.menu.add(0, R.id.sort_new_desc, 2, "Mới nhất")
        popup.menu.add(0, R.id.sort_new_asc, 3, "Cũ nhất")
        popup.menu.add(0, R.id.sort_rating_desc, 4, "Đánh giá cao")
        popup.menu.add(0, R.id.sort_rating_asc, 5, "Đánh giá thấp")

        popup.setOnMenuItemClickListener { item ->
            val selectedSort = when (item.itemId) {
                R.id.sort_sold_desc -> Sort.SOLD_DESC
                R.id.sort_sold_asc -> Sort.SOLD_ASC
                R.id.sort_new_desc -> Sort.NEW_DESC
                R.id.sort_new_asc -> Sort.NEW_ASC
                R.id.sort_rating_desc -> Sort.RATING_DESC
                R.id.sort_rating_asc -> Sort.RATING_ASC
                else -> null
            }

            selectedSort?.let {
                viewModel.changeSort(it)
            }

            true
        }

        popup.show()
    }

    private fun showFilterOverlay() {
        val dialog = FilterDialogFragment()
        dialog.show(parentFragmentManager, "FilterDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearData()
    }
}
