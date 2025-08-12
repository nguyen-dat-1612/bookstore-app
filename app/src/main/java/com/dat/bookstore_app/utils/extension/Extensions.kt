package com.dat.bookstore_app.utils.extension

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.SearchView
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Size
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.DialogVerificationBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.models.ErrorResponse
import com.dat.bookstore_app.network.ApiResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Response
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.features.purchase_history.OrderStepUI
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun SearchView.queryTextChanges(): Flow<String> = callbackFlow {
    val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true
        override fun onQueryTextChange(newText: String?): Boolean {
            trySend(newText.orEmpty())
            return true
        }
    }

    setOnQueryTextListener(listener)
    awaitClose { setOnQueryTextListener(null) }
}

suspend fun <T> apiCallResponse(apiCall: suspend () -> ApiResponse<T>) : Result<T> {
    return try {
        val response = apiCall()
        @Suppress("UNCHECKED_CAST")
        val data = response.data ?: Unit as T
        Result.Success(data)
    } catch (e: HttpException) {
        val code = e.code()
        val errorBodyString = e.response()?.errorBody()?.string()
        Log.e("apiCallResponse", "HTTP $code: $errorBodyString")

        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ErrorResponse::class.java)

        val errorResponse = errorBodyString?.let { adapter.fromJson(it) }
        val message = errorResponse?.message ?: "Có lỗi xảy ra"

        Result.Error(
            code = code,
            message = message,
            throwable = IllegalArgumentException(message)
        )
    } catch (e: IOException ) {
        Log.e("apiCallResponse", "IO: $e")
        Result.Error(message = e.message ?: "Unknown error", throwable = e)
    } catch (e: CancellationException) {
        Log.e("apiCallResponse", "Cancellation: $e")
        throw e
    } catch (e: Exception) {
        Log.e("apiCallResponse", "Unknown: $e")
        Result.Error(message = e.message ?: "Unknown error", throwable = e)
    }
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(code, message, throwable)
}

fun ImageView.loadUrl(url: String) {
    this.load(url) {
        crossfade(true)
        placeholder(R.drawable.placeholder)   // ảnh hiển thị tạm khi loading
        error(R.drawable.placeholder)
    }
}

fun TextView.setDiscountedPrice(originalPrice: Int, discountPercent: Int) {
    val discountedPrice = originalPrice * (100 - discountPercent) / 100

    val nf = NumberFormat.getInstance(Locale("vi", "VN"))
    val original = nf.format(originalPrice) + " ₫"
    val discounted = nf.format(discountedPrice) + " ₫  "

    val spannable = SpannableString(discounted + original)

    val start = discounted.length
    val end = discounted.length + original.length

    // Gạch ngang
    spannable.setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    // Màu xám
    spannable.setSpan(ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    // Thu nhỏ kích thước (70%)
    spannable.setSpan(RelativeSizeSpan(0.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.text = spannable
}
fun TextView.setPriceWithStrikethroughFirst(originalPrice: Int, discountPercent: Int) {
    val discountedPrice = originalPrice * (100 - discountPercent) / 100

    val nf = NumberFormat.getInstance(Locale("vi", "VN"))
    val original = nf.format(originalPrice) + " ₫"
    val discounted = nf.format(discountedPrice) + " ₫"

    val fullText = "$original  $discounted"
    val spannable = SpannableString(fullText)

    // Vị trí giá gốc (gạch, xám, nhỏ)
    val startOriginal = 0
    val endOriginal = original.length

    spannable.setSpan(StrikethroughSpan(), startOriginal, endOriginal, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannable.setSpan(ForegroundColorSpan(Color.parseColor("#A0A0A0")), startOriginal, endOriginal, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // xám nhạt
    spannable.setSpan(RelativeSizeSpan(0.7f), startOriginal, endOriginal, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    // Vị trí giá sau giảm (xanh)
    val startDiscounted = endOriginal + 2 // +2 vì có "  "
    val endDiscounted = fullText.length

    spannable.setSpan(ForegroundColorSpan(Color.parseColor("#2196F3")), startDiscounted, endDiscounted, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // màu xanh

    this.text = spannable
}


fun TextView.setDiscountedPricePayment(originalPrice: Int, discountPercent: Int) {
    val discountedPrice = originalPrice * (100 - discountPercent) / 100

    val nf = NumberFormat.getInstance(Locale("vi", "VN"))
    val original = nf.format(originalPrice) + " ₫"
    val discounted = nf.format(discountedPrice) + " ₫  "

    val spannable = SpannableString(discounted + original)

    // Màu vàng cho giá sau giảm
    spannable.setSpan(
        ForegroundColorSpan(Color.parseColor("#FFD700")), // hoặc Color.YELLOW nếu đủ sáng
        0,
        discounted.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val start = discounted.length
    val end = discounted.length + original.length

    // Gạch ngang cho giá gốc
    spannable.setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    // Màu xám cho giá gốc
    spannable.setSpan(ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    // Thu nhỏ giá gốc
    spannable.setSpan(RelativeSizeSpan(0.7f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.text = spannable
}



fun AppCompatActivity.setStatusBarColorCompat(@ColorRes colorRes: Int, darkIcon: Boolean) {
    val color = ContextCompat.getColor(this, colorRes)

    // ✅ Đổi màu status bar (áp dụng mọi SDK)
    window.statusBarColor = color

    // ✅ Điều chỉnh icon sáng/tối hiện đại (không deprecated)
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller?.isAppearanceLightStatusBars = darkIcon

    // ✅ Cho SDK 30+ nếu có sử dụng gesture navigation
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }
}

fun EditText.isNotBlank(): Boolean = this.text.toString().trim().isNotEmpty()

fun mapSteps(
    currentStatus: OrderStatus,
    createdAt: String,
    updatedAt: String
): List<OrderStepUI> {
    val isCanceled = currentStatus == OrderStatus.CANCELLED
    val steps = mutableListOf<OrderStepUI>()

    // Đơn hàng mới (chỉ dùng createdAt)
    steps.add(
        OrderStepUI(
            status = OrderStatus.ALL,
            isCompleted = currentStatus != OrderStatus.ALL,
            isCurrent = currentStatus == OrderStatus.ALL,
            isCanceled = false,
            createdAt = createdAt
        )
    )

    // Đang xử lý
    steps.add(
        OrderStepUI(
            status = OrderStatus.CONFIRMED,
            isCompleted = currentStatus in listOf(OrderStatus.CONFIRMED, OrderStatus.SHIPPING, OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            isCurrent = currentStatus in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED),
            isCanceled = false,
            updatedAt = if (currentStatus in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)) updatedAt else null
        )
    )

    if (isCanceled) {
        steps.add(
            OrderStepUI(
                status = OrderStatus.CANCELLED,
                isCompleted = false,
                isCurrent = true,
                isCanceled = true,
                updatedAt = updatedAt
            )
        )
    } else {
        steps.add(
            OrderStepUI(
                status = OrderStatus.SHIPPING,
                isCompleted = currentStatus in listOf(OrderStatus.SHIPPING, OrderStatus.DELIVERED),
                isCurrent = currentStatus == OrderStatus.SHIPPING,
                isCanceled = false,
                updatedAt = if (currentStatus == OrderStatus.SHIPPING) updatedAt else null
            )
        )

        steps.add(
            OrderStepUI(
                status = OrderStatus.DELIVERED,
                isCompleted = currentStatus == OrderStatus.DELIVERED,
                isCurrent = currentStatus == OrderStatus.DELIVERED,
                isCanceled = false,
                updatedAt = if (currentStatus == OrderStatus.DELIVERED) updatedAt else null
            )
        )
    }

    return steps
}

fun View.show() {
    visibility = View.VISIBLE
}
fun View.hide() {
    visibility = View.GONE
}

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return this.matches(emailRegex)
}

fun Context.showVerificationDialog(
    title: String,
    message: String,
    iconRes: Int
) {
    val binding = DialogVerificationBinding.inflate(LayoutInflater.from(this))
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setCanceledOnTouchOutside(false)

    dialog.window?.apply {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val width = (resources.displayMetrics.widthPixels * 0.8).toInt() // 80% chiều rộng
        setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        setGravity(Gravity.CENTER)
    }

    binding.tvTitle.text = title
    binding.tvMessage.text = message
    binding.ivStatusIcon.setImageResource(iconRes)

    dialog.show()

    // Auto dismiss after 1.5s
    binding.root.postDelayed({
        dialog.dismiss()
    }, 1500)
}

fun EditText.textChangesFlow() = callbackFlow<String> {
    val listener = addTextChangedListener { text ->
        trySend(text?.toString() ?: "")
    }
    awaitClose { removeTextChangedListener(listener) }
}

fun ImageView.loadUrlFull(url: String) {
    this.load(url) {
        size(Size.ORIGINAL) // tải ảnh kích thước gốc
        crossfade(true)
        placeholder(R.drawable.placeholder)
        error(R.drawable.placeholder)
    }
}

