package com.dat.bookstore_app.presentation.features.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ActivityMainBinding
import com.dat.bookstore_app.presentation.common.base.BaseActivity
import com.dat.bookstore_app.utils.extension.setStatusBarColorCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    val viewModel: MainViewModel by viewModels()
    val sharedViewModel: MainSharedViewModel by viewModels()
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

        processIntent(intent)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.isLoggedIn) {
                        binding.progressOverlayWhite.root.visibility = View.VISIBLE
                        delay(500)
                        binding.progressOverlayWhite.root.visibility = View.GONE
                        // Nếu cần thì ở đây gọi navigate hoặc event khác
                    }
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    fun handleDeepLink(intent: Intent?) {
        if (viewModel.uiState.value.isLoggedIn) {
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

        if (!viewModel.uiState.value.isLoggedIn) {
            intent?.data?.let { uri ->
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_main) as NavHostFragment
                val navController = navHostFragment.navController

                if (uri.scheme == "myapp" && uri.host == "verify-return") {
                    val status = uri.getQueryParameter("status") ?: ""
                    val message =  uri.getQueryParameter("message") ?: ""
//                    sharedViewModel.switchTab("account")

//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.slide_in_right)
//                        .setExitAnim(R.anim.slide_out_left)
//                        .setPopEnterAnim(R.anim.slide_in_left)
//                        .setPopExitAnim(R.anim.slide_out_right)
//                        .build()

                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(
                            R.id.verifyStateFragment,
                            bundleOf("status" to status, "message" to message)
                        )
                    }, 500)
                }
            }

            intent?.data?.let { uri ->
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_main) as NavHostFragment
                val navController = navHostFragment.navController

                if (uri.scheme == "myapp" && uri.host == "forgot-return") {
                    val status = uri.getQueryParameter("status") ?: ""
                    val token =  uri.getQueryParameter("token") ?: ""
//                    sharedViewModel.switchTab("account")

//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.slide_in_right)
//                        .setExitAnim(R.anim.slide_out_left)
//                        .setPopEnterAnim(R.anim.slide_in_left)
//                        .setPopExitAnim(R.anim.slide_out_right)
//                        .build()

                    if (status == "success") {
                        Handler(Looper.getMainLooper()).postDelayed({
                            navController.navigate(
                                R.id.resetPasswordFragment,
                                bundleOf("token" to token)
                            )
                        }, 0)
                    }
                }
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
        intent?.data?.let { uri ->
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_main) as NavHostFragment
            val navController = navHostFragment.navController

            if (uri.scheme == "myapp" && uri.host == "forgot-return") {
                val status = uri.getQueryParameter("status") ?: ""
                val token =  uri.getQueryParameter("token") ?: ""
//                    sharedViewModel.switchTab("account")

//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.slide_in_right)
//                        .setExitAnim(R.anim.slide_out_left)
//                        .setPopEnterAnim(R.anim.slide_in_left)
//                        .setPopExitAnim(R.anim.slide_out_right)
//                        .build()

                if (status == "success") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(
                            R.id.resetPasswordFragment,
                            bundleOf("token" to token)
                        )
                    }, 0)
                }
            }
        }
    }
}
