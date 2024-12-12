package gc.cafe.api.service.order.request

import gc.cafe.api.controller.order.request.OrderProductQuantity

data class OrderCreateServiceRequest(
    val email: String,
    val address: String,
    val postcode: String,
    private var orderProductQuantity: List<OrderProductQuantity>
) {
    val orderProductQuantityMap: Map<Long, Int> = orderProductQuantity.associate {
        it.productId to it.quantity
    } as Map<Long, Int>
}
