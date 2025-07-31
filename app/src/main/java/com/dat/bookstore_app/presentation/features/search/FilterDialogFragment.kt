package com.dat.bookstore_app.presentation.features.search

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.DialogFilterBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.CategoryUiModel
import com.dat.bookstore_app.presentation.common.adapter.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
@AndroidEntryPoint
class FilterDialogFragment : DialogFragment() {

    private val viewModel: SearchResultViewModel by activityViewModels()

    private var tempList = emptyList<CategoryUiModel>()

    private lateinit var adapter: CategoryAdapter

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!


    private val priceRangeFlow = MutableStateFlow<Pair<Int, Int>>(10000 to 200000)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RightSlideDialog)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.8).toInt(),
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setGravity(Gravity.END)
            setBackgroundDrawableResource(android.R.color.transparent)
            setWindowAnimations(R.style.RightSlideAnimation)

            // Set màu status bar (không deprecated)
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        adapter = CategoryAdapter(
            onCategorySelected = { selectedItem ->
                tempList = tempList.map {
                    if (it.id == selectedItem.id) it.copy(isSelected = !it.isSelected)
                    else it
                }
                adapter.submitList(tempList)

                viewModel.previewFilters(
                    allCategories = tempList,
                    minPrice = binding.rangeSlider.values[0].toInt(),
                    maxPrice = binding.rangeSlider.values[1].toInt()
                )
            }

        )

        btnBack.setOnClickListener {
            dismiss()
        }

        btnApplyFilter.setOnClickListener {
            viewModel.applyFilters()
            dismiss()
        }

        btnResetFilter.setOnClickListener {
            tempList = tempList.map { it.copy(isSelected = false) }
            adapter.submitList(tempList)
            viewModel.resetFilters()
        }

        binding.rangeSlider.valueFrom = 1000f
        binding.rangeSlider.valueTo = 500000f
        binding.rangeSlider.stepSize = 1000f
        val minPrice = viewModel.uiState.value.minPrice ?: 10000
        val maxPrice = viewModel.uiState.value.maxPrice ?: 200000
        rangeSlider.values = listOf(minPrice.toFloat(), maxPrice.toFloat())

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            val min = slider.values[0].toInt()
            val max = slider.values[1].toInt()

            val minStr = currencyFormatter.format(min)
            val maxStr = currencyFormatter.format(max)


            priceRangeFlow.value = min to max
        }

        lifecycleScope.launch {
            priceRangeFlow
                .debounce(300)
                .collectLatest { (min, max) ->
                    Toast.makeText(requireContext(), "$min - $max", Toast.LENGTH_SHORT).show()
                    viewModel.previewFilters(
                        allCategories = tempList,
                        minPrice = min,
                        maxPrice = max
                    )
                }
        }

        rvCategories.adapter = adapter
        observerData()
    }

    private fun observerData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest {
                        if (it.allCategories.isNotEmpty()) {
                            tempList = it.allCategories
                            adapter.submitList(tempList)
                        }
                    }
                }
                launch {
                    viewModel.previewTotal.collectLatest { total ->
                        binding.btnApplyFilter.text = "Kết quả: $total"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}