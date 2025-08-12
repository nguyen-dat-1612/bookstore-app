package com.dat.bookstore_app.presentation.features.address

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.DialogSelectProvinceBinding
import com.dat.bookstore_app.databinding.FragmentUpdateAddressBinding
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
class UpdateAddressFragment : BaseFragment<FragmentUpdateAddressBinding>() {

    private val viewModel: UpdateAddressViewModel by viewModels()
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateAddressBinding {
        return FragmentUpdateAddressBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {

        btnHouse.tvCategoryName.text = "Nhà riêng"
        btnOffice.tvCategoryName.text = "Văn phòng"

        // --- Set dữ liệu cơ bản ---
        viewModel.address?.let { address ->
            editTextTen.setText(address.fullName)
            editTextSoDienThoai.setText(address.phoneNumber)
            editTextDiaChiNhanHang.setText(address.addressDetail)
            checkBoxGiaoHangMacDinh.isChecked = address.isDefault

            if (address.addressType == AddressType.HOME) {
                btnHouse.imgTick.visibility = View.VISIBLE
            } else {
                btnOffice.imgTick.visibility = View.VISIBLE
            }
            // Load provinces ngay khi mở màn hình
            viewModel.loadProvinces()
        }

        // Chọn tỉnh/thành phố
        editTextTinhThanhPho.setOnClickListener {
            showProvinceDialog(viewModel.uiState.value.provinces)
        }

        // Chọn phường/xã
        editTextPhuongXa.setOnClickListener {
            if (viewModel.uiState.value.chooseProvince != null) {
                viewModel.loadCommunesByProvince(viewModel.uiState.value.chooseProvince!!.idProvince)
                showCommuneDialog(viewModel.uiState.value.communes)
            } else {
                showToast("Vui lòng chọn tỉnh/thành phố trước")
            }
        }

        binding.btnHouse.container.setOnClickListener {
            viewModel.updateAddressType(AddressType.HOME)
        }
        binding.btnOffice.container.setOnClickListener {
            viewModel.updateAddressType(AddressType.OFFICE)
        }

        // Nút cập nhật địa chỉ
        bottomNav.btnConfirm.setOnClickListener {
            viewModel.updateAddress(
                fullName = editTextTen.text.toString(),
                phoneNumber = editTextSoDienThoai.text.toString(),
                addressDetail = editTextDiaChiNhanHang.text.toString(),
                isDefault = checkBoxGiaoHangMacDinh.isChecked
            )
        }
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
        btnDeleteAddress.setOnClickListener {
            viewModel.deleteAddress()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Quan sát UI state
                launch {
                    viewModel.uiState.collectLatest { uiState ->

                        val address = viewModel.address

                        if (viewModel.uiState.value.addressType == AddressType.HOME) {
                            binding.btnHouse.imgTick.visibility = View.VISIBLE
                            binding.btnOffice.imgTick.visibility = View.GONE
                        } else {
                            binding.btnHouse.imgTick.visibility = View.GONE
                            binding.btnOffice.imgTick.visibility = View.VISIBLE
                        }
                        // Khi provinces đã load, chọn province khớp với address
                        if (uiState.provinces.isNotEmpty() && address?.province != null && uiState.chooseProvince == null) {
                            val province = uiState.provinces.find { it.name == address.province }
                            province?.let {
                                binding.editTextTinhThanhPho.setText(it.name)
                                viewModel.chooseProvince(it)
                                viewModel.loadCommunesByProvince(it.idProvince)
                            }
                        }

                        // Khi communes đã load, chọn commune khớp với address
                        if (uiState.communes.isNotEmpty() && address?.ward != null && uiState.chooseCommune == null) {
                            val commune = uiState.communes.find { it.name == address.ward }
                            commune?.let {
                                binding.editTextPhuongXa.setText(it.name)
                                viewModel.chooseCommune(it)
                            }
                        }

                        // Cập nhật loại địa chỉ nếu chưa set
                        if (uiState.addressType == null && address?.addressType != null) {
                            viewModel.updateAddressType(address.addressType)
                        }

                        // Khi update hoặc delete thành công
                        if (uiState.isUpdateSuccess || uiState.isDeleteSuccess) {
                            navController.popBackStack()
                        }
                    }
                }

                // Quan sát lỗi
                launch {
                    viewModel.errorsState.errors.collectLatest {
                        showToast(it.message.toString())
                    }
                }
            }
        }
    }

    private fun showProvinceDialog(provinces: List<Province>) {
        val dialogBinding = DialogSelectProvinceBinding.inflate(layoutInflater)
        dialogBinding.tvTitle.setText(R.string.hint_tinh_thanh_pho)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        val adapter = ProvinceAdapter(provinces) { selectedProvince ->
            viewModel.chooseProvince(selectedProvince)
            binding.editTextTinhThanhPho.setText(selectedProvince.name)
            alertDialog.dismiss()
        }

        dialogBinding.rvProvinces.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.rvProvinces.adapter = adapter

        dialogBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }

        dialogBinding.etSearchProvince.addTextChangedListener(object : TextWatcher {
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

        // Đặt nền trong suốt để bo góc nhìn rõ
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Tính 70% chiều rộng màn hình
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.8).toInt()

        // Set lại kích thước dialog
        alertDialog.window?.setLayout(
            dialogWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showCommuneDialog(communes: List<Commune>) {
        val dialogBinding = DialogSelectProvinceBinding.inflate(layoutInflater)
        dialogBinding.tvTitle.setText(R.string.hint_phuong_xa)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        val adapter = CommuneAdapter { selectedCommune ->
            binding.editTextPhuongXa.setText(selectedCommune.name)
            viewModel.chooseCommune(selectedCommune)
            alertDialog.dismiss()
        }

        dialogBinding.rvProvinces.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.rvProvinces.adapter = adapter
        adapter.submitList(communes)

        dialogBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }

        dialogBinding.etSearchProvince.addTextChangedListener(object : TextWatcher {
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


        // Đặt nền trong suốt để bo góc nhìn rõ
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Tính 70% chiều rộng màn hình
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.8).toInt()

        // Set lại kích thước dialog
        alertDialog.window?.setLayout(
            dialogWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
