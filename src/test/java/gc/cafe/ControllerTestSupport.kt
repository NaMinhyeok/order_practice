package gc.cafe

import com.fasterxml.jackson.databind.ObjectMapper
import gc.cafe.api.controller.order.OrderController
import gc.cafe.api.controller.product.ProductController
import gc.cafe.api.service.order.OrderService
import gc.cafe.api.service.product.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(
    controllers = [ProductController::class, OrderController::class
    ]
)
abstract class ControllerTestSupport {
    @JvmField
    @Autowired
    protected var mockMvc: MockMvc? = null

    @JvmField
    @Autowired
    protected var objectMapper: ObjectMapper? = null

    @JvmField
    @MockBean
    protected var productService: ProductService? = null

    @JvmField
    @MockBean
    protected var orderService: OrderService? = null
}
