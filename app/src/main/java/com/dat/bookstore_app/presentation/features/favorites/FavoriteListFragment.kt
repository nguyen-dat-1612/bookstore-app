package com.dat.bookstore_app.presentation.features.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentFavoriteListBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.adapter.FavoriteAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R

@AndroidEntryPoint
class FavoriteListFragment : BaseFragment<FragmentFavoriteListBinding>() {

    private val favoriteListViewModel: FavoriteListViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        FavoriteAdapter(
            onClick = {
                navController.navigate(FavoriteListFragmentDirections.actionFavoriteListFragmentToDetailBookFragment(it.book))
            },
            onDelete = { favorite ->
                favoriteListViewModel.deleteFavorite(favorite.id, Sort.NEW_DESC)
            },
            onBuyNow = { favorite ->
                favoriteListViewModel.addToCart(favorite.book.id)
            }
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteListBinding {
        return FragmentFavoriteListBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding){
        rvFavorites.adapter = adapter
        favoriteListViewModel.loadFavorite(Sort.NEW_DESC)
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    favoriteListViewModel.favoritePagingFlow.collectLatest {
                        adapter.submitData(it)
                    }
                }
                launch {
                    favoriteListViewModel.uiState.collectLatest { state ->
                        if (state.addCartSuccess) {
                            showToast("Sản phẩm đã được thêm vào giỏ hàng")
                            favoriteListViewModel.resetState()

                        }
                    }
                }
            }
        }

    }
}