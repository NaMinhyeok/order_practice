package gc.cafe.api.service.order.response

data class OrderDetailResponse(
    val category: String,
    val price: Long,
    val quantity: Int
)
