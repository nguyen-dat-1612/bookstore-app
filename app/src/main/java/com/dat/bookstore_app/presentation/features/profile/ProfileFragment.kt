package com.dat.bookstore_app.presentation.features.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentProfileBinding
import com.dat.bookstore_app.domain.models.User
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.extension.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.findNavController
import coil3.load
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.presentation.features.main.BottomNavFragmentDirections

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels<ProfileViewModel>()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        btnSettings.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToSettingFragment(
                    viewModel.uiState.value.user?.id!!
                )
            )
        }
        btnOrderList.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment()
            )
        }
        ivPaymentPending.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(
                    OrderStatus.PENDING.name
                )
            )
        }
        ivProcessing.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(
                    OrderStatus.CONFIRMED.name
                )
            )
        }
        ivShipping.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(
                    OrderStatus.SHIPPING.name
                )
            )
        }
        ivCompleted.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(
                    OrderStatus.DELIVERED.name
                )
            )
        }
        btnPersonalProfile.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToPersonalProfileFragment()
            )
        }
        btnFavorite.setOnClickListener {
            navController.navigate(
                BottomNavFragmentDirections.actionBottomNavFragmentToFavoriteListFragment()
            )
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.user != null) {
                        setUpProfile(state.user)
                        requestNotificationPermission()
                    }
                }
                viewModel.errorsState.errors.collectLatest {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpProfile(user: User) = with(binding) {
        tvUsername.text = user.fullName
        if (user.avatar != null)  {
            userAvatar.loadUrl(user.avatar)
        } else {
            userAvatar.load(R.drawable.ic_placeholder_avatar)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Đã bật quyền thông báo", Toast.LENGTH_SHORT).show()
                viewModel.fetchAndEnableNotification()
            } else {
                Toast.makeText(requireActivity(), "Quyền thông báo bị từ chối", Toast.LENGTH_SHORT).show()
//                openNotificationSettings()
            }
        }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (!hasAskedNotificationPermission()) {
                setAskedNotificationPermission()
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
        }
        startActivity(intent)
    }

    // --- SharedPreferences helper ---
    private fun hasAskedNotificationPermission(): Boolean {
        val prefs = requireContext().getSharedPreferences("app_prefs", 0)
        return prefs.getBoolean("asked_notification_permission", false)
    }

    private fun setAskedNotificationPermission() {
        val prefs = requireContext().getSharedPreferences("app_prefs", 0)
        prefs.edit().putBoolean("asked_notification_permission", true).apply()
    }
}
