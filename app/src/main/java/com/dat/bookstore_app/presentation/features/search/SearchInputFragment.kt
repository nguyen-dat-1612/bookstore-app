package com.dat.bookstore_app.presentation.features.search

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentSearchBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import com.dat.bookstore_app.R
import com.dat.bookstore_app.presentation.common.adapter.SearchHistoryAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchInputFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel: SearchInputViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        SearchHistoryAdapter(
            onItemClick = {
                onSearch(it)
            }
        )
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        header.btnBack.setOnClickListener {
            navController.popBackStack()
        }
        rvSearchHistory.adapter = adapter

        txClearAll.setOnClickListener {
            viewModel.clearSearch()
        }
        header.searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            ) {
                val query = header.searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.saveQuery(query)

                    onSearch(query = query)

                    header.searchInput.clearFocus()
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }
        header.searchInput.setText(viewModel.query)
    }
    private fun onSearch(query: String) {
        binding.header.searchInput.setText(query)
        val hasResult = try {
            navController.getBackStackEntry(R.id.searchResultFragment)
            true
        } catch (e: IllegalArgumentException) {
            false
        }

        if (hasResult) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("search_query", query)

            navController.popBackStack(R.id.searchResultFragment, false)
        } else {
            // Lần đầu vào result
            val action = SearchInputFragmentDirections
                .actionSearchInputFragmentToSearchResultFragment(query)
            navController.navigate(action)
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    adapter.submitList(it.listHistorySearch)
                }

                viewModel.errorsState.errors.collectLatest {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    // Hàm ẩn bàn phím
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.header.searchInput.windowToken, 0)
    }
}