package com.dat.bookstore_app.presentation.features.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentSettingBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private val settingViewModel: SettingViewModel by viewModels<SettingViewModel>()

    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private val navController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_main)
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding){
        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnLogout.setOnClickListener {
            settingViewModel.logout()
        }
        btnChangePassword.setOnClickListener {
            navController.navigate(R.id.action_settingFragment_to_changePasswordFragment)
        }

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingViewModel.fetchAndEnableNotification()
            } else {
                settingViewModel.disableNotification()
            }
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.uiState.collectLatest {
                    if (it.LogoutSuccess) {
                        mainViewModel.updateLoggedIn(false)
                        navController.popBackStack()
                    }
                }

            }
        }
    }
}