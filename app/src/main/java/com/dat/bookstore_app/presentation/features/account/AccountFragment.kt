package com.dat.bookstore_app.presentation.features.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentAccountBinding
import com.dat.bookstore_app.presentation.features.main.MainViewModel
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    val mainViewModel: MainViewModel by activityViewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountBinding {
        return FragmentAccountBinding.inflate(inflater, container,false);
    }

    override fun setUpView() {

    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState.collectLatest{ state ->
                    val fragment = if (state.isLoggedIn) {
                        ProfileFragment()
                    } else {
                        AuthTabFragment()
                    }


                    val current = childFragmentManager.findFragmentById(R.id.account_container)
                    if (current?.javaClass != fragment.javaClass) {
                        childFragmentManager.beginTransaction()
                            .replace(R.id.account_container, fragment)
                            .commitNowAllowingStateLoss()
                    }
                }
            }
        }


    }
}