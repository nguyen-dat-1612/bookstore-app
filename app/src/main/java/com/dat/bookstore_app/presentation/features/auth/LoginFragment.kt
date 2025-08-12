package com.dat.bookstore_app.presentation.features.auth

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentLoginBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.MainViewModel
import com.dat.bookstore_app.utils.extension.textChangesFlow
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class LoginFragment: BaseFragment<FragmentLoginBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val authCode = account?.serverAuthCode
            if (authCode != null) {
                loginViewModel.onLoginGoogle(authCode)
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.statusCode} - ${GoogleSignInStatusCodes.getStatusCodeString(e.statusCode)}", e)
        }
    }

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        // watcher tách riêng
        fun watcher() {
            val valid = validateInputs()
            btnLogin.isEnabled = valid
            val bgRes = if (valid) R.drawable.bg_button_payment else R.drawable.bg_button_circle_disabled
            btnLogin.background = ContextCompat.getDrawable(requireContext(), bgRes)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(getString(R.string.server_client_id), false)
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // gom chung observe input
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                txEmail.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged()
                    .onEach { text ->
                        if (text.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            emailError.text = "Email không hợp lệ"
                            emailError.visibility = View.VISIBLE
                        } else {
                            emailError.text = ""
                            emailError.visibility = View.GONE
                        }
                        watcher()
                    }
                    .launchIn(this)
            }
            launch {
                txPassword.textChangesFlow()
                    .debounce(300)
                    .distinctUntilChanged()
                    .onEach {
                        watcher()
                    }
                    .launchIn(this)
            }
        }

        setUpUI()
        onListener()
    }

    private fun setUpUI() = with(binding) {
        val textView = signUpText
        val normalText = getString(R.string.sign_up_question) + " "
        val clickableText = getString(R.string.sign_up_action)

        val spannableString = SpannableString(normalText + clickableText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(context, "Đi đến màn Đăng ký", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // chữ đậm
                ds.color = ContextCompat.getColor(requireContext(), R.color.yellow_dark) // màu chữ
                ds.isUnderlineText = false // bỏ gạch dưới
            }
        }

        spannableString.setSpan(
            clickableSpan,
            normalText.length,
            normalText.length + clickableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()


        txEmail.addTextChangedListener {
            loginViewModel.onEmailChange(it.toString())
        }
        txPassword.addTextChangedListener {
            loginViewModel.onPasswordChange(it.toString())
        }
    }


    private fun onListener() = with(binding) {
        btnLogin.setOnClickListener {
            if (validateInputs()) {
                loginViewModel.onLogin()
            }
        }

        btnLoginGoogle.setOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
        forgotPasswordText.setOnClickListener {
            navController.navigate(R.id.action_bottomNavFragment_to_forgotFragment)
        }
        btnSendEmailVerify.setOnClickListener {
            loginViewModel.resendVerify()
        }
        setupPasswordToggle(txPassword, currentPasswordToggle)
    }


    override fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginViewModel.uiState.collectLatest { state ->
                        if (state.isSuccess ) {
                            mainViewModel.updateLoggedIn(true)
                            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        }
                        if (state.isVerify) {
                            binding.btnSendEmailVerify.visibility = View.VISIBLE
                            showSuccessBanner(true, "Tài khoản chưa được xác minh, vui lòng gửi yêu cầu xác minh tài khoản")
                        } else {
                            binding.btnSendEmailVerify.visibility = View.GONE
                        }
                        if (state.isResendVerify) {
                            showSuccessBanner(true, "Gửi yêu cầu xác minh thành công")
                        }
                    }
                }
                launch {
                    loginViewModel.errorsState.errors.collectLatest { error ->
                        error?.let {
                            if (error is HttpException) {
                                if (error.code() == 401) {
                                    // xử lý 401
                                } else if (error.code() == 400) {
                                    showSuccessBanner(
                                        false,
                                        "Tài khoản hoặc mật khẩu không chính xác!"
                                    )
                                }
                            } else {
                                showSuccessBanner(
                                    false,
                                    error.message.toString()
                                )
                            }
                        }
                    }
                }
            }
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

    private fun validateInputs(): Boolean = with(binding) {
        val email = txEmail.text.toString().trim()
        val password = txPassword.text.toString().trim()
        return email.isNotEmpty() &&
                password.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}