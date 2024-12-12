package gc.cafe.api.service.order.response

import gc.cafe.domain.order.Order
import gc.cafe.domain.order.OrderStatus
import gc.cafe.domain.orderproduct.OrderProduct
import java.util.stream.Stream

data class OrderResponse(
    val id: Long,
    val email: String,
    val address: String,
    val postcode: String,
    val orderStatus: OrderStatus,
    val orderDetails: List<OrderDetailResponse>
) {
    companion object {
        fun of(order: Order): OrderResponse {
            return OrderResponse(
                order.id,
                order.email,
                order.address.address,
                order.address.postcode,
                order.orderStatus,
                getOrderDetailResponseStream(order).toList()
            )
        }

        private fun getOrderDetailResponseStream(order: Order): Stream<OrderDetailResponse> {
            return order.orderProducts.stream()
                .map { orderProduct: OrderProduct ->
                    OrderDetailResponse(
                        orderProduct.product.category,
                        orderProduct.product.price,
                        orderProduct.quantity
                    )
                }
        }
    }
}
