package gc.cafe.api.service.order

import gc.cafe.api.service.order.request.OrderCreateServiceRequest
import gc.cafe.api.service.order.response.OrderResponse

interface OrderService {
    fun createOrder(request: OrderCreateServiceRequest): OrderResponse

    fun getOrder(id: Long): OrderResponse

    fun getOrdersByEmail(email: String): List<OrderResponse>
}
