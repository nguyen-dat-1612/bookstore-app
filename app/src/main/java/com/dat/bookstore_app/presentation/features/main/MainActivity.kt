package com.dat.bookstore_app.presentation.features.main

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
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
}