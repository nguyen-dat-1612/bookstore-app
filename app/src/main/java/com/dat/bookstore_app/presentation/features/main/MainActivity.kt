package com.dat.bookstore_app.presentation.features.main

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ActivityMainBinding
import com.dat.bookstore_app.presentation.common.base.BaseActivity
import com.dat.bookstore_app.utils.extension.setStatusBarColorCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setUpView() {
        viewModel.load()
        setStatusBarColorCompat(R.color.primary, darkIcon = false)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_main) as NavHostFragment
        navController = navHostFragment.navController

        if (viewModel.uiState.value.isLoggedIn) {
            processIntent(intent)
        } else {
            Log.d("Notifications", "Not logged in")
        }
    }

    override fun observeViewModel() {}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (viewModel.uiState.value.isLoggedIn) {
            handleDeepLink(intent)
        }
    }

    fun handleDeepLink(intent: Intent?) {
        val orderId = intent?.extras?.getString("order_id")
        if (!orderId.isNullOrEmpty()) {
            Log.d("Notifications handleDeepLink", "id: $orderId")
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()

            Handler(Looper.getMainLooper()).postDelayed({
                navController.navigate(
                    R.id.detailOrderFragment,
                    bundleOf("orderId" to orderId.toLong()),
                    navOptions
                )
            }, 500)
        }

        intent?.data?.let { uri ->
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_main) as NavHostFragment
            val navController = navHostFragment.navController

            if (uri.scheme == "myapp" && uri.host == "payment-return") {
                val transactionId = uri.getQueryParameter("transactionId") ?: ""
                navController.currentBackStackEntry?.savedStateHandle
                    ?.set("deep_link_result", bundleOf("transactionId" to transactionId))
                return
            }
        }
    }

    private fun processIntent(intent: Intent?) {
        if (intent == null || intent.extras == null) return

        val id = intent?.extras?.getString("order_id")

        Log.d("Notifications processIntent", "id: $id")
        if (id != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                navController.navigate(
                    BottomNavFragmentDirections
                        .actionBottomNavFragmentToDetailOrderFragment(id.toLong())
                )
            }, 500) // 2 giây
        }
    }
}
