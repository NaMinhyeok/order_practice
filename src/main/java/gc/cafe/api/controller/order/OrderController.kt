package gc.cafe.api.controller.order

import gc.cafe.api.ApiResponse
import gc.cafe.api.controller.order.request.OrderCreateRequest
import gc.cafe.api.service.order.OrderService
import gc.cafe.api.service.order.response.OrderResponse
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(@RequestBody request: @Valid OrderCreateRequest): ApiResponse<OrderResponse> {
        return ApiResponse.created(orderService.createOrder(request.toServiceRequest()))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ApiResponse<OrderResponse> {
        return ApiResponse.ok(orderService.getOrder(id))
    }

    @GetMapping
    fun getOrdersByEmail(@RequestParam email: String): ApiResponse<List<OrderResponse>> {
        return ApiResponse.ok(orderService.getOrdersByEmail(email))
    }
}
