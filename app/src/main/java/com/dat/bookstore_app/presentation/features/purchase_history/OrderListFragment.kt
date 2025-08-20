package com.dat.bookstore_app.presentation.features.purchase_history

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.DialogConfirmDeleteBinding
import com.dat.bookstore_app.databinding.FragmentOrderListBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.presentation.common.adapter.CommonLoadStateAdapter
import com.dat.bookstore_app.presentation.common.adapter.OrderAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderListFragment : BaseFragment<FragmentOrderListBinding>() {

    private lateinit var orderStatus: OrderStatus

    private val viewModel: OrderListViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        OrderAdapter(
            onItemClicked = {
                navController.navigate(
                    PurchaseHistoryFragmentDirections.actionPurchaseHistoryFragmentToDetailOrderFragment(it.id)
                )
            },
            onCancelOrderClicked = {
                // Show dialog
                showCancelOrderConfirmDialog(it.id)
            },
            onRetryPaymentClicked = {
                navController.navigate(
                    PurchaseHistoryFragmentDirections.actionPurchaseHistoryFragmentToRetryPaymentFragment(it.id)
                )
            },
            onBuyAgainClicked = {
                val buyAgainList = it.orderItems.map { item ->
                    Cart(
                        id = item.book.id,
                        quantity = item.quantity,
                        createdAt = "",
                        updatedAt = "",
                        book = item.book,
                        isSelected = true
                    )
                }.toTypedArray()

                val action = PurchaseHistoryFragmentDirections
                    .actionPurchaseHistoryFragmentToPaymentFragment(cartList = buyAgainList)
                binding.root.findNavController().navigate(action)
            }
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusName = arguments?.getString(ARG_STATUS) ?: OrderStatus.ALL.name
        orderStatus = OrderStatus.valueOf(statusName)

        // Chỉ load dữ liệu lần đầu
        if (savedInstanceState == null) {
            viewModel.loadOrders(
                sort = Sort.NEW_DESC,
                filter = orderStatus
            )
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOrderListBinding.inflate(inflater, container, false)

    override fun setUpView() {
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() = with(binding) {
        rvOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvOrder.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommonLoadStateAdapter { adapter.retry() },
            footer = CommonLoadStateAdapter { adapter.retry() }
        )
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderPagingFlow.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    val isEmpty = loadStates.refresh is androidx.paging.LoadState.NotLoading &&
                            adapter.itemCount == 0
                    binding.layoutEmptyOrder.visibility =
                        if (isEmpty) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        binding.progressOverlay.root.visibility =
                            if (state.isLoading) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.errorsState.errors.collect {
                        showToast(it.message.toString())
                    }
                }

            }
        }
    }

    private fun showCancelOrderConfirmDialog(orderId: Long) {
        val binding = DialogConfirmDeleteBinding.inflate(layoutInflater)

        binding.tvMessage.text = "Bạn có chắc chắn hủy đơn hàng này không?"
        binding.btnCancel.text = "Giữ lại"
        binding.btnConfirm.text = "Hủy đơn"

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.cancelOrder(orderId)
            alertDialog.dismiss()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 80% chiều rộng màn hình
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.7).toInt()
        alertDialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        private const val ARG_STATUS = "order_status"

        fun newInstance(status: OrderStatus): OrderListFragment {
            return OrderListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATUS, status.name)
                }
            }
        }
    }
}
