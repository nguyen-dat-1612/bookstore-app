package com.dat.bookstore_app.presentation.features.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentProfileBinding
import com.dat.bookstore_app.domain.models.User
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.extension.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.findNavController
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
            navController.navigate(BottomNavFragmentDirections.
                actionBottomNavFragmentToSettingFragment(viewModel.uiState.value.user?.id!!)
            )
        }
        btnOrderList.setOnClickListener {
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment())
        }
        ivPaymentPending.setOnClickListener{
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(OrderStatus.PENDING.name))
        }
        ivProcessing.setOnClickListener{
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(OrderStatus.CONFIRMED.name))
        }

        ivShipping.setOnClickListener{
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(OrderStatus.SHIPPING.name))
        }
        ivCompleted.setOnClickListener{
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToPurchaseHistoryFragment(OrderStatus.DELIVERED.name))
        }
        btnPersonalProfile.setOnClickListener {
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToPersonalProfileFragment())
        }
        btnFavorite.setOnClickListener {
            navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToFavoriteListFragment())
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.user != null) {
                        setUpProfile(state.user)
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
        if (user.avatar != null)  userAvatar.loadUrl(user.avatar!!)
    }
}