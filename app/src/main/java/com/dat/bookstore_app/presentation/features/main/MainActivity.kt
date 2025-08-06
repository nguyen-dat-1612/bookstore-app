package com.dat.bookstore_app.presentation.features.main

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ActivityMainBinding
import com.dat.bookstore_app.presentation.common.base.BaseActivity
import com.dat.bookstore_app.utils.extension.setStatusBarColorCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val viewModel: MainViewModel by viewModels()

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        setStatusBarColorCompat(R.color.primary, darkIcon = false)
        setupStatusBarControl()
    }

    override fun observeViewModel() {}

    private fun setupStatusBarControl() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_main) as NavHostFragment
        val navController = navHostFragment.navController
        // Không cần setupWithNavController ở đây
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun onResume() {
        super.onResume()
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "myapp" && uri.host == "payment-return") {
                val status = uri.getQueryParameter("status") ?: "unknown"
                val orderId = uri.getQueryParameter("orderId") ?: ""
                val amount = uri.getQueryParameter("amount") ?: "0"
                val transactionId = uri.getQueryParameter("transactionId") ?: ""

                val navController = findNavController(R.id.nav_host_main)

                val args = bundleOf(
                    "paymentStatus" to status,
                    "orderId" to orderId,
                    "amount" to amount,
                    "transactionId" to transactionId
                )
                Log.d(TAG, "handleDeepLink: $args")

                navController.currentBackStackEntry?.savedStateHandle?.set("payment_result", args)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}