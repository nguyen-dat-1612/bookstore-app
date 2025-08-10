package com.dat.bookstore_app.presentation.features.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentVerifyStateBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.dat.bookstore_app.R

@AndroidEntryPoint
class VerifyStateFragment : BaseFragment<FragmentVerifyStateBinding>() {

    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    val options = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .setPopUpTo(R.id.verifyStateFragment, true)
        .setLaunchSingleTop(true)
        .build()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyStateBinding {
        val binding = FragmentVerifyStateBinding.inflate(inflater, container, false)
        return binding
    }

    override fun setUpView() {
        // Lấy dữ liệu từ arguments
        val status = arguments?.getString("status").orEmpty()
        val message = arguments?.getString("message").orEmpty()

        // Hiển thị dựa theo status
        if (status == "success") {
            binding.tvTitle.text = "Xác minh tài khoản thành công"
            binding.tvMessage.text = "Tài khoản của bạn đã được xác minh. Bạn có thể đăng nhập ngay bây giờ."
            binding.ivStatusIcon.setImageResource(R.drawable.ic_check_circle_green) // icon thành công
        } else {
            binding.tvTitle.text = "Xác minh thất bại"
            binding.tvMessage.text = if (message.isBlank()) "Đã xảy ra lỗi trong quá trình xác minh." else message
            binding.ivStatusIcon.setImageResource(R.drawable.ic_error_red) // icon lỗi
        }

        binding.btnHome.setOnClickListener {
            navigateTabBottomNav("home")
        }

        binding.btnLogin.setOnClickListener {
            navigateTabBottomNav("account")
        }
    }

    fun navigateTabBottomNav(tab: String) {
        sharedViewModel.switchTab(tab)
        navController.navigate(R.id.bottomNavFragment, null, options)
    }

    override fun observeViewModel() {

    }

}