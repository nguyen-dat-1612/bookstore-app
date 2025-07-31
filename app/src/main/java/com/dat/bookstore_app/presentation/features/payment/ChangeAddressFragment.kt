package com.dat.bookstore_app.presentation.features.payment

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentChangeAddressBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R

@AndroidEntryPoint
class ChangeAddressFragment : BaseFragment<FragmentChangeAddressBinding>() {

    private val viewmodel: ChangeAddressViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangeAddressBinding {
        return FragmentChangeAddressBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnUpdate.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("new_address", Triple(fullName, phone, address))

            navController.popBackStack()

        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnUpdate.isEnabled =
                    etFullName.text?.toString()?.isNotBlank() == true &&
                            etPhone.text?.toString()?.isNotBlank() == true &&
                            etAddress.text?.toString()?.isNotBlank() == true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etFullName.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)
        etAddress.addTextChangedListener(textWatcher)

        viewmodel.loadAddressFromArgsIfAvailable()
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.uiState.collectLatest {
                    with(binding) {
                        etFullName.setText(it.fullName)
                        etAddress.setText(it.address)
                        etPhone.setText(it.phone)
                    }
                }
            }
        }
    }
}