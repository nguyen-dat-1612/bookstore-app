package com.dat.bookstore_app.presentation.features.book

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentBookInformationBinding
import com.dat.bookstore_app.databinding.ItemBookInfoRowBinding
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.presentation.common.base.BaseFragment

class BookInformationFragment : BaseFragment<FragmentBookInformationBinding>() {

    private val args by navArgs<BookInformationFragmentArgs>()
    private lateinit var book: Book
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookInformationBinding {
        return FragmentBookInformationBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        book = args.book
        setupUI()
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun observeViewModel() {

    }

    private fun setupUI() = with(binding) {
        tvDescription.text = book.description

        addInfoRow("Mã hàng", book.id.toString())
        addInfoRow("Nhà cung cấp", "Nhà Xuất Bản Kim Đồng")
        addInfoRow("Tác giả", book.author)
        addInfoRow("Người dịch", "Tecchan")
        addInfoRow("Nhà xuất bản", book.publisher)
        addInfoRow("Năm xuất bản", book.publicationDate)
        addInfoRow("Ngôn ngữ", book.language)
        addInfoRow("Trọng lượng", "100")
        addInfoRow("Kích thước", "26 x 18 x 0.2 cm")
        addInfoRow("Số trang", book.pageCount.toString())
        addInfoRow("Hình thức bìa", book.coverType)
    }

    private fun addInfoRow(label: String, value: String) {
        val rowBinding = ItemBookInfoRowBinding.inflate(layoutInflater, binding.layoutInfo, false)
        rowBinding.tvLabel.text = label
        rowBinding.tvValue.text = value
        binding.layoutInfo.addView(rowBinding.root)
    }
}
