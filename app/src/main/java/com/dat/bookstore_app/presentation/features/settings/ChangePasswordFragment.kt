package com.dat.bookstore_app.presentation.features.settings

import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentChangePasswordBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val viewModel:ChangePasswordViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        setupRealTimeValidation()
        setupPasswordToggle(currentPasswordInput, binding.currentPasswordToggle)
        setupPasswordToggle(newPasswordInput, binding.newPasswordToggle)
        setupPasswordToggle(reNewPasswordInput, binding.reNewPasswordToggle)

        changePasswordButton.setOnClickListener {
            val oldPass = currentPasswordInput.text.toString().trim()
            val newPass = newPasswordInput.text.toString().trim()
            val rePass = reNewPasswordInput.text.toString().trim()

            if (validatePasswords(oldPass, newPass, rePass)) {
                viewModel.changePassword(oldPass, newPass)
            }
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    if (it.isChangePasswordSuccess) {
                        Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    if (it.messageError != null) {
                        Toast.makeText(requireContext(), it.messageError, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setupPasswordToggle(editText: EditText, toggle: ImageView) {
        toggle.setOnClickListener {
            val isHidden = editText.transformationMethod is PasswordTransformationMethod
            if (isHidden) {
                editText.transformationMethod = null
                toggle.setImageResource(R.drawable.ic_eye_on)
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                toggle.setImageResource(R.drawable.ic_eye_off)
            }
            editText.setSelection(editText.text?.length ?: 0)
        }
    }
    private fun setupRealTimeValidation() = with(binding) {
        currentPasswordInput.doAfterTextChanged {
            if (it.toString().isNotBlank()) currentPasswordError.visibility = View.GONE
        }
        newPasswordInput.doAfterTextChanged {
            newPasswordError.visibility = View.GONE
        }
        reNewPasswordInput.doAfterTextChanged {
            reNewPasswordError.visibility = View.GONE
        }
    }


    private fun validatePasswords(
        oldPass: String,
        newPass: String,
        rePass: String
    ): Boolean = with(binding) {
        var valid = true

        currentPasswordError.visibility = View.GONE
        newPasswordError.visibility = View.GONE
        reNewPasswordError.visibility = View.GONE

        if (oldPass.isBlank()) {
            currentPasswordError.text = "Mật khẩu cũ không được bỏ trống"
            currentPasswordError.visibility = View.VISIBLE
            valid = false
        }

        if (newPass.isBlank()) {
            newPasswordError.text = "Mật khẩu mới không được bỏ trống"
            newPasswordError.visibility = View.VISIBLE
            valid = false
        } else if (newPass.length < 6) {
            newPasswordError.text = "Mật khẩu mới phải có ít nhất 6 ký tự"
            newPasswordError.visibility = View.VISIBLE
            valid = false
        } else if (newPass == oldPass) {
            newPasswordError.text = "Mật khẩu mới phải khác mật khẩu cũ"
            newPasswordError.visibility = View.VISIBLE
            valid = false
        }

        if (rePass.isBlank()) {
            reNewPasswordError.text = "Vui lòng nhập lại mật khẩu mới"
            reNewPasswordError.visibility = View.VISIBLE
            valid = false
        } else if (rePass != newPass) {
            reNewPasswordError.text = "Mật khẩu xác nhận không khớp"
            reNewPasswordError.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

}