package com.dat.bookstore_app.presentation.features.settings

import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentCreatePasswordBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CreatePasswordFragment : BaseFragment<FragmentCreatePasswordBinding>() {

    private val createPasswordViewModel: CreatePasswordViewModel by viewModels<CreatePasswordViewModel>()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePasswordBinding {
        return FragmentCreatePasswordBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
        btnCreatePassword.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()
            val confirmPassword = reNewPasswordInput.text.toString().trim()

            if (validatePasswords(newPassword, confirmPassword)) {
                createPasswordViewModel.createPassword(newPassword,confirmPassword)
            }
        }
        setupRealTimeValidation()
        setupPasswordToggle(newPasswordInput, binding.newPasswordToggle)
        setupPasswordToggle(reNewPasswordInput, binding.reNewPasswordToggle)

    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    createPasswordViewModel.uiState.collectLatest {
                        if (it.isCreatePasswordSuccess) {
                            navController.popBackStack()
                        }
                    }
                }
                launch {
                    createPasswordViewModel.errorsState.errors.collect {
                        if (it.message != null) {
                            showToast(it.message!!)
                        }
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
        newPasswordInput.doAfterTextChanged {
            newPasswordError.visibility = View.GONE
        }
        reNewPasswordInput.doAfterTextChanged {
            reNewPasswordError.visibility = View.GONE
        }
    }
    private fun validatePasswords(
        newPass: String,
        rePass: String
    ): Boolean = with(binding) {
        var valid = true

        newPasswordError.visibility = View.GONE
        reNewPasswordError.visibility = View.GONE

        if (newPass.isBlank()) {
            newPasswordError.text = "Mật khẩu mới không được bỏ trống"
            newPasswordError.visibility = View.VISIBLE
            valid = false
        } else if (newPass.length < 6) {
            newPasswordError.text = "Mật khẩu mới phải có ít nhất 6 ký tự"
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