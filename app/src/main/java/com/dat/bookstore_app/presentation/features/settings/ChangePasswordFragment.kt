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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val viewModel: ChangePasswordViewModel by viewModels()

    private val currentPasswordFlow = MutableStateFlow("")
    private val newPasswordFlow = MutableStateFlow("")
    private val reNewPasswordFlow = MutableStateFlow("")

    // Flags để biết user đã bắt đầu nhập
    private var isCurrentPasswordTouched = false
    private var isNewPasswordTouched = false
    private var isReNewPasswordTouched = false

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

        setupPasswordToggle(currentPasswordInput, currentPasswordToggle)
        setupPasswordToggle(newPasswordInput, newPasswordToggle)
        setupPasswordToggle(reNewPasswordInput, reNewPasswordToggle)

        setupRealTimeValidation()

        bottomNav.btnConfirm.setOnClickListener {
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
                launch {
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
                launch {
                    viewModel.errorsState.errors.collect {
                        showToast(it.message.toString())
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
            if (!isCurrentPasswordTouched && !it.isNullOrEmpty()) isCurrentPasswordTouched = true
            currentPasswordFlow.value = it?.toString() ?: ""
        }
        newPasswordInput.doAfterTextChanged {
            if (!isNewPasswordTouched && !it.isNullOrEmpty()) isNewPasswordTouched = true
            newPasswordFlow.value = it?.toString() ?: ""
        }
        reNewPasswordInput.doAfterTextChanged {
            if (!isReNewPasswordTouched && !it.isNullOrEmpty()) isReNewPasswordTouched = true
            reNewPasswordFlow.value = it?.toString() ?: ""
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    currentPasswordFlow
                        .debounce(300)
                        .collectLatest { oldPass ->
                            if (isCurrentPasswordTouched) {
                                if (oldPass.isBlank()) {
                                    currentPasswordError.text = "Mật khẩu cũ không được bỏ trống"
                                    currentPasswordError.visibility = View.VISIBLE
                                } else {
                                    currentPasswordError.visibility = View.GONE
                                }

                                val newPass = newPasswordFlow.value
                                if (newPass.isNotBlank() && newPass == oldPass) {
                                    newPasswordError.text = "Mật khẩu mới phải khác mật khẩu cũ"
                                    newPasswordError.visibility = View.VISIBLE
                                } else if (newPass.isNotBlank() && newPass.length >= 6) {
                                    newPasswordError.visibility = View.GONE
                                }
                            }
                        }
                }

                launch {
                    newPasswordFlow
                        .debounce(300)
                        .collectLatest { newPass ->
                            if (isNewPasswordTouched) {
                                when {
                                    newPass.isBlank() -> {
                                        newPasswordError.text = "Mật khẩu mới không được bỏ trống"
                                        newPasswordError.visibility = View.VISIBLE
                                    }
                                    newPass.length < 6 -> {
                                        newPasswordError.text = "Mật khẩu mới phải có ít nhất 6 ký tự"
                                        newPasswordError.visibility = View.VISIBLE
                                    }
                                    else -> {
                                        val oldPass = currentPasswordFlow.value
                                        if (newPass == oldPass) {
                                            newPasswordError.text = "Mật khẩu mới phải khác mật khẩu cũ"
                                            newPasswordError.visibility = View.VISIBLE
                                        } else {
                                            newPasswordError.visibility = View.GONE
                                        }
                                    }
                                }

                                val rePass = reNewPasswordFlow.value
                                if (rePass.isNotBlank() && rePass != newPass) {
                                    reNewPasswordError.text = "Mật khẩu xác nhận không khớp"
                                    reNewPasswordError.visibility = View.VISIBLE
                                } else if (rePass.isNotBlank()) {
                                    reNewPasswordError.visibility = View.GONE
                                }
                            }
                        }
                }

                launch {
                    reNewPasswordFlow
                        .debounce(300)
                        .collectLatest { rePass ->
                            if (isReNewPasswordTouched) {
                                if (rePass.isBlank()) {
                                    reNewPasswordError.text = "Vui lòng nhập lại mật khẩu mới"
                                    reNewPasswordError.visibility = View.VISIBLE
                                } else {
                                    val newPass = newPasswordFlow.value
                                    if (rePass != newPass) {
                                        reNewPasswordError.text = "Mật khẩu xác nhận không khớp"
                                        reNewPasswordError.visibility = View.VISIBLE
                                    } else {
                                        reNewPasswordError.visibility = View.GONE
                                    }
                                }
                            }
                        }
                }
            }
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
