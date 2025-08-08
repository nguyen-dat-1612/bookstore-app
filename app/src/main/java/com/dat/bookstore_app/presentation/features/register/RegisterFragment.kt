package com.dat.bookstore_app.presentation.features.register

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentRegisterBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val registerViewModel: RegisterViewModel by viewModels()
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        val watcher = {
            btnLogin.isEnabled = validateInputs()
        }

        nameText.doAfterTextChanged { watcher() }
        emailText.doAfterTextChanged { watcher() }
        passwordText.doAfterTextChanged { watcher() }
        tvConfirmPassword.doAfterTextChanged { watcher() }
        phoneText.doAfterTextChanged { watcher() }

        btnLogin.setOnClickListener {
            val name = nameText.text.toString().trim()
            val email = emailText.text.toString().trim()
            val phone = phoneText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            registerViewModel.register(
                fullName = name,
                email = email,
                phone = phone,
                password = password
            )
        }
        watcher()
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    registerViewModel.uiState.collectLatest {
                        if (it.isSuccess) {
                            showSuccessBanner("Đăng ký thành công, vui lòng kiểm tra email để xác nhận")
                        }
                    }
                }
                launch {
                    registerViewModel.loadingState.loading.collectLatest {
                        requireActivity().findViewById<View>(R.id.progressOverlay).visibility = if (it) View.VISIBLE else View.GONE
//                        binding.progressOverlay.root.visibility = if (it) View.VISIBLE else View.GONE
                    }
                }

            }
        }
    }

    private fun validateInputs(): Boolean = with(binding){
        val name = nameText.text.toString().trim()
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()
        val confirmPassword = tvConfirmPassword.text.toString().trim()
        val phone = phoneText.text.toString().trim()

        return name.isNotEmpty() &&
                email.isNotEmpty() &&
                phone.isNotEmpty() &&
                password.length >= 8 &&
                confirmPassword == password
    }
    private fun showSuccessBanner(message: String) = with(binding) {
        successBanner.text = message
        successBanner.visibility = View.VISIBLE

        // Animation trượt xuống
        successBanner.animate()
            .translationY(0f)
            .setDuration(300)
            .withEndAction {
                // Sau 3s, trượt lên và ẩn
                successBanner.postDelayed({
                    successBanner.animate()
                        .translationY(-successBanner.height.toFloat())
                        .setDuration(300)
                        .withEndAction {
                            successBanner.visibility = View.GONE
                        }
                        .start()
                }, 3000)
            }
            .start()
    }

    fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }


}