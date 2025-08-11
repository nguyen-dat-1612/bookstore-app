package com.dat.bookstore_app.presentation.features.address

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.DialogSelectProvinceBinding
import com.dat.bookstore_app.databinding.FragmentAddAddressBinding
import com.dat.bookstore_app.domain.enums.AddressType
import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province
import com.dat.bookstore_app.presentation.common.adapter.AddressTypeAdapter
import com.dat.bookstore_app.presentation.common.adapter.CommuneAdapter
import com.dat.bookstore_app.presentation.common.adapter.ProvinceAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressFragment : BaseFragment<FragmentAddAddressBinding>() {

    private val viewModel: AddAddressViewModel by viewModels()
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddAddressBinding {
        return FragmentAddAddressBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding){
        editTextTinhThanhPho.setOnClickListener {
            viewModel.loadProvinces()
            showProvinceDialog(viewModel.uiState.value.provinces)
        }

        editTextPhuongXa.setOnClickListener {
            // Load danh sách phường từ ViewModel
            if (viewModel.uiState.value.chooseProvince != null) {
                viewModel.loadCommunesByProvince(viewModel.uiState.value.chooseProvince!!.idProvince)
                showCommuneDialog(viewModel.uiState.value.communes)
            }
        }
        val addressTypeAdapter = AddressTypeAdapter(AddressType.values().toList()) { selectedType ->
            viewModel.updateAddressType(selectedType)
        }
        rvAddressTypes.adapter = addressTypeAdapter
        btnAddAddress.setOnClickListener {
            viewModel.addAddress(
                fullName = editTextTen.text.toString(),
                phoneNumber = editTextSoDienThoai.text.toString(),
                addressDetail = editTextDiaChiNhanHang.text.toString(),
                isDefault = checkBoxGiaoHangMacDinh.isChecked
            )
        }
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { uiState ->
                        if (uiState.isSuccess) {
                            navController.popBackStack()
                        }
                    }
                }
                launch {
                    viewModel.errorsState.errors.collectLatest {
                        showToast(it.message.toString())
                    }
                }
            }
        }
    }

    private fun showProvinceDialog(provinces: List<Province>) {
        val binding = DialogSelectProvinceBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        val adapter = ProvinceAdapter(provinces) { selectedProvince ->
            viewModel.chooseProvince(selectedProvince)
            this.binding.editTextTinhThanhPho.setText(selectedProvince.name)
            alertDialog.dismiss()
        }

        binding.rvProvinces.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProvinces.adapter = adapter

        // Nút hủy
        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        // Lọc danh sách khi nhập
        binding.etSearchProvince.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = provinces.filter {
                    it.name.contains(s.toString(), ignoreCase = true)
                }
                adapter.submitList(filtered)
            }
        })

        alertDialog.show()
    }

    private fun showCommuneDialog(communes: List<Commune>) {
        val binding = DialogSelectProvinceBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        val adapter = CommuneAdapter { selectedCommune ->
            this.binding.editTextPhuongXa.setText(selectedCommune.name)
            viewModel.chooseCommune(selectedCommune)
            alertDialog.dismiss()
        }

        binding.rvProvinces.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProvinces.adapter = adapter
        adapter.submitList(communes)

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        binding.etSearchProvince.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = communes.filter {
                    it.name.contains(s.toString(), ignoreCase = true)
                }
                adapter.submitList(filtered)
            }
        })

        alertDialog.show()
    }


}
