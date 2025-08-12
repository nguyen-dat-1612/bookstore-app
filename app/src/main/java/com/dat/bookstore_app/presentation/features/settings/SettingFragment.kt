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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private val settingViewModel: SettingViewModel by viewModels<SettingViewModel>()

    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private val navController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_main)
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(getString(R.string.server_client_id), false)
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnLogout.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                settingViewModel.logout()
            }
        }
        btnChangePassword.setOnClickListener {
            if (settingViewModel.uiState.value.user?.noPassword!!) {
                navController.navigate(R.id.action_settingFragment_to_createPasswordFragment)
            } else {
                navController.navigate(R.id.action_settingFragment_to_changePasswordFragment)
            }
        }

//        switchNotification.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                settingViewModel.fetchAndEnableNotification()
//            } else {
//                settingViewModel.disableNotification()
//            }
//        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingViewModel.uiState.collectLatest {
                        if (it.LogoutSuccess) {
                            mainViewModel.updateLoggedIn(false)
                            navController.popBackStack()
                        }

                    }
                }
                launch {
                    settingViewModel.errorsState.errors.collect {
                        showToast(it.message.toString())
                    }
                }

            }
        }
    }
}