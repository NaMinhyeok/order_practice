package gc.cafe.api.service.order

import gc.cafe.api.service.order.request.OrderCreateServiceRequest
import gc.cafe.api.service.order.response.OrderResponse
import gc.cafe.domain.order.Order
import gc.cafe.domain.order.OrderRepository
import gc.cafe.domain.order.OrderStatus
import gc.cafe.domain.orderproduct.OrderProduct
import gc.cafe.domain.orderproduct.OrderProduct.Companion.create
import gc.cafe.domain.orderproduct.OrderProductRepository
import gc.cafe.domain.product.Product
import gc.cafe.domain.product.ProductRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer


@Transactional
@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderProductRepository: OrderProductRepository
) : OrderService {
    override fun createOrder(request: OrderCreateServiceRequest): OrderResponse {
        val order = Order.create(request.email, request.address, request.postcode)

        val savedOrder = orderRepository.save(order)

        val productIds: Set<Long> = request.orderProductQuantityMap.keys
        val products = productRepository.findAllById(productIds)

        require(productIds.size == products.size) { "주문 상품 id 중 존재하지 않는 상품이 존재합니다." }

        val orderProducts = products.stream()
            .map { product: Product ->
                request.orderProductQuantityMap[product.id]?.let {
                    create(
                        savedOrder,
                        product,
                        it
                    )
                }
            }
            .toList()

        orderProductRepository.saveAll(orderProducts)

        return OrderResponse.of(savedOrder)
    }


    @Transactional(readOnly = true)
    override fun getOrder(id: Long): OrderResponse {
        val order = orderRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 주문 id : " + id + "를 가진 주문이 존재하지 않습니다.") }
        return OrderResponse.of(order)
    }

    override fun getOrdersByEmail(email: String): List<OrderResponse> {
        val orders = orderRepository.findByEmail(email)

        return orders.map { OrderResponse.of(it) }
    }

    @Async("threadPoolTaskExecutor")
    @Scheduled(cron = "0 0 14 * * *")
    fun sendOrder() {
        val orders = orderRepository.findByOrderStatus(OrderStatus.ORDERED)

        orders.forEach(Consumer { order: Order -> order.updateStatus(OrderStatus.DELIVERING) })
    }
}
