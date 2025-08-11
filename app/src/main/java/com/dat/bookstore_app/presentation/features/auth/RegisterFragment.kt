package com.dat.bookstore_app.presentation.features.auth

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.dat.bookstore_app.utils.extension.textChangesFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val registerViewModel: RegisterViewModel by viewModels()

    // Lưu trạng thái "đã tương tác" của từng input
    private val touchedMap = mutableMapOf(
        "name" to false,
        "email" to false,
        "phone" to false,
        "password" to false,
        "confirmPassword" to false
    )

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        val normalText = getString(R.string.sign_in_question) + " "
        val clickableText = getString(R.string.sign_in_action)
        val spannableString = SpannableString(normalText + clickableText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(context, "Đi đến màn Đăng nhập", Toast.LENGTH_SHORT).show()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                ds.color = ContextCompat.getColor(requireContext(), R.color.yellow_dark)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, normalText.length, normalText.length + clickableText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signUpText.text = spannableString
        signUpText.movementMethod = LinkMovementMethod.getInstance()

        // Hàm cập nhật lỗi và hiển thị
        // Chỉ hiện lỗi nếu trường đó đã được tương tác (touched = true)
        fun updateError(
            fieldName: String,
            editTextValue: String,
            errorView: View,
            errorMsg: String?
        ): Boolean {
            return if (errorMsg != null && touchedMap[fieldName] == true) {
                (errorView as? android.widget.TextView)?.apply {
                    text = errorMsg
                    visibility = View.VISIBLE
                }
                false
            } else {
                errorView.visibility = View.GONE
                true
            }
        }

        fun validateAll(): Boolean {
            val name = nameText.text.toString().trim()
            val email = emailText.text.toString().trim()
            val phone = phoneText.text.toString().trim()
            val password = edtPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            var valid = true

            valid = updateError("name", name, nameError, if (name.isEmpty()) "Tên không được để trống" else null) && valid

            valid = updateError("email", email, emailError,
                when {
                    email.isEmpty() -> "Email không được để trống"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
                    else -> null
                }
            ) && valid

            valid = updateError("phone", phone, phoneError, if (phone.isEmpty()) "Số điện thoại không được để trống" else null) && valid

            valid = updateError("password", password, passwordError,
                if (password.length < 8) "Mật khẩu phải từ 8 ký tự" else null
            ) && valid

            valid = updateError("confirmPassword", confirmPassword, confirmPasswordError,
                if (confirmPassword != password) "Mật khẩu xác nhận không khớp" else null
            ) && valid

            return valid
        }

        fun watcher() {
            val valid = validateAll()
            btnLogin.isEnabled = valid
            val bgRes = if (valid) R.drawable.bg_button_payment else R.drawable.bg_button_circle_disabled
            btnLogin.background = ContextCompat.getDrawable(requireContext(), bgRes)
        }

        // Thiết lập listener để đánh dấu touched khi user tương tác
        fun setTouchedListener(editText: EditText, fieldName: String) {
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // blur mới đánh dấu touched
                    touchedMap[fieldName] = true
                    watcher() // validate khi rời input
                }
            }
        }

        setTouchedListener(nameText, "name")
        setTouchedListener(emailText, "email")
        setTouchedListener(phoneText, "phone")
        setTouchedListener(edtPassword, "password")
        setTouchedListener(edtConfirmPassword, "confirmPassword")

        // Gộp textChangesFlow để validate realtime (có debounce)
        viewLifecycleOwner.lifecycleScope.launch {
            merge(
                nameText.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged(),
                emailText.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged(),
                phoneText.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged(),
                edtPassword.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged(),
                edtConfirmPassword.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged()
            )
                .onEach {
                    watcher()
                }
                .launchIn(this)
        }

        btnLogin.setOnClickListener {
            // Bật tất cả touched để show lỗi nếu có
            touchedMap.keys.forEach { touchedMap[it] = true }
            if (validateAll()) {
                registerViewModel.register(
                    fullName = nameText.text.toString().trim(),
                    email = emailText.text.toString().trim(),
                    phone = phoneText.text.toString().trim(),
                    password = edtPassword.text.toString().trim()
                )
            } else {
                watcher() // cập nhật UI lỗi nếu không pass
            }
        }

        setupPasswordToggle(edtPassword, currentPasswordToggle)
        setupPasswordToggle(edtConfirmPassword, confirmPasswordToggle)

        watcher() // disable nút ban đầu
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    registerViewModel.uiState.collectLatest {
                        if (it.isSuccess) {
                            showSuccessBanner(true, "Đăng ký thành công, vui lòng kiểm tra email để xác nhận")
                            resetInputs()
                            // Reset touched khi reset form
                            touchedMap.keys.forEach { touchedMap[it] = false }
                        }
                    }
                }
                launch {
                    registerViewModel.loadingState.loading.collectLatest {
                        requireActivity().findViewById<View>(R.id.progressOverlay).visibility = if (it) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    registerViewModel.errorsState.errors.collectLatest {
                        showSuccessBanner(false, it.message.toString())
                    }
                }
            }
        }
    }

    private fun resetInputs() = with(binding) {
        nameText.text?.clear()
        emailText.text?.clear()
        phoneText.text?.clear()
        edtPassword.text?.clear()
        edtConfirmPassword.text?.clear()
        btnLogin.isEnabled = false
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
    private fun showSuccessBanner(status: Boolean, message: String) = with(binding) {
        successBanner.text = message
        successBanner.visibility = View.VISIBLE
        if (status) {
            successBanner.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.success_green)
            )
        } else {
            successBanner.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.error_red)
            )
        }
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
}
