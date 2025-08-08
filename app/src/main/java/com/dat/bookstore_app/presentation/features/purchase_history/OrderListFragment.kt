package com.dat.bookstore_app.presentation.features.purchase_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dat.bookstore_app.databinding.FragmentOrderListBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.adapter.CommonLoadStateAdapter
import com.dat.bookstore_app.presentation.common.adapter.OrderAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R
import com.dat.bookstore_app.domain.models.Cart

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
                viewModel.cancelOrder(it.id)
            },
            onRetryPaymentClicked = {
                navController.navigate(
                    PurchaseHistoryFragmentDirections.actionPurchaseHistoryFragmentToRetryPaymentFragment(it.id)
                )
            },
            onBuyAgainClicked = {
                val buyAgainList = it.orderItems.map {
                    Cart(
                        id = it.book.id,
                        quantity = it.quantity,
                        createdAt = "",
                        updatedAt = "",
                        book = it.book,
                        isSelected = true // hoặc false tùy ý
                    )
                }.toTypedArray()

                val action = PurchaseHistoryFragmentDirections
                    .actionPurchaseHistoryFragmentToPaymentFragment( cartList = buyAgainList)
                binding.root.findNavController().navigate(action)
            }
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOrderListBinding {
        val statusName = arguments?.getString(ARG_STATUS) ?: OrderStatus.ALL.name
        orderStatus = OrderStatus.valueOf(statusName)
        return FragmentOrderListBinding.inflate(inflater, container, false)
    }

    override fun setUpView()  {
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() = with(binding){
        rvOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvOrder.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommonLoadStateAdapter { adapter.retry() },
            footer = CommonLoadStateAdapter { adapter.retry() }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadOrders(
            sort = Sort.NEW_DESC,
            filter = orderStatus
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

        // Quan sát trạng thái load để kiểm tra rỗng
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    val isEmpty = loadStates.refresh is androidx.paging.LoadState.NotLoading &&
                            adapter.itemCount == 0
                    binding.layoutEmptyOrder.visibility = if (isEmpty) View.VISIBLE else View.GONE
                }
            }
        }
    }


    companion object {
        private const val ARG_STATUS = "order_status"

        fun newInstance(status: OrderStatus): OrderListFragment {
            val fragment = OrderListFragment()
            val bundle = Bundle()
            bundle.putString(ARG_STATUS, status.name)
            fragment.arguments = bundle
            return fragment
        }
    }

}