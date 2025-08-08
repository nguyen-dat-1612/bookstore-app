package com.dat.bookstore_app.presentation.common.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ItemOrderHistoryBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.presentation.common.base.BasePagingAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.utils.extension.hide
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.extension.show
import com.dat.bookstore_app.utils.helpers.CurrencyUtils.formatVND

class OrderAdapter(
    private val onItemClicked: (Order) -> Unit,
    private val onCancelOrderClicked: (Order) -> Unit,
    private val onRetryPaymentClicked: (Order) -> Unit,
    private val onBuyAgainClicked: (Order) -> Unit
) : BasePagingAdapter<Order,ItemOrderHistoryBinding, OrderAdapter.OrderViewHolder> (
    ItemOrderHistoryBinding::inflate,
    OrderDiffCallback()
){
    inner class OrderViewHolder(
        binding: ItemOrderHistoryBinding
    ) : BaseViewHolder<Order, ItemOrderHistoryBinding>(binding) {
        override fun bind(item: Order) {
            with(binding) {
                orderId.text = "#${item.id}"
                orderStatus.text = item.status.title
                imageView.loadUrl(
                    item.orderItems[0].book.thumbnail
                )
                nameText.text = item.orderItems[0].book.title
                tvQuantity.text = "${item.orderItems.size} sản phẩm"
                totalPrice.text = formatVND(item.totalAmount)
                itemView.setOnClickListener {
                    onItemClicked(item)
                }
                renderButtons(item, binding)
                btnSeeDetail.setOnClickListener {
                    onItemClicked(item)
                }
                btnBuyAgain.setOnClickListener{
                    onBuyAgainClicked(item)
                }
            }
        }
    }

    private fun renderButtons(order: Order, binding: ItemOrderHistoryBinding) {
        with(binding) {
            btnRetryPayment.hide()
            btnCancelOrder.hide()
            btnBuyAgain.hide()
            btnSeeDetail.hide()
            footerContainer.show()

            when (order.status) {
                OrderStatus.PENDING -> {
                    if (order.paymentMethod != PaymentMethod.COD) {
                        btnRetryPayment.show()
                        btnRetryPayment.setOnClickListener {
                            onRetryPaymentClicked(order)
                        }
                    }

                    btnCancelOrder.show()
                    btnCancelOrder.setOnClickListener {
                        onCancelOrderClicked(order)
                    }
                }

                OrderStatus.DELIVERED -> {
                    btnBuyAgain.show()
                    btnBuyAgain.setOnClickListener {
                        onBuyAgainClicked(order)
                    }
                }
                OrderStatus.CANCELLED -> {
                    btnBuyAgain.show()
                    btnBuyAgain.setOnClickListener {
                        onBuyAgainClicked(order)
                    }

                    btnCancelOrder.show()
                    btnCancelOrder.setBackgroundResource(R.drawable.bg_button_disabled)
                    btnCancelOrder.backgroundTintList = null
                    btnCancelOrder.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_dark))
                    btnCancelOrder.isEnabled = false
                }

                OrderStatus.ALL, OrderStatus.SHIPPING, OrderStatus.CONFIRMED -> {
                    btnSeeDetail.show()
                }
            }
        }
    }


    class OrderDiffCallback: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Order, newItem: Order): Any? {
            if (oldItem.status != newItem.status) {
                return newItem.status
            }
            return super.getChangePayload(oldItem, newItem)
        }

    }

    override fun createViewHolder(binding: ItemOrderHistoryBinding): OrderViewHolder {
        return OrderViewHolder(binding)
    }

}