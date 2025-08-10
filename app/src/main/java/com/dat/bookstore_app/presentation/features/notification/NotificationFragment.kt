package com.dat.bookstore_app.presentation.features.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentNotificationBinding
import com.dat.bookstore_app.presentation.common.adapter.NotificationAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.BottomNavFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {

//    private val viewModel: NotificationViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        NotificationAdapter(
            onItemClick = {
//                navController.navigate(
//                    BottomNavFragmentDirections.actionBottomNavFragmentToDetailOrderFragment(
//                        it.orderId
//                    )
//                )
            }
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        rvNotifications.adapter = adapter
    }

    override fun observeViewModel() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uiState.collectLatest {
////                    adapter.submitList(it.notifications)
//                }
//
//            }
//        }
    }

}