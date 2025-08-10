package com.dat.bookstore_app.presentation.features.login

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentLoginBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(getString(R.string.server_client_id), false)
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

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
                ds.color = ContextCompat.getColor(requireContext(), R.color.yellow_dark)
                ds.isUnderlineText = false
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
            loginViewModel.onLogin()
        }


        btnLoginGoogle.setOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }


    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collectLatest { state ->
                    if (state.isSuccess) {
                        mainViewModel.updateLoggedIn(true)
                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    }

                }
                loginViewModel.errorsState.errors.collectLatest { error ->
                    error?.let {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}