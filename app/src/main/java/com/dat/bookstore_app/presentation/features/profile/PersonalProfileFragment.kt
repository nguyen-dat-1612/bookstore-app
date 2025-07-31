package com.dat.bookstore_app.presentation.features.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentPersonalProfileBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R

@AndroidEntryPoint
class PersonalProfileFragment : BaseFragment<FragmentPersonalProfileBinding>() {

    private val viewModel: PersonalProfileViewModel by viewModels<PersonalProfileViewModel>()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalProfileBinding {
        return FragmentPersonalProfileBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {

        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        setupTextWatchers()

        btnUpdateProfile.setOnClickListener {
            clearFocusAndHideKeyboard()
            validateAndSubmit()
        }

    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    if (it.user != null) {
                        binding.etFullName.setText(it.user.fullName)
                        binding.etAddress.setText(it.user.address)
                        binding.etPhone.setText(it.user.phone)
                    }
                }
            }
        }
    }

    private fun setupTextWatchers() = with(binding) {
        etFullName.addTextChangedListener {
            val value = it.toString().trim()
            fullNameError.visibility = if (value.isBlank()) {
                fullNameError.text = "Họ và tên không được để trống"
                View.VISIBLE
            } else {
                fullNameError.text = ""
                View.INVISIBLE
            }
        }

        etAddress.addTextChangedListener {
            val value = it.toString().trim()
            addressError.visibility = if (value.isBlank()) {
                addressError.text = "Địa chỉ không được để trống"
                View.VISIBLE
            } else {
                addressError.text = ""
                View.INVISIBLE
            }
        }

        etPhone.addTextChangedListener {
            val value = it.toString().trim()
            phoneError.visibility = if (!isValidVietnamPhoneNumber(value)) {
                phoneError.text = "Số điện thoại không hợp lệ"
                View.VISIBLE
            } else {
                phoneError.text = ""
                View.INVISIBLE
            }
        }
    }

    private fun validateAndSubmit() {
        val fullName = binding.etFullName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        var isValid = true

        // Validate họ và tên
        if (fullName.isBlank()) {
            binding.fullNameError.text = "Họ và tên không được để trống"
            binding.fullNameError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.fullNameError.text = ""
            binding.fullNameError.visibility = View.INVISIBLE
        }

        // Validate địa chỉ
        if (address.isBlank()) {
            binding.addressError.text = "Địa chỉ không được để trống"
            binding.addressError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.addressError.text = ""
            binding.addressError.visibility = View.INVISIBLE
        }

        // Validate số điện thoại Việt Nam
        if (!isValidVietnamPhoneNumber(phone)) {
            binding.phoneError.text = "Số điện thoại không hợp lệ"
            binding.phoneError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.phoneError.text = ""
            binding.phoneError.visibility = View.INVISIBLE
        }

        // Nếu hợp lệ, gọi ViewModel cập nhật
        if (isValid) {
            viewModel.updateAccount(fullName, address, phone)
        }
    }

    private fun isValidVietnamPhoneNumber(phone: String): Boolean {
        val regex = Regex("^(\\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$")
        return regex.matches(phone)
    }

    private fun clearFocusAndHideKeyboard() {
        // Clear focus tất cả các EditText
        binding.etFullName.clearFocus()
        binding.etAddress.clearFocus()
        binding.etPhone.clearFocus()

        // Ẩn bàn phím
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}