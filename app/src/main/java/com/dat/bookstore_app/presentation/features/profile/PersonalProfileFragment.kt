package com.dat.bookstore_app.presentation.features.profile

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentPersonalProfileBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.extension.loadUrl
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class PersonalProfileFragment : BaseFragment<FragmentPersonalProfileBinding>() {

    private val viewModel: PersonalProfileViewModel by viewModels()

    // Lấy ảnh từ thư viện
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cropLauncher: ActivityResultLauncher<android.content.Intent>

    // Chụp ảnh
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private var tempImageUri: Uri? = null

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

        // Đăng ký mở thư viện ảnh
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { startCrop(it) }
        }

        // Đăng ký chụp ảnh
        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tempImageUri?.let { startCrop(it) }
            }
        }

        // Đăng ký nhận kết quả crop
        cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val resultUri = UCrop.getOutput(intent)
                    resultUri?.let {
                        binding.ivAvatar.load(it) {
                            crossfade(true)
                            placeholder(R.drawable.ic_placeholder_avatar) // nếu bạn có icon mặc định
                            error(R.drawable.ic_placeholder_avatar)
                        }
                        handleImagePicked(it)
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
            }
        }

        // Click avatar -> chọn ảnh
        editProfileImage.setOnClickListener {
            val options = arrayOf("Chụp ảnh", "Chọn từ thư viện")
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn ảnh đại diện")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> { // Chụp ảnh
                            val photoFile = File.createTempFile("temp_photo_", ".jpg", requireContext().cacheDir)
                            tempImageUri = androidx.core.content.FileProvider.getUriForFile(
                                requireContext(),
                                "${requireContext().packageName}.provider",
                                photoFile
                            )
                            takePhotoLauncher.launch(tempImageUri!!)
                        }
                        1 -> { // Thư viện
                            pickImageLauncher.launch("image/*")
                        }
                    }
                }
                .show()
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
                        if (it.user.avatar != null)  {
                            binding.ivAvatar.loadUrl(it.user.avatar)
                        } else binding.ivAvatar.load(R.drawable.ic_placeholder_avatar)
                    }
                }
                launch {
                    viewModel.loadingState.loading.collect { isLoading ->
                        if (isLoading) {
                            binding.progressOverlay.root.visibility = View.VISIBLE
                        }
//                        else {
//                            delay(2000) // giữ loading 2 giây trước khi ẩn
//                            binding.progressOverlay.root.visibility = View.GONE
//                            // cập nhật dữ liệu ở đây nếu cần
//                        }
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

        if (fullName.isBlank()) {
            binding.fullNameError.text = "Họ và tên không được để trống"
            binding.fullNameError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.fullNameError.text = ""
            binding.fullNameError.visibility = View.INVISIBLE
        }

        if (address.isBlank()) {
            binding.addressError.text = "Địa chỉ không được để trống"
            binding.addressError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.addressError.text = ""
            binding.addressError.visibility = View.INVISIBLE
        }

        if (!isValidVietnamPhoneNumber(phone)) {
            binding.phoneError.text = "Số điện thoại không hợp lệ"
            binding.phoneError.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.phoneError.text = ""
            binding.phoneError.visibility = View.INVISIBLE
        }

        if (isValid) {
            viewModel.updateAccount(fullName, address, phone)
        }
    }

    private fun isValidVietnamPhoneNumber(phone: String): Boolean {
        val regex = Regex("^(\\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$")
        return regex.matches(phone)
    }

    private fun clearFocusAndHideKeyboard() {
        binding.etFullName.clearFocus()
        binding.etAddress.clearFocus()
        binding.etPhone.clearFocus()


        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun startCrop(sourceUri: Uri?) {
        sourceUri ?: return // nếu null thì bỏ qua

        val destinationUri = Uri.fromFile(
            File(requireContext().cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
        )

        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(90)
            setFreeStyleCropEnabled(true)
        }

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val intent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(screenWidth, screenWidth)
            .withMaxResultSize(screenWidth.toInt(), screenWidth.toInt())
            .withOptions(options)
            .getIntent(requireContext())

        cropLauncher.launch(intent)
    }

    private fun handleImagePicked(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return

        // Lấy tên file
        val fileName = "avatar_${System.currentTimeMillis()}.jpg"

        // Tạo RequestBody cho file (chỉ nhận image/*)
        val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

        // MultipartBody.Part cho "file"
        val filePart = MultipartBody.Part.createFormData(
            "file",
            fileName,
            requestFile
        )

        // RequestBody cho "folder"
        val folderPart = "avatar".toRequestBody("text/plain".toMediaTypeOrNull())

        viewModel.uploadFile(filePart, folderPart)
    }
}
