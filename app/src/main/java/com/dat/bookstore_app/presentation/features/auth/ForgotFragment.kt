package com.dat.bookstore_app.presentation.features.auth

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentForgotBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.extension.showVerificationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ForgotFragment : BaseFragment<FragmentForgotBinding>() {

    private val viewModel: ForgotViewModel by viewModels()
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotBinding {
        return FragmentForgotBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        btnSendEmail.setOnClickListener {
            viewModel.onForgot(
                email = emailInput.text.toString()
            )
        }

        btnBack.setOnClickListener {
            navController.popBackStack()
        }

    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest {
                        if (it.isSuccess) {
                            requireContext().showVerificationDialog(
                                title = "Đã gửi email xác nhận đặt lại mật khẩu",
                                message = "Vui lòng kiểm tra hộp thư và làm theo hướng dẫn để hoàn tất việc đặt lại mật khẩu.",
                                iconRes = R.drawable.ic_check_circle_green
                            )
                            reset()
                        }
                        if (it.isLoading) {
                            requireActivity().findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                        } else {
                            requireActivity().findViewById<ProgressBar>(R.id.progressBar).postDelayed({
                                requireActivity().findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            }, 300)
                        }
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

    fun reset() = with(binding) {
        emailInput.text.clear()
        viewModel.resetViewModel()
    }

}